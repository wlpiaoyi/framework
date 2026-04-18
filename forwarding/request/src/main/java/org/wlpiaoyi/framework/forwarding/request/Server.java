package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingConsoleDashboard;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingPortStats;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.socket.Builder;
import org.wlpiaoyi.framework.utils.socket.server.SocketServer;
import org.wlpiaoyi.framework.utils.thread.Runnable;

import java.io.IOException;

/**
 * request 模块的核心服务端启动器。
 * <p>
 * 负责根据配置文件 {@code fw_config/forwarding/config.json} 中的 {@code dict} 映射，
 * 在本地启动多个 {@link SocketServer} 监听端口。每个端口对应一个远端目标地址，
 * 形成“本地端口 → 加密隧道 → response 模块 → 真实目标”的转发链路。
 * </p>
 * <p>
 * 静默模式（{@code logEnabled=false}）下，还会启动 {@link ForwardingConsoleDashboard}
 * 在控制台周期性刷新端口流量监控表格。
 * </p>
 */
@Slf4j
public class Server {

    // 静态块：类加载时即读取配置文件，确保全局配置可用
    static {
        try {
            ForwardUtils.loadMap();
        } catch (IOException e) {
            log.error("[fw-req] load map error", e);
        }
    }

    /**
     * 启动所有监听端口。
     * <p>
     * 遍历配置中的 {@code dict}，为每个端口在线程池中提交独立任务：
     * 构建 {@link SocketServer} 并调用 {@link SocketServer#start()} 进入 accept 循环。
     * 新连接到达时会创建 {@link ServerReader} 实例处理 I/O。
     * </p>
     *
     * @throws InterruptedException 若主线程被中断
     */
    public void run() throws InterruptedException {
        // 若处于静默模式，初始化端口统计并启动控制台监控面板
        if (!ForwardUtils.isLogEnabled()) {
            ForwardingPortStats.initFromDict();
            ForwardingConsoleDashboard.start();
        }
        log.info("[fw-req] bootstrap hub={} listenEntries={} buffer={}B timeout={}s",
                ForwardUtils.getResponseServerAddress(), ForwardUtils.getDict().size(),
                ForwardUtils.BUFF_CACHE_SIZE, MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut", 60));

        // 遍历配置中的端口映射，为每个监听端口启动独立的服务线程
        ForwardUtils.getDict().forEach((k, v) -> {
            Builder.getThreadPool().submit((Runnable<Object, Object>) (taskId, param) -> {
                int port = Integer.parseInt(k.toString());
                int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut", 60);
                String target = ForwardUtils.getRequestServerAddress(port);
                log.info("[fw-req] listen port={} -> target {}", port, target);

                // 构建 SocketServer：绑定本地端口，设置连接超时与回调
                var server = SocketServer.build(port, ForwardUtils.BUFF_CACHE_SIZE, timeOut)
                        .setOnBound(() -> ForwardingPortStats.markListenRunning(port))
                        .setLoadReader(clientId -> new ServerReader(port));
                server.start();

                // 监听线程退出（通常因异常），标记为已停止
                ForwardingPortStats.markListenStopped(port);
                return 0;
            });
        });
    }
}
