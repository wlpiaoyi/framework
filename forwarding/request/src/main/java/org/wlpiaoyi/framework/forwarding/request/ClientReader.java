package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.BufferCaches;
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

    private final BufferCaches bufferCaches = new BufferCaches();


    public ClientReader(IWriter serverWriter) {
        this.serverWriter = serverWriter;
    }

    @Override
    public int begin(int clientId, String host, int port) {
        log.info("[fw-req] hub-socket-ready clientId={} hubPeer={}:{}", clientId, host, port);
        return 1;
    }

    @Override
    public synchronized int read(IWriter writer, int clientId, byte[] readBytes, int readLen) {
        int off = this.read(writer, clientId, readBytes, 0, readLen);
        while (off > 0) {
           off = this.read(writer, clientId, readBytes, off, readLen);
        }
        return 0;
    }

    @Override
    public void error(int clientId, Exception e) {
        if (SocketQuietErrors.isBenignClose(e)) {
            log.debug("[fw-req] hub-read-closed clientId={} ({})", clientId, e.toString());
        } else {
            log.error("[fw-req] hub-read-error clientId={}", clientId, e);
        }
    }

    @Override
    public void end(int clientId) {
        log.info("[fw-req] hub-read-end clientId={}", clientId);
    }


    private int read(IWriter writer, int clientId, byte[] bytes, int off,  int len) {
        int cOff = -1;
        try{
            this.messageId ++;
            if(this.messageId < 0) this.messageId = 1;
            if (log.isDebugEnabled()) {
                log.debug("[fw-req] hub-tcp-assemble clientId={} slice=[{}, {}) of readCallLen={}", clientId, off, len, len);
            }
            cOff = this.bufferCaches.loadIfNeed(bytes, off, len);
            if(cOff == -1){
                if (log.isTraceEnabled()) {
                    log.trace("[fw-req] hub->client frame-incomplete clientId={} bytesThisCall={}", clientId, len - off);
                }
                return 0;
            }
            ResponseMessage message = new ResponseMessage(this.messageId);
            message.formatBytes(this.bufferCaches.getBuffers(), 0);
            if (!message.check()) throw new RuntimeException("message check error！clientId:" + clientId);
            byte[] enc = message.getData();
            if (enc == null || enc.length == 0) {
                log.debug("[fw-req] hub->client empty-payload clientId={} wireMsgId={} wireDeclaredLen={} (no write to browser)",
                        clientId, message.getId(), message.getLen());
                return cOff;
            }
            var data = SecurityUtils.getSecurity().decrypt(enc, 0, enc.length);
            this.serverWriter.write(clientId, data, data.length);
            log.debug("[fw-req] hub->client delivered clientId={} respMsgId={} wireDeclaredLen={} encLen={} plainLen={} wireHeadHex={} plainPreviewHex={}",
                    clientId, message.getId(), message.getLen(), enc.length, data.length,
                    ForwardingLog.hexPreview(this.bufferCaches.getBuffers(), 0, Math.min(message.getLen(), this.bufferCaches.getBuffers().length), 16),
                    ForwardingLog.hexPreview(data, 0, data.length, ForwardingLog.DEFAULT_HEX_PREVIEW_BYTES));
            return cOff;
        }finally {
            if (cOff != -1) this.bufferCaches.init();
        }
    }
}
