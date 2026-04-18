package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.ResponseMessage;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.IWriter;
import org.wlpiaoyi.framework.utils.socket.SocketQuietErrors;

/**
 * response 侧用于读取真实目标服务器响应数据的处理器。
 * <p>
 * 每个 {@link ServerReader#getClient} 创建的到目标的 {@link org.wlpiaoyi.framework.utils.socket.client.SocketClient}
 * 都会绑定一个 {@code ClientReader} 实例。
 * 职责：
 * <ul>
 *   <li>读取目标服务器返回的原始字节。</li>
 *   <li>使用 {@link SecurityUtils#getSecurity()} 加密数据，封装为 {@link ResponseMessage}。</li>
 *   <li>通过 {@link #serverWriter} 将加密后的消息写回给 request 模块（Hub）。</li>
 *   <li>目标连接结束时，级联关闭 Hub 会话，保证 request 侧能感知并关闭浏览器连接。</li>
 * </ul>
 * </p>
 */
@Slf4j
public class ClientReader implements IReader {

    /** 用于将加密后的响应写回 Hub（最终到达 request 侧） */
    private final IWriter serverWriter;

    /** 消息序号计数器 */
    private int messageId = 0;

    /** 复用的字节缓存，用于序列化 ResponseMessage */
    private final byte[] bufferCaches = new byte[ForwardUtils.MAX_CACHE_SIZE];

    public ClientReader(IWriter serverWriter) {
        this.serverWriter = serverWriter;
    }

    /**
     * 目标连接建立时的回调。
     *
     * @param clientId 连接唯一标识
     * @param host     目标对端地址
     * @param port     目标对端端口
     * @return 固定返回 1
     */
    @Override
    public int begin(int clientId, String host, int port) {
        log.info("[fw-res] target-socket-ready clientId={} targetPeer={}:{}", clientId, host, port);
        return 1;
    }

    /**
     * 收到目标服务器响应数据时的回调。
     * <p>
     * 将原始数据加密后封装为 {@link ResponseMessage}，通过 {@link #serverWriter} 写回 Hub。
     * 使用 {@code synchronized} 保证同一客户端的消息顺序写入。
     * </p>
     *
     * @param writer   目标侧 writer（当前未使用）
     * @param clientId 连接唯一标识
     * @param readBytes 读取到的字节数组
     * @param readLen   本次读取有效长度
     * @return 固定返回 0
     */
    @Override
    public synchronized int read(IWriter writer, int clientId, byte[] readBytes, int readLen) {
        this.messageId++;
        if (this.messageId < 0) this.messageId = 1;

        ResponseMessage message = new ResponseMessage(this.messageId);
        if (this.messageId < 0) this.messageId = 1;

        // 复制原始数据（避免污染底层缓冲区）
        byte[] data = new byte[readLen];
        System.arraycopy(readBytes, 0, data, 0, readLen);

        log.debug("[fw-res] target->hub plain clientId={} msgId={} rawLen={} previewHex={}",
                clientId, message.getId(), readLen,
                ForwardingLog.hexPreview(readBytes, 0, readLen, ForwardingLog.DEFAULT_HEX_PREVIEW_BYTES));

        // RSA+AES 混合加密
        message.setData(SecurityUtils.getSecurity().encrypt(data, 0, data.length));
        if (!message.check()) throw new RuntimeException("message check error！clientId:" + clientId);

        // 序列化并写回 Hub
        message.toBytes(this.bufferCaches, 0);
        this.serverWriter.write(clientId, this.bufferCaches, message.getLen());

        log.debug("[fw-res] target->hub wire clientId={} msgId={} frameLen={} encPayloadLen={} wireHeadHex={} encHex={}",
                clientId, message.getId(), message.getLen(), message.getData().length,
                ForwardingLog.hexPreview(this.bufferCaches, 0, Math.min(message.getLen(), this.bufferCaches.length), 16),
                ForwardingLog.hexPreview(message.getData()));
        return 0;
    }

    /**
     * 目标连接发生异常时的回调。
     *
     * @param clientId 连接唯一标识
     * @param e        异常对象
     */
    @Override
    public void error(int clientId, Exception e) {
        if (SocketQuietErrors.isBenignClose(e)) {
            log.debug("[fw-res] target-read-closed clientId={} ({})", clientId, e.toString());
        } else {
            log.error("[fw-res] target-read-error clientId={}", clientId, e);
        }
    }

    /**
     * 目标连接正常结束时的回调。
     * <p>
     * 关键级联关闭逻辑：目标服务器已 FIN 时，主动关闭本侧与 request 的 Hub 会话 TCP 连接，
     * 使 request 上读取 Hub 的线程结束，进而关闭浏览器/源连接。
     * </p>
     *
     * @param clientId 连接唯一标识
     */
    @Override
    public void end(int clientId) {
        log.info("[fw-res] target-read-end clientId={} (cascade-close hub toward request)", clientId);
        // 转发目标已 FIN：关闭本侧 Hub 会话 TCP，使 request 上读 Hub 的线程结束并关闭浏览器
        this.serverWriter.closeSocket(clientId);
    }
}
