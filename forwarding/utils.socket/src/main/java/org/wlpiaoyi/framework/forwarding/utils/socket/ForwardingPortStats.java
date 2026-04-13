package org.wlpiaoyi.framework.forwarding.utils.socket;

import org.wlpiaoyi.framework.utils.MapUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * request 侧各转发监听端口的连接数与累计流量（用于静默模式控制台表格）。
 */
public final class ForwardingPortStats {

    public enum ListenState {
        /** 尚未绑定或线程未跑到 bind */
        PENDING,
        /** 已成功 bind 并处于 accept 循环 */
        RUNNING,
        /** bind 失败或监听线程已退出 */
        STOPPED
    }

    public static final class Row {
        public final int port;
        public volatile String name;
        public final AtomicInteger connections = new AtomicInteger();
        public final LongAdder bytesUp = new LongAdder();
        public final LongAdder bytesDown = new LongAdder();
        public volatile ListenState listenState = ListenState.PENDING;

        volatile long snapUp;
        volatile long snapDown;
        volatile long snapTimeNanos;
        /** 字节/秒，由静默模式控制台定时任务每秒更新 */
        public volatile double upRateBps;
        public volatile double downRateBps;

        Row(int port, String name) {
            this.port = port;
            this.name = name == null ? "-" : name;
        }
    }

    private static final ConcurrentHashMap<Integer, Row> BY_PORT = new ConcurrentHashMap<>();

    private ForwardingPortStats() {
    }

    @SuppressWarnings("rawtypes")
    public static void initFromDict() {
        Map dict = ForwardUtils.getDict();
        if (dict == null || dict.isEmpty()) {
            return;
        }
        for (Object ko : dict.keySet()) {
            int port = Integer.parseInt(ko.toString());
            Map entry = MapUtils.getMap(dict, ko);
            String name = entry == null ? "-" : MapUtils.getString(entry, "name", "-");
            BY_PORT.compute(port, (p, row) -> {
                if (row == null) {
                    return new Row(p, name);
                }
                row.name = name;
                return row;
            });
        }
    }

    public static void markListenRunning(int port) {
        if (!ForwardUtils.isLogEnabled()) {
            Row r = BY_PORT.get(port);
            if (r != null) {
                r.listenState = ListenState.RUNNING;
            }
        }
    }

    public static void markListenStopped(int port) {
        if (!ForwardUtils.isLogEnabled()) {
            Row r = BY_PORT.get(port);
            if (r != null) {
                r.listenState = ListenState.STOPPED;
            }
        }
    }

    public static void onConnectionOpen(int port) {
        if (!ForwardUtils.isLogEnabled()) {
            Row r = BY_PORT.get(port);
            if (r != null) {
                r.connections.incrementAndGet();
            }
        }
    }

    public static void onConnectionClose(int port) {
        if (!ForwardUtils.isLogEnabled()) {
            Row r = BY_PORT.get(port);
            if (r != null) {
                r.connections.updateAndGet(c -> c > 0 ? c - 1 : 0);
            }
        }
    }

    public static void addBytesUp(int port, int len) {
        if (len <= 0 || ForwardUtils.isLogEnabled()) {
            return;
        }
        Row r = BY_PORT.get(port);
        if (r != null) {
            r.bytesUp.add(len);
        }
    }

    public static void addBytesDown(int port, int len) {
        if (len <= 0 || ForwardUtils.isLogEnabled()) {
            return;
        }
        Row r = BY_PORT.get(port);
        if (r != null) {
            r.bytesDown.add(len);
        }
    }

    /** 按端口排序的快照，供表格渲染。 */
    public static List<Row> snapshotRowsSorted() {
        List<Row> list = new ArrayList<>(BY_PORT.values());
        list.sort(Comparator.comparingInt(r -> r.port));
        return list;
    }

    static void tickRates() {
        long now = System.nanoTime();
        for (Row r : BY_PORT.values()) {
            long up = r.bytesUp.sum();
            long down = r.bytesDown.sum();
            long t0 = r.snapTimeNanos;
            if (t0 == 0L) {
                r.snapUp = up;
                r.snapDown = down;
                r.snapTimeNanos = now;
                r.upRateBps = 0;
                r.downRateBps = 0;
                continue;
            }
            double sec = (now - t0) / 1_000_000_000.0;
            if (sec < 1e-6) {
                sec = 1e-6;
            }
            r.upRateBps = (up - r.snapUp) / sec;
            r.downRateBps = (down - r.snapDown) / sec;
            r.snapUp = up;
            r.snapDown = down;
            r.snapTimeNanos = now;
        }
    }
}
