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
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-17 14:37:22</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class ForwardUtils {

//    public static final int MAX_CACHE_SIZE = 1024 * 1024 * 20;
//    public static final int BUFF_CACHE_SIZE = 1024 * 1024 * 10;
    protected static final String CONFIG_PATH = "C:\\Home\\Document\\Develop\\Java\\framework\\forwarding\\utils.socket\\src\\main\\resources\\config.json";
    public static final int MAX_CACHE_SIZE = 1024 * 2;
    public static final int BUFF_CACHE_SIZE = 1024;

    @Getter
    private static Map CONFIG_MAP = null;

    public static void loadMap() throws IOException {
        CONFIG_MAP = Collections.unmodifiableMap(Objects.requireNonNull(ReaderUtils.loadMap(CONFIG_PATH, StandardCharsets.UTF_8)));
    }

    public static String getResponseServerAddress() {
        return MapUtils.getString(CONFIG_MAP, "host");
    }

    public static String getRequestServerAddress(int port) {
        return MapUtils.getValueByKeyPath(CONFIG_MAP, "dict." + port + ".host", null, String.class) ;
    }

    public static Map getDict() {
        return MapUtils.getMap(CONFIG_MAP, "dict");
    }

}
