package org.wlpiaoyi.framework.forwarding.utils.socket.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.Arrays;

/**
 * TCP 粘包/拆包缓冲器。
 * <p>
 * 解决 TCP 流式传输中的粘包与拆包问题：
 * <ol>
 *   <li>首次接收时，读取前 4 字节作为消息总长度（大端序）。</li>
 *   <li>将后续字节累积到内部缓冲区，直到凑齐完整一帧。</li>
 *   <li>帧完整后返回下一帧起始偏移；帧不完整返回 -1，等待更多数据。</li>
 * </ol>
 * </p>
 * <p>
 * 当日志开启时，提供详细的 TRACE/DEBUG 级别组包日志（含 hex 预览），便于排查卡死或巨帧问题。
 * </p>
 */
@Slf4j
public class BufferCaches {

    /** 内部缓冲区，上限由 {@link ForwardUtils#MAX_CACHE_SIZE} 决定 */
    @Getter
    private final byte[] buffers = new byte[ForwardUtils.MAX_CACHE_SIZE];

    /** 当前已写入缓冲区的字节偏移 */
    private int bufferOff = -1;
    /** 当前帧声明的总长度（从前 4 字节解析） */
    private int bufferLen = -1;

    /**
     * 将外部字节数组中的数据拷贝到内部缓冲区，并在帧完整时返回下一帧起始偏移。
     * <p>
     * 调用逻辑：
     * <ul>
     *   <li>若 {@code bufferOff == -1}，表示新帧开始，先读取 4 字节长度头。</li>
     *   <li>持续拷贝数据到 {@link #buffers}，直到 {@code bufferOff >= bufferLen}。</li>
     *   <li>若帧完整，返回输入数组中的下一帧起始索引（可能为 0 表示刚好读完）。</li>
     *   <li>若帧不完整，返回 -1。</li>
     * </ul>
     * </p>
     *
     * @param bytes 外部读取到的字节数组
     * @param off   本次处理的起始偏移（必须在 [0, len) 范围内）
     * @param len   外部数组本次读取的总长度
     * @return 下一帧在输入数组中的起始偏移；若当前数据还未读完需要继续read 返回-1；刚好读完 返回0; 否则当前数据流offset下一帧数据
     */
    public synchronized int loadIfNeed(byte[] bytes, int off, int len) {
        if (len <= 0) return -1;
        if (off < 0 || off >= len) {
            throw new RuntimeException("BufferCaches.loadIfNeed.off < 0 || off >= len");
        }
        // 新帧开始：解析 4 字节长度头
        if (this.bufferOff == -1 && this.bufferLen == -1) {
            this.bufferOff = 0;
            int chunkAvail = len - off;
            if (chunkAvail < 10) {
                log.warn("[fw-frame] header-split-risk chunkAvail={}B (need 4 for wire length) off={} totalReadCall={} hex={} — TCP 拆包时此处会误解析长度，表现为卡死或巨帧",
                        chunkAvail, off, len, ForwardingLog.hexPreview(bytes, off, chunkAvail, 16));
                for (int i = off; i < len; i++) {
                    this.buffers[this.bufferOff++] = bytes[i];
                }
                return -1;
            }else{
                this.bufferLen = (int) ValueUtils.byteToLong(bytes, off, 4);
                if (log.isDebugEnabled()) {
                    log.debug("[fw-frame] new-frame declaredWireTotal={} maxBuffer={}B chunkAvail={} first4hex={}",
                            this.bufferLen, ForwardUtils.MAX_CACHE_SIZE, chunkAvail,
                            ForwardingLog.hexPreview(bytes, off, 10, 10));
                }
                if (this.bufferLen > ForwardUtils.MAX_CACHE_SIZE)
                    throw new RuntimeException("BufferCaches.loadIfNeed.bufferLen > MAX_CACHE_SIZE");
                if (log.isTraceEnabled()) {
                    log.trace("[fw-frame] assemble start declaredTotal={}B", this.bufferLen);
                }
            }
        }
        if (this.bufferLen == -1){
            int coff = off;
            for (int i = coff; i < 10 - this.bufferOff; i++) {
                this.buffers[this.bufferOff++] = bytes[i];
                off ++;
            }
            this.bufferLen = (int) ValueUtils.byteToLong(this.buffers, 0, 4);
        }

        int cOff = -1;
        for (int i = off; i < len; i++) {
            this.buffers[this.bufferOff++] = bytes[i];
            if (this.bufferOff >= this.bufferLen) {
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
        return cOff >= len ? 0 : cOff;
    }

    /**
     * 重置缓冲区状态，准备接收下一帧。
     */
    public synchronized void reset() {
        this.bufferOff = -1;
        this.bufferLen = -1;
        Arrays.fill(this.buffers, (byte) 0);
    }
}
