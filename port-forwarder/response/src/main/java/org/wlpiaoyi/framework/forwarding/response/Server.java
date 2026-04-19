package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.socket.server.SocketServer;

/**
 * response 模块的核心服务端启动器。
 * <p>
 * 负责监听单一端口，接收来自 request 模块的加密 {@link org.wlpiaoyi.framework.forwarding.utils.socket.model.RequestMessage}，
 * 解密后连接到真实目标服务器，再将目标响应加密回传。
 * </p>
 * <p>
 * 每个新连接会分配一个 {@link ServerReader} 实例处理读取逻辑。
 * </p>
 */
@Slf4j
public class Server {

    /** 封装的底层 SocketServer */
    private final SocketServer server;

    /** 当前监听的端口号 */
    private final int listenPort;

    /**
     * 构造 Server。
     *
     * @param port 监听端口
     */
    public Server(int port) {
        this.listenPort = port;
        int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut", 60);
        // 构建 SocketServer：绑定端口，设置超时，每个新连接使用 ServerReader 处理
        this.server = SocketServer.build(port, ForwardUtils.BUFF_CACHE_SIZE, timeOut)
                .setLoadReader(clientId -> new ServerReader());
    }

    /**
     * 启动监听，阻塞运行直到服务停止。
     *
     * @throws InterruptedException 若主线程被中断
     */
    public void run() throws InterruptedException {
        log.info("[fw-res] bootstrap listenPort={} buffer={}B timeout={}s",
                listenPort, ForwardUtils.BUFF_CACHE_SIZE, MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut", 60));
        this.server.start();
    }
}
