package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;

import java.io.IOException;

/**
 * response 模块的入口类（JVM 启动主类）。
 * <p>
 * 职责：
 * <ul>
 *   <li>静态块加载配置文件 {@code fw_config/forwarding/config.json}。</li>
 *   <li>解析配置中的 {@code host} 字段，提取端口号，创建并启动 {@link Server}。</li>
 * </ul>
 * </p>
 */
@Slf4j
public class AppLoader {

    // 类加载时读取全局配置
    static {
        try {
            ForwardUtils.loadMap();
        } catch (IOException e) {
            log.error("AppLoader.static. Error occurred while loading config", e);
        }
    }

    /**
     * 程序入口。
     *
     * @param args 命令行参数（当前未使用）
     */
    public static void main(String[] args) {
        // 从配置中解析 response 监听端口，例如 "0.0.0.0:8080" → 8080
        Server server = new Server(Integer.parseInt(ForwardUtils.getResponseServerAddress().split(":")[1]));
        try {
            server.run();
        } catch (InterruptedException e) {
            log.error("AppLoader.main. Error occurred while running server", e);
        }
    }
}
