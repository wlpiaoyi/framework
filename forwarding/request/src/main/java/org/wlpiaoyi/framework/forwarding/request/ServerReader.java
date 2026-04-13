package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.RequestMessage;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.SocketQuietErrors;
import org.wlpiaoyi.framework.utils.socket.IWriter;
import org.wlpiaoyi.framework.utils.socket.client.SocketClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-16 14:53:53</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class ServerReader implements IReader {

    private final Map<String, SocketClient> clientContentDict = new ConcurrentHashMap<>();

    private final byte[] bufferCaches = new byte[ForwardUtils.MAX_CACHE_SIZE];

    private int messageId = 0;

    private final int serverPort;

    public ServerReader(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public int begin(int clientId, String host, int port) {
        log.info("[fw-req] peer-open listenPort={} clientId={} peer={}:{}", serverPort, clientId, host, port);
        return 1;
    }

    @Override
    public int read(IWriter writer, int clientId, byte[] bytes, int len) {
        this.messageId ++;
        if(this.messageId < 0) this.messageId = 1;
        RequestMessage message = new RequestMessage(this.messageId);
        {
            String[] addrs = ForwardUtils.getRequestServerAddress(this.serverPort).split(":");
            message.setHost(addrs[0]);
            message.setPort(Integer.parseInt(addrs[1]));
        }
        if(len > 0){
            byte[] data = new byte[len];
            System.arraycopy(bytes, 0, data, 0, len);
            message.setData(SecurityUtils.getSecurity().encrypt(data, 0, data.length));
        }else{
            message.setData(null);
        }
        var hub = ForwardUtils.getResponseServerAddress().split(":");
        String respHost = hub[0];
        int respPort = Integer.parseInt(hub[1]);
        SocketClient client = this.getClient(clientId, respHost, respPort, writer);
        if (client == null) {
            log.warn("[fw-req] hub unavailable, drop ingress listenPort={} clientId={} hub={}:{}", serverPort, clientId, respHost, respPort);
            return 0;
        }
        var hubWriter = client.getWriter();
        if (hubWriter == null) {
            log.warn("[fw-req] hub writer missing listenPort={} clientId={}", serverPort, clientId);
            return 0;
        }
        int bLen = message.toBytes(this.bufferCaches, 0);
        hubWriter.write(clientId, this.bufferCaches, bLen);
        int encLen = message.getData() == null ? 0 : message.getData().length;
        log.debug("[fw-req] client->hub listenPort={} clientId={} msgId={} plainLen={} encPayloadLen={} target={}:{} hub={}:{} wireFrameLen={} encHex={}",
                serverPort, clientId, message.getId(), len, encLen, message.getHost(), message.getPort(), respHost, respPort, bLen,
                encLen > 0 ? ForwardingLog.hexPreview(message.getData()) : "-");
        return 0;
    }

    @Override
    public void error(int clientId, Exception e) {
        if (SocketQuietErrors.isBenignClose(e)) {
            log.debug("[fw-req] peer-tcp-end listenPort={} clientId={} ({})", serverPort, clientId, e.toString());
        } else {
            log.error("[fw-req] peer-error listenPort={} clientId={}", serverPort, clientId, e);
        }
        synchronized (this.clientContentDict){
            this.clientContentDict.forEach((k, v) -> {
                v.disConnect();
            });
            this.clientContentDict.clear();
        }
    }

    @Override
    public void end(int clientId) {
        log.info("[fw-req] peer-close listenPort={} clientId={}", serverPort, clientId);
        synchronized (this.clientContentDict){
            this.clientContentDict.forEach((k, v) -> {
                v.disConnect();
            });
            this.clientContentDict.clear();
        }
    }

    public SocketClient getClient(int clientId, String respHost, int respPort, IWriter serverWriter) {
        String key = clientId + ":" + respHost + ":" + respPort;
        return this.clientContentDict.compute(key, (k, v) -> {
            if (v != null) {
                return v;
            }
            int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut", 60);
            SocketClient socketClient = new SocketClient(respHost, respPort, timeOut, clientId, ForwardUtils.BUFF_CACHE_SIZE, new ClientReader(serverWriter));
            try {
                socketClient.connect();
                socketClient.asyncRun(null);
                log.info("[fw-req] hub-channel-open clientId={} hub={}:{}", clientId, respHost, respPort);
                return socketClient;
            } catch (IOException e) {
                log.error("[fw-req] hub-connect-fail clientId={} hub={}:{}", clientId, respHost, respPort, e);
                return null;
            }
        });
    }

}
