package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;

/**
 * request 模块的入口类（JVM 启动主类）。
 * <p>
 * 职责：
 * <ul>
 *   <li>实例化 {@link Server} 并调用 {@link Server#run()} 启动所有本地监听端口。</li>
 *   <li>启动后进入无限循环，保持主线程存活，防止 JVM 退出。</li>
 * </ul>
 * </p>
 */
@Slf4j
public class AppLoader {

    /**
     * 程序入口。
     *
     * @param args 命令行参数（当前未使用）
     */
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.run();
            // 主线程休眠循环，维持进程存活
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            log.error("AppLoader.main. Error occurred while running server", e);
        }
    }
}
