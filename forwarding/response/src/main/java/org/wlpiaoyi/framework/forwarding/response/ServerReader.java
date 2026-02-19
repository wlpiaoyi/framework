package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.BufferCaches;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.RequestMessage;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.ResponseMessage;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
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
        log.info("ServerReader.begin. ClientId: {}, Host: {}, Port: {}", clientId, host, port);
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
        log.info("ServerReader.end. ClientId: {}", clientId);
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

//    protected synchronized SocketClient getClient(int clientId, RequestMessage message, IWriter serverWriter){
//        return this.clientContentDict.computeIfAbsent(message.getHost() + ":" + message.getPort(), k -> {
//            int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut",60);
//            SocketClient socketClient = new SocketClient(message.getHost(), message.getPort(), timeOut, clientId, ForwardUtils.BUFF_CACHE_SIZE,
//            new ClientReader(serverWriter));
//            try {
//                socketClient.connect();
//                socketClient.asyncRun(null);
//            } catch (IOException e) {
//                log.error("ServerReader.getClient. ClientId: {} connect fail", clientId, e);
//            }
//            return socketClient;
//        });
//    }

    private int read(IWriter writer, int clientId, byte[] bytes, int off,  int len) {
        int cOff = 0;
        try{
            this.messageId ++;
            if(this.messageId < 0) this.messageId = 1;
            cOff = this.bufferCaches.loadIfNeed(bytes, off, len);
            if(cOff == -1){
                log.debug("ServerReader.read.load.continue ClientId: {}, MessageId: {}", clientId, messageId);
                return 0;
            }
            log.debug("ServerReader.read.load.end. ClientId: {}, MessageId: {}", clientId, messageId);
            RequestMessage message = new RequestMessage(this.messageId);
            message.formatBytes(this.bufferCaches.getBuffers(), 0);
            if (!message.check()) throw new RuntimeException("message check error！clientId:" + clientId);
            // use server forwarding data
            var client = this.getClient(clientId, message.getHost(), message.getPort(), writer);
            if (message.getData() != null && message.getData().length > 0){
                var data = SecurityUtils.getSecurity().decrypt(message.getData(), 0, message.getData().length);
                client.getWriter().write(clientId, data, data.length);
                System.out.println("R=================================================>:eMessage:");
                System.out.println(new String(message.getData()));
                System.out.println("R=================================================<:eMessage");
                System.out.println("R=================================================>:dMessage:");
                System.out.println(new String(data));
                System.out.println("R=================================================<:dMessage");
            }
            return cOff;
        }finally {
            if (cOff != -1) this.bufferCaches.init();
        }
    }
}
