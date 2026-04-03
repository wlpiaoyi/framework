package org.wlpiaoyi.framework.forwarding.request;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * 按监听端口统计转发流量：上行（客户端→本机读入并转出的明文长度）、下行（对端返回解密后写回客户端的长度）。<br>
 * 与 {@link RequestConnectionRegistry#initFromDict()} 同步初始化端口条目。
 */
public final class RequestTrafficRegistry {

    private static final ConcurrentHashMap<Integer, LongAdder> UP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, LongAdder> DOWN = new ConcurrentHashMap<>();

    private RequestTrafficRegistry() {
    }

    public static void initFromDict() {
        UP.clear();
        DOWN.clear();
        for (int p : RequestConnectionRegistry.getOrderedPorts()) {
            UP.put(p, new LongAdder());
            DOWN.put(p, new LongAdder());
        }
        RequestPortStatusConsole.clearRateSnapshots();
    }

    /** 客户端发往转发的数据量（{@link org.wlpiaoyi.framework.forwarding.request.ServerReader#read} 明文长度） */
    public static void addUpstream(int listenPort, long bytes) {
        if (bytes <= 0) {
            return;
        }
        LongAdder a = UP.get(listenPort);
        if (a != null) {
            a.add(bytes);
        }
    }

    /** 写回客户端的数据量（解密后 payload 长度） */
    public static void addDownstream(int listenPort, long bytes) {
        if (bytes <= 0) {
            return;
        }
        LongAdder a = DOWN.get(listenPort);
        if (a != null) {
            a.add(bytes);
        }
    }

    public static long getUpstreamBytes(int listenPort) {
        LongAdder a = UP.get(listenPort);
        return a == null ? 0L : a.sum();
    }

    public static long getDownstreamBytes(int listenPort) {
        LongAdder a = DOWN.get(listenPort);
        return a == null ? 0L : a.sum();
    }
}
