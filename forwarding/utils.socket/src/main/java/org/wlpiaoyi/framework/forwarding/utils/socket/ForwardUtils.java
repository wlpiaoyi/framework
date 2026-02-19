package org.wlpiaoyi.framework.forwarding.utils.socket;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-17 14:37:22</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class ForwardUtils {

//    public static final int MAX_CACHE_SIZE = 1024 * 1024 * 20;
//    public static final int BUFF_CACHE_SIZE = 1024 * 1024 * 10;

    public static final int MAX_CACHE_SIZE = 1024 * 2;
    public static final int BUFF_CACHE_SIZE = 1024;

    public static String getResponseServerAddress() {
        return "127.0.0.1:8080";
    }

    public static String getRequestServerAddress(int port) {
        return switch (port) {
            case 1180 -> "aiggit.com:30001";
            case 1122 -> "47.109.202.19:22";
            default -> throw new RuntimeException("port error");
        };
    }

}
