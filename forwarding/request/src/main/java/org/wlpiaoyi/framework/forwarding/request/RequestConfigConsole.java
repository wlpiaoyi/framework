package org.wlpiaoyi.framework.forwarding.request;

import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.utils.MapUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 启动时在控制台打印转发配置（监听端口 → 目标地址）。<br>
 * 默认不输出；需要时加 {@code -Dforwarding.console.config=true}。
 */
public final class RequestConfigConsole {

    private RequestConfigConsole() {
    }

    public static void printForwardingTable() {
        if (!Boolean.parseBoolean(System.getProperty("forwarding.console.config", "false"))) {
            return;
        }
        System.out.println("--- forwarding-request config: " + ForwardUtils.CONFIG_PATH + " ---");
        String response = ForwardUtils.getResponseServerAddress();
        System.out.println("  response server: " + (response == null ? "(null)" : response));
        Map<?, ?> dict = ForwardUtils.getDict();
        if (dict == null || dict.isEmpty()) {
            System.out.println("  dict: (empty)");
            System.out.println("--- end ---");
            return;
        }
        List<Map.Entry<?, ?>> entries = new ArrayList<>(dict.entrySet());
        entries.sort(Comparator.comparingInt(e -> parsePortKeySafe(String.valueOf(e.getKey()))));
        System.out.println("  listen port  ->  target");
        for (Map.Entry<?, ?> e : entries) {
            int port = parsePortKeySafe(String.valueOf(e.getKey()));
            if (port == Integer.MAX_VALUE) {
                continue;
            }
            Object v = e.getValue();
            String target = "";
            String name = "";
            if (v instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) v;
                target = MapUtils.getString(m, "host", "");
                name = MapUtils.getString(m, "name", "");
            }
            String line = "  " + port + "  ->  " + (target.isEmpty() ? "(no host)" : target);
            if (!name.isEmpty()) {
                line += "  (" + name + ")";
            }
            System.out.println(line);
        }
        System.out.println("--- end ---");
    }

    private static int parsePortKeySafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}
