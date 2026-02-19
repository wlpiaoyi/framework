package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.BufferCaches;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.RequestMessage;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.ResponseMessage;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.IWriter;
import org.wlpiaoyi.framework.utils.socket.client.SocketClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-17 13:04:47</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class ClientReader implements IReader {

    private final IWriter serverWriter;
    private int messageId = 0;

    private final BufferCaches bufferCaches = new BufferCaches();


    public ClientReader(IWriter serverWriter) {
        this.serverWriter = serverWriter;
    }

    @Override
    public int begin(int clientId, String host, int port) {
        log.debug("ClientReader.begin. ClientId: {}, Host: {}, Port: {}", clientId, host, port);
        return 1;
    }

    @Override
    public synchronized int read(IWriter writer, int clientId, byte[] readBytes, int readLen) {
//        log.debug("ClientReader.read. ClientId: {}, ReadLen: {}", clientId, readLen);
        int off = this.read(writer, clientId, readBytes, 0, readLen);
        while (off > 0) {
           off = this.read(writer, clientId, readBytes, off, readLen);
        }
        return 0;
    }

    @Override
    public void error(int clientId, Exception e) {
        log.error("ClientReader.error. ClientId: {}", clientId, e);
    }

    @Override
    public void end(int clientId) {
        log.info("ClientReader.end. ClientId: {}", clientId);
    }

    private int read(IWriter writer, int clientId, byte[] bytes, int off,  int len) {
        int cOff = 0;
        try{
            this.messageId ++;
            if(this.messageId < 0) this.messageId = 1;
            cOff = this.bufferCaches.loadIfNeed(bytes, off, len);
            if(cOff == -1){
//                log.debug("ClientReader.read.load.continue ClientId: {}, MessageId: {}", clientId, messageId);
                return 0;
            }
//            log.debug("ClientReader.read.load.end. ClientId: {}, MessageId: {}", clientId, messageId);
            ResponseMessage message = new ResponseMessage(this.messageId);
            message.formatBytes(this.bufferCaches.getBuffers(), 0);
            if (!message.check()) throw new RuntimeException("message check error！clientId:" + clientId);
            // use server forwarding data
            var data = SecurityUtils.getSecurity().decrypt(message.getData(), 0, message.getData().length);
            this.serverWriter.write(clientId, data, data.length);
//            log.debug("ClientReader.read. ClientId: {} write len:{} message: {}", clientId, message.getData().length, new String(message.getData()));
            return cOff;
        }finally {
            if (cOff != -1) this.bufferCaches.init();
        }
    }
}
