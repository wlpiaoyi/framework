package org.wlpiaoyi.framework.forwarding.utils.socket;

import lombok.Getter;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 转发框架的全局配置加载与常量工具类。
 * <p>
 * 负责从 {@code fw_config/forwarding/config.json} 读取配置，提供全局常量、
 * 以及快捷访问配置字段的方法（如 response 地址、端口映射字典、日志开关等）。
 * </p>
 */
public class ForwardUtils {

//    public static final int MAX_CACHE_SIZE = 1024 * 1024 * 20;
//    public static final int BUFF_CACHE_SIZE = 1024 * 1024 * 10;

    /** 配置文件相对路径 */
    protected static final String CONFIG_PATH = "fw_config/forwarding/config.json";

    /** Socket 读写缓冲区的默认大小 */
    public static final int BUFF_CACHE_SIZE = 128;         // 1024 字节

    /** 单帧消息最大字节数（用于序列化缓冲与粘包缓冲区上限） */
    public static final int MAX_CACHE_SIZE = BUFF_CACHE_SIZE * 4;      // 2048 字节

    /** 加载后的全局配置 Map（只读） */
    @Getter
    private static Map CONFIG_MAP = null;

    /**
     * 加载配置文件并应用日志级别。
     * <p>
     * 将 JSON 配置解析为 Map 后设为不可变，并触发 {@link ForwardingLogging#applyFromConfig()}
     * 根据 {@code logEnabled} 调整日志输出行为。
     * </p>
     *
     * @throws IOException 读取配置文件失败时抛出
     */
    public static void loadMap() throws IOException {
        CONFIG_MAP = Collections.unmodifiableMap(
                Objects.requireNonNull(ReaderUtils.loadMap(CONFIG_PATH, StandardCharsets.UTF_8))
        );
        ForwardingLogging.applyFromConfig();
    }

    /**
     * 获取 response 模块（Hub）监听地址。
     * <p>
     * 对应配置中的 {@code host} 字段，格式通常为 {@code ip:port}。
     * </p>
     *
     * @return response 地址字符串
     */
    public static String getResponseServerAddress() {
        return MapUtils.getString(CONFIG_MAP, "host");
    }

    /**
     * 根据本地监听端口获取对应的真实目标服务器地址。
     * <p>
     * 对应配置中 {@code dict.<port>.host} 字段，格式为 {@code ip:port}。
     * </p>
     *
     * @param port 本地监听端口号
     * @return 目标地址字符串
     */
    public static String getRequestServerAddress(int port) {
        return MapUtils.getValueByKeyPath(CONFIG_MAP, "dict." + port + ".host", null, String.class);
    }

    /**
     * 获取端口映射字典。
     * <p>
     * 对应配置中的 {@code dict} 字段，key 为本地监听端口，value 包含目标地址等信息。
     * </p>
     *
     * @return 端口映射字典
     */
    public static Map getDict() {
        return MapUtils.getMap(CONFIG_MAP, "dict");
    }

    /**
     * 判断日志是否启用。
     * <p>
     * {@code logEnabled} 缺省为 {@code true}；仅当配置为 {@code false} 时为静默模式
     *（关闭日志并可配合 request 侧控制台表格监控）。
     * </p>
     *
     * @return true 表示日志启用；false 表示静默模式
     */
    @SuppressWarnings("rawtypes")
    public static boolean isLogEnabled() {
        if (CONFIG_MAP == null) {
            return true;
        }
        return !Boolean.FALSE.equals(MapUtils.getBoolean(CONFIG_MAP, "logEnabled", Boolean.TRUE));
    }
}
