package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingPortStats;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.BufferCaches;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.ResponseMessage;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.IWriter;
import org.wlpiaoyi.framework.utils.socket.SocketQuietErrors;

/**
 * request 侧用于读取 response 模块（Hub）回传数据的处理器。
 * <p>
 * 每个 {@link ServerReader#getClient} 创建的到 Hub 的 {@link org.wlpiaoyi.framework.utils.socket.client.SocketClient}
 * 都会绑定一个 {@code ClientReader} 实例。
 * 职责：
 * <ul>
 *   <li>处理 TCP 粘包/拆包：使用 {@link BufferCaches} 累积字节，直到凑齐完整一帧。</li>
 *   <li>解析 {@link ResponseMessage}，用 {@link SecurityUtils#getSecurity()} 解密数据。</li>
 *   <li>将解密后的明文通过 {@link #serverWriter} 回写给本地浏览器/客户端。</li>
 *   <li>Hub 连接结束时，级联关闭浏览器侧连接，保证生命周期对齐。</li>
 * </ul>
 * </p>
 */
@Slf4j
public class ClientReader implements IReader {

    /** 用于将解密后的数据回写给本地浏览器/客户端 */
    private final IWriter serverWriter;

    /** 当前监听的本地端口号（用于流量统计） */
    private final int listenPort;

    /** 消息序号计数器 */
    private int messageId = 0;

    /** 粘包/拆包缓冲区，负责从 TCP 流中组装完整的消息帧 */
    private final BufferCaches bufferCaches = new BufferCaches();

    public ClientReader(IWriter serverWriter, int listenPort) {
        this.serverWriter = serverWriter;
        this.listenPort = listenPort;
    }

    /**
     * Hub 连接建立时的回调。
     *
     * @param clientId 连接唯一标识
     * @param host     Hub 对端地址
     * @param port     Hub 对端端口
     * @return 固定返回 1
     */
    @Override
    public int begin(int clientId, String host, int port) {
        log.info("[fw-req] hub-socket-ready clientId={} hubPeer={}:{}", clientId, host, port);
        return 1;
    }

    /**
     * 收到 Hub 数据时的回调。
     * <p>
     * 由于 TCP 是流式协议，单次 read 可能包含多个完整消息，也可能只包含部分消息。
     * 因此使用循环调用私有 {@link #read} 方法，确保一次回调中处理所有完整帧。
     * </p>
     *
     * @param writer   Hub 侧的 writer（当前未使用）
     * @param clientId 连接唯一标识
     * @param readBytes 读取到的字节数组
     * @param readLen   本次读取到的有效长度
     * @return 固定返回 0
     */
    @Override
    public synchronized int read(IWriter writer, int clientId, byte[] readBytes, int readLen) {
        int off = this.read(writer, clientId, readBytes, 0, readLen);
        while (off > 0) {
            off = this.read(writer, clientId, readBytes, off, readLen);
        }
        return 0;
    }

    /**
     * Hub 连接发生异常时的回调。
     *
     * @param clientId 连接唯一标识
     * @param e        异常对象
     */
    @Override
    public void error(int clientId, Exception e) {
        if (SocketQuietErrors.isBenignClose(e)) {
            log.debug("[fw-req] hub-read-closed clientId={} ({})", clientId, e.toString());
        } else {
            log.error("[fw-req] hub-read-error clientId={}", clientId, e);
        }
    }

    /**
     * Hub 连接正常结束时的回调。
     * <p>
     * 通常因 response 侧目标服务器关闭连接，此时需要级联关闭浏览器侧的 TCP 连接，
     * 使本地应用能感知到连接结束。
     * </p>
     *
     * @param clientId 连接唯一标识
     */
    @Override
    public void end(int clientId) {
        log.info("[fw-req] hub-read-end clientId={} (cascade-close browser peer)", clientId);
        // Hub 已结束（通常因 response 侧目标关闭后关了 Hub）：关闭浏览器侧 TCP，与上游生命周期对齐
        this.serverWriter.closeSocket(clientId);
    }

    /**
     * 处理单帧 ResponseMessage 的核心方法。
     * <p>
     * 执行流程：
     * <ol>
     *   <li>通过 {@link BufferCaches#loadIfNeed} 检查当前字节是否凑齐完整帧。</li>
     *   <li>若帧不完整（{@code cOff == -1}），返回 0，等待下一次 read 回调。</li>
     *   <li>帧完整后，用 {@link ResponseMessage#formatBytes} 解析消息头与加密数据。</li>
     *   <li>调用 {@link SecurityUtils#getSecurity()#decrypt} 解密。</li>
     *   <li>将明文通过 {@link #serverWriter} 写回浏览器。</li>
     * </ol>
     * </p>
     *
     * @param writer   Hub 侧 writer
     * @param clientId 连接唯一标识
     * @param bytes    字节数组
     * @param off      本次处理的起始偏移量
     * @param len      数组总长度
     * @return 下一帧的起始偏移量；若当前帧不完整返回 0；处理完毕后由外层循环判断是否还有剩余数据
     */
    private int read(IWriter writer, int clientId, byte[] bytes, int off, int len) {
        int cOff = -1;
        try {
            this.messageId++;
            if (this.messageId < 0) this.messageId = 1;
            if (log.isDebugEnabled()) {
                log.debug("[fw-req] hub-tcp-assemble clientId={} slice=[{}, {}) of readCallLen={}", clientId, off, len, len);
            }

            // 粘包处理：尝试组装完整帧
            cOff = this.bufferCaches.loadIfNeed(bytes, off, len);
            if (cOff == -1) {
                if (log.isTraceEnabled()) {
                    log.trace("[fw-req] hub->client frame-incomplete clientId={} bytesThisCall={}", clientId, len - off);
                }
                return 0;
            }

            // 帧已完整，解析 ResponseMessage
            ResponseMessage message = new ResponseMessage(this.messageId);
            message.formatBytes(this.bufferCaches.getBuffers(), 0);
            if (message.checkKey()) throw new RuntimeException("message check error！clientId:" + clientId);

            byte[] enc = message.getData();
            if (enc == null || enc.length == 0) {
                log.debug("[fw-req] hub->client empty-payload clientId={} wireMsgId={} wireDeclaredLen={} (no write to browser)",
                        clientId, message.getId(), message.getLen());
                return cOff;
            }

            // 解密并回写给浏览器
            var data = SecurityUtils.getSecurity().decrypt(enc, 0, enc.length);
            ForwardingPortStats.addBytesDown(listenPort, data.length);
            this.serverWriter.write(clientId, data, data.length);

            log.debug("[fw-req] hub->client delivered clientId={} respMsgId={} wireDeclaredLen={} encLen={} plainLen={} wireHeadHex={} plainPreviewHex={}",
                    clientId, message.getId(), message.getLen(), enc.length, data.length,
                    ForwardingLog.hexPreview(this.bufferCaches.getBuffers(), 0, Math.min(message.getLen(), this.bufferCaches.getBuffers().length), 16),
                    ForwardingLog.hexPreview(data, 0, data.length, ForwardingLog.DEFAULT_HEX_PREVIEW_BYTES));
            return cOff;
        } finally {
            // 若已处理完一帧，重置缓冲区准备接收下一帧
            if (cOff != -1) this.bufferCaches.reset();
        }
    }
}
