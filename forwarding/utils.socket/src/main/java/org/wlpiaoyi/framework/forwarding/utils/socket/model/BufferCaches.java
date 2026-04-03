package org.wlpiaoyi.framework.forwarding.utils.socket.model;

import lombok.Getter;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-19 12:41:18</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class BufferCaches {

    @Getter
    private final byte[] buffers = new byte[ForwardUtils.MAX_CACHE_SIZE];

    private int bufferOff = -1;
    private int bufferLen = -1;

    public synchronized int loadIfNeed(byte[] bytes, int off,  int len) {
        if(len <= 0) return -1;
        if(off < 0 || off > len) {
            throw new RuntimeException("BufferCaches.loadIfNeed.off < 0 || off > len");
        }
        if (off == len) return -1;

        int cOff = -1;
        // State machine:
        // - bufferOff == -1 表示没有开始拼包（或上一条消息已 init()）
        // - bufferLen == -1 表示还没读够 4 字节长度头，无法确定本条消息总长度
        if (this.bufferOff == -1) {
            this.bufferOff = 0;
            this.bufferLen = -1;
        }

        for (int i = off; i < len; i++) {
            if (this.bufferOff >= this.buffers.length) {
                throw new RuntimeException("BufferCaches.loadIfNeed.buffers overflow. off=" + i + ", bufferOff=" + this.bufferOff);
            }
            this.buffers[this.bufferOff++] = bytes[i];

            // 当还不知道消息总长时：先收集 4 字节 len，再解析 bufferLen
            if (this.bufferLen == -1 && this.bufferOff == 4) {
                // Message.len 由 short 生成写入 4 字节，这里按低 16 位解析成无符号长度
                int rawLen = (int) ValueUtils.byteToLong(this.buffers, 0, 4);
                this.bufferLen = rawLen & 0xFFFF;
                if (this.bufferLen < 4 || this.bufferLen > ForwardUtils.MAX_CACHE_SIZE) {
                    throw new RuntimeException("BufferCaches.loadIfNeed.bufferLen invalid: " + this.bufferLen);
                }
            }

            if (this.bufferLen != -1 && this.bufferOff >= this.bufferLen) {
                cOff = i + 1; // 下一条消息的起始偏移
                break;
            }
        }

        // cOff == -1 表示当前数据不足以拼出完整消息
        if (cOff == -1) return -1;
        return cOff == len ? 0 : cOff;
    }

    public synchronized void init(){
        this.bufferOff = -1;
        this.bufferLen = -1;
    }
}
