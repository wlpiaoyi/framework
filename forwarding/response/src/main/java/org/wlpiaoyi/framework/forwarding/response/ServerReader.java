package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.BufferCaches;
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
    private int messageId = 0;
    private final BufferCaches bufferCaches = new BufferCaches();

    @Override
    public int begin(int clientId, String host, int port) {
        log.debug("ServerReader.begin clientId={} peer={}:{}", clientId, host, port);
        return 1;
    }

    @Override
    public int read(IWriter writer, int clientId, byte[] readBytes, int readLen) {
        int off = this.read(writer, clientId, readBytes, 0, readLen);
        while (off > 0) {
            off = this.read(writer, clientId, readBytes, off, readLen);
        }
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

    private SocketClient getClient(int clientId, String respHost, int respPort, IWriter serverWriter) {
        return this.clientContentDict.computeIfAbsent(clientId + ":" + respHost + ":" + respPort, k -> {
            int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut",60);
            SocketClient socketClient = new SocketClient(respHost, respPort, timeOut, clientId, ForwardUtils.BUFF_CACHE_SIZE,
                    new ClientReader(serverWriter));
            try {
                socketClient.connect();
                socketClient.asyncRun(null);
            } catch (IOException e) {
                log.error("ServerReader.getClient. ClientId: {} connect fail", clientId, e);
            }
            return socketClient;
        });
    }

    private int read(IWriter writer, int clientId, byte[] bytes, int off,  int len) {
        int cOff = 0;
        try{
            this.messageId ++;
            if(this.messageId < 0) this.messageId = 1;
            cOff = this.bufferCaches.loadIfNeed(bytes, off, len);
            if(cOff == -1){
                log.trace("ServerReader framing pending clientId={} messageId={}", clientId, messageId);
                return 0;
            }
            log.trace("ServerReader frame complete clientId={} messageId={}", clientId, messageId);
            RequestMessage message = new RequestMessage(this.messageId);
            message.formatBytes(this.bufferCaches.getBuffers(), 0);
            if (!message.check()) throw new RuntimeException("message check error clientId=" + clientId + " messageId=" + message.getId());
            // use server forwarding data
            var client = this.getClient(clientId, message.getHost(), message.getPort(), writer);
            if (message.getData() != null && message.getData().length > 0){
                log.debug("response.forward clientId={} messageId={} target={}:{} cipherLen={}", clientId, message.getId(),
                        message.getHost(), message.getPort(), message.getData().length);
                ForwardingLog.traceCipher(log, "response.req", clientId, message.getId(), message.getHost(), message.getPort(), message.getData());
                var data = SecurityUtils.getSecurity().decrypt(message.getData(), 0, message.getData().length);
                ForwardingLog.tracePlain(log, "response.req.dec", clientId, message.getId(), message.getHost(), message.getPort(), data);
                client.getWriter().write(clientId, data, data.length);
            }
            return cOff;
        }finally {
            if (cOff != -1) this.bufferCaches.init();
        }
    }
}
