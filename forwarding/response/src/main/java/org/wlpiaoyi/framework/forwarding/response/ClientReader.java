package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.ResponseMessage;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.IWriter;
import org.wlpiaoyi.framework.utils.socket.SocketQuietErrors;

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
        log.info("[fw-res] target-socket-ready clientId={} targetPeer={}:{}", clientId, host, port);
        return 1;
    }

    @Override
    public synchronized int read(IWriter writer, int clientId, byte[] readBytes, int readLen) {
        this.messageId++;
        if (this.messageId < 0) this.messageId = 1;
        ResponseMessage message = new ResponseMessage(this.messageId);
        if (this.messageId < 0) this.messageId = 1;
        byte[] data = new byte[readLen];
        System.arraycopy(readBytes, 0, data, 0, readLen);
        log.debug("[fw-res] target->hub plain clientId={} msgId={} rawLen={} previewHex={}",
                clientId, message.getId(), readLen,
                ForwardingLog.hexPreview(readBytes, 0, readLen, ForwardingLog.DEFAULT_HEX_PREVIEW_BYTES));
        message.setData(SecurityUtils.getSecurity().encrypt(data, 0, data.length));
        if (!message.check()) throw new RuntimeException("message check error！clientId:" + clientId);
        message.toBytes(this.bufferCaches, 0);
        this.serverWriter.write(clientId, this.bufferCaches, message.getLen());
        log.debug("[fw-res] target->hub wire clientId={} msgId={} frameLen={} encPayloadLen={} wireHeadHex={} encHex={}",
                clientId, message.getId(), message.getLen(), message.getData().length,
                ForwardingLog.hexPreview(this.bufferCaches, 0, Math.min(message.getLen(), this.bufferCaches.length), 16),
                ForwardingLog.hexPreview(message.getData()));
        return 0;
    }

    @Override
    public void error(int clientId, Exception e) {
        if (SocketQuietErrors.isBenignClose(e)) {
            log.debug("[fw-res] target-read-closed clientId={} ({})", clientId, e.toString());
        } else {
            log.error("[fw-res] target-read-error clientId={}", clientId, e);
        }
    }

    @Override
    public void end(int clientId) {
        log.info("[fw-res] target-read-end clientId={}", clientId);
    }
}
