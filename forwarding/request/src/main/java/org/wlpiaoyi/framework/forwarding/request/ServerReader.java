package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingPortStats;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingLog;
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
 * request 侧本地 SocketServer 的连接读取处理器。
 * <p>
 * 每个新建立的浏览器/客户端连接都会创建一个 {@code ServerReader} 实例。
 * 其核心职责：
 * <ul>
 *   <li><b>接收本地明文流量</b>：{@link #read} 被触发时，将原始字节封装为 {@link RequestMessage}。</li>
 *   <li><b>加密转发</b>：使用 {@link SecurityUtils#getSecurity()} 对数据进行 RSA+AES 混合加密，
 *       通过到 response 模块（Hub）的 {@link SocketClient} 发送。</li>
 *   <li><b>生命周期管理</b>：当浏览器侧连接断开或出错时，级联关闭所有关联的 Hub 连接。</li>
 * </ul>
 * </p>
 */
@Slf4j
public class ServerReader implements IReader {

    /** 缓存当前浏览器连接对应的 Hub 客户端，key = "clientId:respHost:respPort" */
    private final Map<String, SocketClient> clientContentDict = new ConcurrentHashMap<>();

    /** 复用的字节缓存，用于将 RequestMessage 序列化后写入 Hub */
    private final byte[] bufferCaches = new byte[ForwardUtils.MAX_CACHE_SIZE];

    /** 消息序号计数器，用于调试追踪 */
    private int messageId = 0;

    /** 当前监听的本地端口号 */
    private final int serverPort;

    public ServerReader(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * 新连接建立时的回调。
     *
     * @param clientId 连接唯一标识（由底层 SocketServer 分配）
     * @param host     对端地址
     * @param port     对端端口
     * @return 固定返回 1，表示继续读取
     */
    @Override
    public int begin(int clientId, String host, int port) {
        ForwardingPortStats.onConnectionOpen(serverPort);
        log.info("[fw-req] peer-open listenPort={} clientId={} peer={}:{}", serverPort, clientId, host, port);
        return 1;
    }

    /**
     * 收到本地客户端数据时的回调（核心转发逻辑）。
     * <p>
     * 执行流程：
     * <ol>
     *   <li>创建 {@link RequestMessage}，填入目标 host/port（从配置 {@code dict} 中获取）。</li>
     *   <li>若数据长度 {@code > 0}，复制并加密数据，设置到 message；否则 data 为 null。</li>
     *   <li>获取或创建到 response 模块的 {@link SocketClient}。</li>
     *   <li>将 message 序列化为字节，通过 Hub writer 发送。</li>
     * </ol>
     * </p>
     *
     * @param writer   用于回写数据的 writer（此处主要给 {@link ClientReader} 回写用）
     * @param clientId 连接唯一标识
     * @param bytes    原始字节数组
     * @param len      本次读取到的有效长度
     * @return 固定返回 0
     */
    @Override
    public int read(IWriter writer, int clientId, byte[] bytes, int len) {
        this.messageId++;
        if (this.messageId < 0) this.messageId = 1;
        if (log.isDebugEnabled()) {
            log.debug("[fw-req] ingress-tcp-chunk listenPort={} clientId={} seq={} chunkLen={} (0=ClientRunner首调或空读)",
                    serverPort, clientId, this.messageId, len);
        }

        // 构造 RequestMessage，设置目标地址
        RequestMessage message = new RequestMessage(this.messageId);
        {
            String[] addrs = ForwardUtils.getRequestServerAddress(this.serverPort).split(":");
            message.setHost(addrs[0]);
            message.setPort(Integer.parseInt(addrs[1]));
        }

        // 有实际数据则加密，否则 payload 为空
        if (len > 0) {
            ForwardingPortStats.addBytesUp(serverPort, len);
            byte[] data = new byte[len];
            System.arraycopy(bytes, 0, data, 0, len);
            message.setData(SecurityUtils.getSecurity().encrypt(data, 0, data.length));
        } else {
            message.setData(null);
        }

        // 解析 response 模块（Hub）地址
        var hub = ForwardUtils.getResponseServerAddress().split(":");
        String respHost = hub[0];
        int respPort = Integer.parseInt(hub[1]);

        // 获取或创建到 Hub 的 SocketClient
        SocketClient client = this.getClient(clientId, respHost, respPort, writer);
        if (client == null) {
            log.warn("[fw-req] hub unavailable, drop ingress listenPort={} clientId={} hub={}:{}", serverPort, clientId, respHost, respPort);
            return 0;
        }

        var hubWriter = client.getWriter();
        if (hubWriter == null) {
            log.warn("[fw-req] hub writer missing listenPort={} clientId={}", serverPort, clientId);
            return 0;
        }

        // 序列化并发送
        int bLen = message.toBytes(this.bufferCaches, 0);
        hubWriter.write(clientId, this.bufferCaches, bLen);

        int encLen = message.getData() == null ? 0 : message.getData().length;
        if(log.isDebugEnabled()){
            log.debug("[fw-req] client->hub listenPort={} clientId={} msgId={} plainLen={} encPayloadLen={} target={}:{} hub={}:{} wireFrameLen={} encHex={}",
                    serverPort, clientId, message.getId(), len, encLen, message.getHost(), message.getPort(), respHost, respPort, bLen, ForwardingLog.hexPreview(bufferCaches));
        }
        return 0;
    }

    /**
     * 连接发生异常时的回调。
     * <p>
     * 区分“正常关闭”与“异常错误”打印不同级别日志，并级联断开所有 Hub 连接。
     * </p>
     *
     * @param clientId 连接唯一标识
     * @param e        异常对象
     */
    @Override
    public void error(int clientId, Exception e) {
        ForwardingPortStats.onConnectionClose(serverPort);
        if (SocketQuietErrors.isBenignClose(e)) {
            log.debug("[fw-req] peer-tcp-end listenPort={} clientId={} ({})", serverPort, clientId, e.toString());
        } else {
            log.error("[fw-req] peer-error listenPort={} clientId={}", serverPort, clientId, e);
        }
        synchronized (this.clientContentDict) {
            this.clientContentDict.forEach((k, v) -> v.disConnect());
            this.clientContentDict.clear();
        }
    }

    /**
     * 连接正常关闭时的回调。
     * <p>
     * 级联断开所有关联的 Hub 连接，清理资源。
     * </p>
     *
     * @param clientId 连接唯一标识
     */
    @Override
    public void end(int clientId) {
        ForwardingPortStats.onConnectionClose(serverPort);
        log.info("[fw-req] peer-close listenPort={} clientId={}", serverPort, clientId);
        synchronized (this.clientContentDict) {
            this.clientContentDict.forEach((k, v) -> v.disConnect());
            this.clientContentDict.clear();
        }
    }

    /**
     * 获取或创建到 response 模块（Hub）的 {@link SocketClient}。
     * <p>
     * 使用 {@code clientId:respHost:respPort} 作为 key 缓存连接，避免同一浏览器连接重复建立 Hub 通道。
     * 新创建的客户端会绑定 {@link ClientReader} 用于读取 Hub 回传的响应数据。
     * </p>
     *
     * @param clientId    浏览器连接 ID
     * @param respHost    Hub 主机地址
     * @param respPort    Hub 端口
     * @param serverWriter 用于回写给浏览器的 writer（传递给 ClientReader）
     * @return 已连接的 {@link SocketClient}，若连接失败返回 null
     */
    public SocketClient getClient(int clientId, String respHost, int respPort, IWriter serverWriter) {
        final String key = clientId + ":" + respHost + ":" + respPort;
        return this.clientContentDict.compute(key, (k, v) -> {
            if (v != null) {
                return v; // 已有缓存连接，直接复用
            }
            int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut", 60);
            SocketClient socketClient = new SocketClient(respHost, respPort, timeOut, clientId,
                    ForwardUtils.BUFF_CACHE_SIZE, new ClientReader(serverWriter, serverPort));
            try {
                socketClient.connect();
                // 注册断开回调：Hub 连接断开后从缓存中移除
                socketClient.asyncRun(() -> {
                    this.clientContentDict.remove(key);
                    log.debug("[fw-req] hub-client-removed-from-map clientId={} key={}", clientId, key);
                });
                log.info("[fw-req] hub-channel-open clientId={} hub={}:{}", clientId, respHost, respPort);
                return socketClient;
            } catch (IOException e) {
                log.error("[fw-req] hub-connect-fail clientId={} hub={}:{}", clientId, respHost, respPort, e);
                return null;
            }
        });
    }
}
