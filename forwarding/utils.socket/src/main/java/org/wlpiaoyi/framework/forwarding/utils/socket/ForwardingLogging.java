package org.wlpiaoyi.framework.forwarding.utils.socket;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.wlpiaoyi.framework.utils.MapUtils;

import java.util.Map;

/**
 * 根据 {@code fw_config/forwarding/config.json} 中的 {@code logEnabled} 在加载配置后调整日志输出。
 */
public final class ForwardingLogging {

    private ForwardingLogging() {
    }

    /**
     * 在 {@link ForwardUtils#loadMap()} 填充配置后调用。
     * {@code logEnabled} 缺省为 true；为 false 时关闭控制台与文件输出（与 logback.xml 中 root 挂载的追加器一致）。
     */
    @SuppressWarnings("rawtypes")
    public static void applyFromConfig() {
        Map cfg = ForwardUtils.getCONFIG_MAP();
        if (cfg == null) {
            return;
        }
        boolean enabled = Boolean.TRUE.equals(MapUtils.getBoolean(cfg, "logEnabled", Boolean.TRUE));
        if (enabled) {
            return;
        }
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
