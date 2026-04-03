package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.RequestMessage;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.socket.IReader;
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
        log.debug("ServerReader.begin clientId={} peer={}:{}", clientId, host, port);
        return 1;
    }

    @Override
    public int read(IWriter writer, int clientId, byte[] bytes, int len) {
        // TCP 分片/建立连接阶段可能会触发一次 readLen=0 的调用，
        // 这种“空包”不应当构造并转发消息，否则容易造成不必要的连接创建与协议错位。
        if (len <= 0) return 0;
//        log.info("ServerReader.read. ClientId: {}, ReadLen: {}", clientId, readLen);
        this.messageId ++;
        if(this.messageId < 0) this.messageId = 1;
        RequestMessage message = new RequestMessage(this.messageId);
        {
            String[] addrs = ForwardUtils.getRequestServerAddress(this.serverPort).split(":");
            message.setHost(addrs[0]);
            message.setPort(Integer.parseInt(addrs[1]));
        }
        byte[] data = new byte[len];
        System.arraycopy(bytes, 0, data, 0, len);
        message.setData(SecurityUtils.getSecurity().encrypt(data, 0, data.length));
        SocketClient client;
        {
            var addrs = ForwardUtils.getResponseServerAddress().split(":");
            String respHost = addrs[0];
            int respPort = Integer.parseInt(addrs[1]);
            client = this.getClient(clientId, respHost, respPort, writer);
        }
        int bLen = message.toBytes(this.bufferCaches, 0);
        client.getWriter().write(clientId, this.bufferCaches, bLen);
        log.debug("request.upstream clientId={} messageId={} chunkLen={} target={}:{}", clientId, this.messageId, len,
                message.getHost(), message.getPort());
        ForwardingLog.traceChunk(log, "request.upstream", clientId, this.messageId, len, message.getHost(), message.getPort());
        return 0;
    }

    @Override
    public void error(int clientId, Exception e) {
        log.error("ServerReader.error. ClientId: {}", clientId, e);
        synchronized (this.clientContentDict){
            this.clientContentDict.forEach((k, v) -> {
                v.disConnect();
            });
            this.clientContentDict.clear();
        }
    }

    @Override
    public void end(int clientId) {
        log.debug("ServerReader.end clientId={}", clientId);
        synchronized (this.clientContentDict){
            this.clientContentDict.forEach((k, v) -> {
                v.disConnect();
            });
            this.clientContentDict.clear();
        }
    }

    public SocketClient getClient(int clientId, String respHost, int respPort, IWriter serverWriter) {
        return this.clientContentDict.computeIfAbsent(clientId + ":" + respHost + ":" + respPort, k -> {
            int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut",60);
            SocketClient socketClient = new SocketClient(respHost, respPort, clientId, timeOut, ForwardUtils.BUFF_CACHE_SIZE, new ClientReader(serverWriter));
            try {
                socketClient.connect();
                socketClient.asyncRun(null);
            } catch (IOException e) {
                log.error("ServerReader.getClient. ClientId: {} connect fail", clientId, e);
            }
            return socketClient;
        });
    }

}
