package org.wlpiaoyi.framework.forwarding.utils.socket.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.utils.ValueUtils;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-19 12:41:18</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class BufferCaches {

    @Getter
    private final byte[] buffers = new byte[ForwardUtils.MAX_CACHE_SIZE];

    private int bufferOff = -1;
    private int bufferLen = -1;

    public synchronized int loadIfNeed(byte[] bytes, int off,  int len) {
        if(len <= 0) return -1;
        if(off < 0 || off >= len) {
            throw new RuntimeException("BufferCaches.loadIfNeed.off < 0 || off >= len");
        }
        if(this.bufferOff == -1 || this.bufferLen == -1){
            int chunkAvail = len - off;
            if (chunkAvail < 4) {
                log.warn("[fw-frame] header-split-risk chunkAvail={}B (need 4 for wire length) off={} totalReadCall={} hex={} — TCP 拆包时此处会误解析长度，表现为卡死或巨帧",
                        chunkAvail, off, len, ForwardingLog.hexPreview(bytes, off, chunkAvail, 16));
            }
            this.bufferOff = 0;
            this.bufferLen = (int) ValueUtils.byteToLong(bytes, off, 4);
            if (log.isDebugEnabled()) {
                log.debug("[fw-frame] new-frame declaredWireTotal={} maxBuffer={}B chunkAvail={} first4hex={}",
                        this.bufferLen, ForwardUtils.MAX_CACHE_SIZE, chunkAvail,
                        chunkAvail >= 4 ? ForwardingLog.hexPreview(bytes, off, 4, 4) : ForwardingLog.hexPreview(bytes, off, chunkAvail, chunkAvail));
            }
            if(this.bufferLen > ForwardUtils.MAX_CACHE_SIZE)
                throw new RuntimeException("BufferCaches.loadIfNeed.bufferLen > MAX_CACHE_SIZE");
            if (log.isTraceEnabled()) {
                log.trace("[fw-frame] assemble start declaredTotal={}B", this.bufferLen);
            }
        }
        int cOff = -1;
        for (int i = off; i < len; i++){
            this.buffers[this.bufferOff++] = bytes[i];
            if(this.bufferOff >= this.bufferLen){
                cOff = i + 1;
                if (log.isDebugEnabled()) {
                    log.debug("[fw-frame] frame-complete copied={}B wireDeclared={}B", this.bufferOff, this.bufferLen);
                }
                if (log.isTraceEnabled()) {
                    log.trace("[fw-frame] assemble done");
                }
                break;
            }
        }
        if (cOff < 0 && this.bufferLen > 0 && log.isDebugEnabled()) {
            log.debug("[fw-frame] partial have={}/{}B (await more TCP)", this.bufferOff, this.bufferLen);
        }
        return cOff == len ? 0 : cOff;
    }

    public synchronized void init(){
        this.bufferOff = -1;
        this.bufferLen = -1;
    }
}
