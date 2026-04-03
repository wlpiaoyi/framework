package org.wlpiaoyi.framework.forwarding.request;

import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.utils.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 按配置文件中的监听端口统计当前活跃的前端连接数（每个 TCP 会话 begin +1 / end -1）。
 */
public final class RequestConnectionRegistry {

    private static final ConcurrentHashMap<Integer, AtomicInteger> ACTIVE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, String> LABELS = new ConcurrentHashMap<>();
    private static volatile List<Integer> ORDERED_PORTS = List.of();

    private RequestConnectionRegistry() {
    }

    public static void initFromDict() {
        Map<?, ?> dict = ForwardUtils.getDict();
        ACTIVE.clear();
        LABELS.clear();
        if (dict == null || dict.isEmpty()) {
            ORDERED_PORTS = List.of();
            return;
        }
        List<Map.Entry<?, ?>> entries = new ArrayList<>(dict.entrySet());
        entries.sort(Comparator.comparingInt(e -> parsePortKeySafe(String.valueOf(e.getKey()))));

        List<Integer> ports = new ArrayList<>();
        for (Map.Entry<?, ?> e : entries) {
            int port = parsePortKeySafe(String.valueOf(e.getKey()));
            if (port == Integer.MAX_VALUE) {
                continue;
            }
            ports.add(port);
            ACTIVE.put(port, new AtomicInteger(0));
            Object v = e.getValue();
            String label;
            if (v instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> m = (Map<String, Object>) v;
                String name = MapUtils.getString(m, "name", "");
                label = name.isEmpty() ? String.valueOf(port) : port + " (" + name + ")";
            } else {
                label = String.valueOf(port);
            }
            if (label.length() > 28) {
                label = label.substring(0, 25) + "...";
            }
            LABELS.put(port, label);
        }
        ORDERED_PORTS = Collections.unmodifiableList(ports);
    }

    public static List<Integer> getOrderedPorts() {
        return ORDERED_PORTS;
    }

    public static String getLabel(int listenPort) {
        return LABELS.getOrDefault(listenPort, String.valueOf(listenPort));
    }

    public static int getActiveCount(int listenPort) {
        AtomicInteger a = ACTIVE.get(listenPort);
        return a == null ? 0 : a.get();
    }

    public static void onClientBegin(int listenPort) {
        AtomicInteger a = ACTIVE.get(listenPort);
        if (a != null) {
            a.incrementAndGet();
            RequestPortStatusConsole.refresh();
        }
    }

    public static void onClientEnd(int listenPort) {
        AtomicInteger a = ACTIVE.get(listenPort);
        if (a != null) {
            a.updateAndGet(v -> Math.max(0, v - 1));
            RequestPortStatusConsole.refresh();
        }
    }

    private static int parsePortKeySafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}
