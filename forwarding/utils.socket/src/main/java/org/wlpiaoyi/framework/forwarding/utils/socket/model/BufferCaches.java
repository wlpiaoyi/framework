package org.wlpiaoyi.framework.forwarding.utils.socket.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
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
            this.bufferOff = 0;
            this.bufferLen = (int) ValueUtils.byteToLong(bytes, off, 4);
            if(this.bufferLen > ForwardUtils.MAX_CACHE_SIZE)
                throw new RuntimeException("BufferCaches.loadIfNeed.bufferLen > MAX_CACHE_SIZE");
//            log.debug("BufferCaches.loadIfNeed.load. bufferLen: {}", this.bufferLen);
        }
        int cOff = -1;
        for (int i = off; i < len; i++){
            this.buffers[this.bufferOff++] = bytes[i];
            if(this.bufferOff >= this.bufferLen){
                cOff = i + 1;
                break;
            }
        }
//        log.debug("BufferCaches.loadIfNeed.read. bufferOff: {}, bufferLen: {}", this.bufferOff, this.bufferLen);
        return cOff == len ? 0 : cOff;
    }

    public synchronized void init(){
        this.bufferOff = -1;
        this.bufferLen = 0;
    }
}
