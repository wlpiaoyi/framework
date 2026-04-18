package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.BufferCaches;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.RequestMessage;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.SocketQuietErrors;
import org.wlpiaoyi.framework.utils.socket.IWriter;
import org.wlpiaoyi.framework.utils.socket.client.SocketClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * response 侧用于读取 request 模块（Hub）数据的处理器。
 * <p>
 * 每个来自 request 的新连接都会创建一个 {@code ServerReader} 实例。
 * 核心职责：
 * <ul>
 *   <li><b>粘包处理</b>：使用 {@link BufferCaches} 组装完整帧。</li>
 *   <li><b>解析请求</b>：将字节流反序列化为 {@link RequestMessage}，提取目标 host/port 与加密数据。</li>
 *   <li><b>解密转发</b>：使用 {@link SecurityUtils#getSecurity()} 解密，通过 {@link SocketClient} 发送给真实目标。</li>
 *   <li><b>目标连接管理</b>：按 {@code clientId:host:port} 缓存到目标的连接，避免重复建连。</li>
 *   <li><b>级联清理</b>：Hub 连接断开时，关闭所有关联的目标连接。</li>
 * </ul>
 * </p>
 */
@Slf4j
public class ServerReader implements IReader {

    /** 缓存到真实目标服务器的连接，key = "clientId:targetHost:targetPort" */
    private final Map<String, SocketClient> clientContentDict = new ConcurrentHashMap<>();

    /** 消息序号计数器 */
    private int messageId = 0;

    /** 粘包/拆包缓冲区 */
    private final BufferCaches bufferCaches = new BufferCaches();

    /**
     * Hub 新连接建立时的回调。
     *
     * @param clientId 连接唯一标识
     * @param host     对端地址
     * @param port     对端端口
     * @return 固定返回 1
     */
    @Override
    public int begin(int clientId, String host, int port) {
        log.info("[fw-res] peer-open clientId={} peer={}:{}", clientId, host, port);
        return 1;
    }

    /**
     * 收到 Hub 数据时的回调。
     * <p>
     * 循环调用私有 {@link #read} 处理所有完整帧，解决 TCP 粘包问题。
     * </p>
     *
     * @param writer   用于回写数据的 writer（传递给 ClientReader）
     * @param clientId 连接唯一标识
     * @param readBytes 读取到的字节数组
     * @param readLen   本次读取有效长度
     * @return 固定返回 0
     */
    @Override
    public int read(IWriter writer, int clientId, byte[] readBytes, int readLen) {
        if(ForwardUtils.isLogEnabled()){
            log.debug("[fw-res] peer-read clientId={} msgLen={} msg={}", clientId, readLen, ForwardingLog.hexPreview(readBytes));
        }
        long len = ValueUtils.byteToLong(readBytes, 0, 4);
        if(len > ForwardUtils.BUFF_CACHE_SIZE * 4 || len < 0){
            System.out.println();
        }
        int off = this.read(writer, clientId, readBytes, 0, readLen);
        while (off > 0) {
            off = this.read(writer, clientId, readBytes, off, readLen);
        }
        return 0;
    }

    /**
     * Hub 连接发生异常时的回调。
     * <p>
     * 区分正常关闭与异常错误，并级联断开所有目标连接。
     * </p>
     *
     * @param clientId 连接唯一标识
     * @param e        异常对象
     */
    @Override
    public void error(int clientId, Exception e) {
        if (SocketQuietErrors.isBenignClose(e)) {
            log.debug("[fw-res] peer-tcp-end clientId={} ({})", clientId, e.toString());
        } else {
            log.error("[fw-res] peer-error clientId={}", clientId, e);
        }
        synchronized (this.clientContentDict) {
            this.clientContentDict.forEach((k, v) -> v.disConnect());
            this.clientContentDict.clear();
        }
    }

    /**
     * Hub 连接正常关闭时的回调。
     * <p>
     * 级联断开所有关联的真实目标连接，清理资源。
     * </p>
     *
     * @param clientId 连接唯一标识
     */
    @Override
    public void end(int clientId) {
        log.info("[fw-res] peer-close clientId={}", clientId);
        synchronized (this.clientContentDict) {
            this.clientContentDict.forEach((k, v) -> v.disConnect());
            this.clientContentDict.clear();
        }
    }

    /**
     * 获取或创建到真实目标服务器的 {@link SocketClient}。
     * <p>
     * 使用 {@code clientId:respHost:respPort} 作为 key 缓存连接。
     * 新创建的客户端会绑定 {@link ClientReader} 用于读取目标响应并加密回传。
     * </p>
     *
     * @param clientId     Hub 连接 ID
     * @param respHost     真实目标主机
     * @param respPort     真实目标端口
     * @param serverWriter 用于回写给 Hub 的 writer（传递给 ClientReader）
     * @return 已连接的 {@link SocketClient}，若连接失败返回 null
     */
    private SocketClient getClient(int clientId, String respHost, int respPort, IWriter serverWriter) {
        final String key = clientId + ":" + respHost + ":" + respPort;
        return this.clientContentDict.compute(key, (k, v) -> {
            if (v != null) {
                return v; // 复用已有连接
            }
            int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut", 60);
            SocketClient socketClient = new SocketClient(respHost, respPort, timeOut, clientId,
                    ForwardUtils.BUFF_CACHE_SIZE, new ClientReader(serverWriter));
            try {
                socketClient.connect();
                // 注册断开回调：目标连接断开后从缓存中移除
                socketClient.asyncRun(() -> {
                    this.clientContentDict.remove(key);
                    log.debug("[fw-res] target-client-removed-from-map clientId={} key={}", clientId, key);
                });
                log.info("[fw-res] target-channel-open clientId={} target={}:{}", clientId, respHost, respPort);
                return socketClient;
            } catch (IOException e) {
                log.error("[fw-res] target-connect-fail clientId={} target={}:{}", clientId, respHost, respPort, e);
                return null;
            }
        });
    }

    /**
     * 处理单帧 RequestMessage 的核心方法。
     * <p>
     * 执行流程：
     * <ol>
     *   <li>通过 {@link BufferCaches#loadIfNeed} 组装完整帧。</li>
     *   <li>解析 {@link RequestMessage}，校验消息合法性。</li>
     *   <li>解密数据（若有）。</li>
     *   <li>获取或创建到目标服务器的 {@link SocketClient}，将明文写入。</li>
     * </ol>
     * </p>
     *
     * @param writer   用于回写的 writer
     * @param clientId 连接唯一标识
     * @param bytes    字节数组
     * @param off      本次处理起始偏移
     * @param len      数组总长度
     * @return 下一帧起始偏移；若帧不完整返回 0
     */
    private int read(IWriter writer, int clientId, byte[] bytes, int off, int len) {
        int cOff = -1;
        try {
            this.messageId++;
            if (this.messageId < 0) this.messageId = 1;
            if (log.isDebugEnabled()) {
                log.debug("[fw-res] hub-tcp-assemble clientId={} slice=[{}, {}) readCallLen={}", clientId, off, len, len);
            }
            // 粘包处理
            cOff = this.bufferCaches.loadIfNeed(bytes, off, len);
            if (cOff == -1) {
                if (log.isTraceEnabled()) {
                    log.trace("[fw-res] hub tcp chunk clientId={} frame-incomplete bytesThisCall={}", clientId, len - off);
                }
                return 0;
            }

            // 帧完整，解析 RequestMessage
            RequestMessage message = new RequestMessage(this.messageId);
            message.formatBytes(this.bufferCaches.getBuffers(), 0);
            if (!message.check()) throw new RuntimeException("message check error！clientId:" + clientId);

            if (log.isDebugEnabled()) {
                int encN = message.getData() == null ? 0 : message.getData().length;
                log.debug("[fw-res] request-parsed clientId={} wireMsgId={} wireDeclaredLen={} target={}:{} encPayloadLen={} wireHeadHex={}",
                        clientId, message.getId(), message.getLen(), message.getHost(), message.getPort(), encN,
                        ForwardingLog.hexPreview(this.bufferCaches.getBuffers(), 0, Math.min(message.getLen(), this.bufferCaches.getBuffers().length), 16));
            }

            // 获取或创建到真实目标的连接
            var client = this.getClient(clientId, message.getHost(), message.getPort(), writer);
            if (client == null) {
                log.warn("[fw-res] target unavailable, drop frame clientId={} target={}:{}", clientId, message.getHost(), message.getPort());
                return cOff;
            }

            // 有数据则解密并转发给目标
            if (message.getData() != null && message.getData().length > 0) {
                log.debug("[fw-res] request cipher clientId={} msgId={} target={}:{} encLen={} encHex={}",
                        clientId, message.getId(), message.getHost(), message.getPort(), message.getData().length,
                        ForwardingLog.hexPreview(message.getData()));
                var data = SecurityUtils.getSecurity().decrypt(message.getData(), 0, message.getData().length);
                log.debug("[fw-res] request plain clientId={} msgId={} target={}:{} plainLen={} previewHex={}",
                        clientId, message.getId(), message.getHost(), message.getPort(), data.length,
                        ForwardingLog.hexPreview(data, 0, data.length, ForwardingLog.DEFAULT_HEX_PREVIEW_BYTES));

                var tw = client.getWriter();
                if (tw == null) {
                    log.warn("[fw-res] target writer missing clientId={} target={}:{}", clientId, message.getHost(), message.getPort());
                    return cOff;
                }
                tw.write(clientId, data, data.length);
            } else {
                log.debug("[fw-res] request no-payload clientId={} msgId={} target={}:{}",
                        clientId, message.getId(), message.getHost(), message.getPort());
            }
            return cOff;
        } finally {
            // 处理完一帧后重置缓冲区
            if (cOff != -1) this.bufferCaches.reset();
        }
    }
}
