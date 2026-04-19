package org.wlpiaoyi.framework.forwarding.utils.socket;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.wlpiaoyi.framework.utils.MapUtils;

import java.util.Map;

/**
 * 转发框架的运行时日志动态开关。
 * <p>
 * 根据 {@code fw_config/forwarding/config.json} 中的 {@code logEnabled} 字段，
 * 在加载配置后动态调整 Logback 日志级别：
 * <ul>
 *   <li>{@code true}（默认）：正常输出日志，停止控制台监控面板。</li>
 *   <li>{@code false}：进入静默模式，关闭大部分日志输出，由 {@link ForwardingConsoleDashboard} 接管控制台。</li>
 * </ul>
 * </p>
 */
public final class ForwardingLogging {

    private ForwardingLogging() {
        // 工具类禁止实例化
    }

    /**
     * 在 {@link ForwardUtils#loadMap()} 填充配置后调用。
     * <p>
     * {@code logEnabled} 缺省为 {@code true}；为 {@code false} 时关闭控制台与文件输出
     *（与 logback.xml 中 root 挂载的追加器一致）。
     * </p>
     */
    @SuppressWarnings("rawtypes")
    public static void applyFromConfig() {
        Map cfg = ForwardUtils.getCONFIG_MAP();
        if (cfg == null) {
            return;
        }
        boolean enabled = Boolean.TRUE.equals(MapUtils.getBoolean(cfg, "logEnabled", Boolean.TRUE));
        if (enabled) {
            // 日志开启时停止控制台表格，避免与日志追加器冲突
            ForwardingConsoleDashboard.stop();
            return;
        }
        // 静默模式：关闭 Logback 相关 logger
        if (!(LoggerFactory.getILoggerFactory() instanceof LoggerContext lc)) {
            return;
        }
        Logger root = lc.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.detachAndStopAllAppenders();
        root.setLevel(Level.OFF);
        Logger forwarding = lc.getLogger("org.wlpiaoyi.framework.forwarding");
        forwarding.setLevel(Level.OFF);
        Logger utilsSocket = lc.getLogger("org.wlpiaoyi.framework.utils.socket");
        utilsSocket.setLevel(Level.OFF);
    }
}
