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
 * request 侧各转发监听端口的连接数与流量统计（用于静默模式控制台表格）。
 * <p>
 * 仅在 {@code logEnabled=false} 时生效，为 {@link ForwardingConsoleDashboard} 提供数据源。
 * 使用 {@link ConcurrentHashMap} + {@link AtomicInteger} + {@link LongAdder} 保证高并发线程安全。
 * </p>
 */
public final class ForwardingPortStats {

    /** 监听端口状态枚举 */
    public enum ListenState {
        /** 尚未绑定或线程未跑到 bind */
        PENDING,
        /** 已成功 bind 并处于 accept 循环 */
        RUNNING,
        /** bind 失败或监听线程已退出 */
        STOPPED
    }

    /** 单条端口统计记录 */
    public static final class Row {
        public final int port;
        public volatile String name;
        /** 当前活跃连接数 */
        public final AtomicInteger connections = new AtomicInteger();
        /** 累计上行字节（request → response） */
        public final LongAdder bytesUp = new LongAdder();
        /** 累计下行字节（response → request） */
        public final LongAdder bytesDown = new LongAdder();
        public volatile ListenState listenState = ListenState.PENDING;

        // 内部快照字段，用于计算速率
        volatile long snapUp;
        volatile long snapDown;
        volatile long snapTimeNanos;
        /** 上行速率（字节/秒），由静默模式控制台定时任务每秒更新 */
        public volatile double upRateBps;
        /** 下行速率（字节/秒），由静默模式控制台定时任务每秒更新 */
        public volatile double downRateBps;

        Row(int port, String name) {
            this.port = port;
            this.name = name == null ? "-" : name;
        }
    }

    /** 按端口索引的统计表 */
    private static final ConcurrentHashMap<Integer, Row> BY_PORT = new ConcurrentHashMap<>();

    private ForwardingPortStats() {
        // 工具类禁止实例化
    }

    /**
     * 从配置字典 {@code dict} 初始化端口统计行。
     * <p>
     * 读取每个端口的 {@code name} 字段作为展示名称。
     * </p>
     */
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

    /** 标记指定端口监听已启动 */
    public static void markListenRunning(int port) {
        if (!ForwardUtils.isLogEnabled()) {
            Row r = BY_PORT.get(port);
            if (r != null) {
                r.listenState = ListenState.RUNNING;
            }
        }
    }

    /** 标记指定端口监听已停止 */
    public static void markListenStopped(int port) {
        if (!ForwardUtils.isLogEnabled()) {
            Row r = BY_PORT.get(port);
            if (r != null) {
                r.listenState = ListenState.STOPPED;
            }
        }
    }

    /** 指定端口新增一个连接 */
    public static void onConnectionOpen(int port) {
        if (!ForwardUtils.isLogEnabled()) {
            Row r = BY_PORT.get(port);
            if (r != null) {
                r.connections.incrementAndGet();
            }
        }
    }

    /** 指定端口减少一个连接（不会减到负数） */
    public static void onConnectionClose(int port) {
        if (!ForwardUtils.isLogEnabled()) {
            Row r = BY_PORT.get(port);
            if (r != null) {
                r.connections.updateAndGet(c -> c > 0 ? c - 1 : 0);
            }
        }
    }

    /** 为指定端口增加上行字节统计 */
    public static void addBytesUp(int port, int len) {
        if (len <= 0 || ForwardUtils.isLogEnabled()) {
            return;
        }
        Row r = BY_PORT.get(port);
        if (r != null) {
            r.bytesUp.add(len);
        }
    }

    /** 为指定端口增加下行字节统计 */
    public static void addBytesDown(int port, int len) {
        if (len <= 0 || ForwardUtils.isLogEnabled()) {
            return;
        }
        Row r = BY_PORT.get(port);
        if (r != null) {
            r.bytesDown.add(len);
        }
    }

    /** 按端口排序的快照，供表格渲染 */
    public static List<Row> snapshotRowsSorted() {
        List<Row> list = new ArrayList<>(BY_PORT.values());
        list.sort(Comparator.comparingInt(r -> r.port));
        return list;
    }

    /**
     * 计算各端口上下行速率（字节/秒）。
     * <p>
     * 由 {@link ForwardingConsoleDashboard} 每秒调用一次，基于前后两次快照的差值计算。
     * </p>
     */
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
