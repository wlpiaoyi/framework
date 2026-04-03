package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.ResponseMessage;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.IWriter;

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
    private final byte[] bufferCaches = new byte[ForwardUtils.MAX_CACHE_SIZE];

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
        // 建立连接阶段/网络空读时可能会触发 readLen=0。
        // 这种空包不应当被封装成消息转发，否则会造成多余连接/协议错位。
        if (readLen <= 0) return 0;
        this.messageId++;
        if (this.messageId < 0) this.messageId = 1;
        ResponseMessage message = new ResponseMessage(this.messageId);
        ForwardingLog.traceResponseChunk(log, "response.resp", clientId, message.getId(), readLen,
                this.serverWriter.getServerHost(), this.serverWriter.getServerPort());
        byte[] data = new byte[readLen];
        System.arraycopy(readBytes, 0, data, 0, readLen);
        log.debug("response.return clientId={} messageId={} chunkLen={} peer={}:{}", clientId, message.getId(), readLen,
                this.serverWriter.getServerHost(), this.serverWriter.getServerPort());
        ForwardingLog.tracePlain(log, "response.resp.fromTarget", clientId, message.getId(),
                this.serverWriter.getServerHost(), this.serverWriter.getServerPort(), data);
        message.setData(SecurityUtils.getSecurity().encrypt(data, 0, data.length));
        ForwardingLog.traceCipher(log, "response.resp.cipher", clientId, message.getId(),
                this.serverWriter.getServerHost(), this.serverWriter.getServerPort(), message.getData());
        if (!message.check()) throw new RuntimeException("message check error clientId=" + clientId + " messageId=" + message.getId());
        message.toBytes(this.bufferCaches, 0);
        this.serverWriter.write(clientId, this.bufferCaches, message.getLen());
        return 0;
    }

    @Override
    public void error(int clientId, Exception e) {
        log.error("ClientReader.error. ClientId: {}", clientId, e);
    }

    @Override
    public void end(int clientId) {
        log.debug("ClientReader.end. ClientId: {}", clientId);
    }
}
