package org.wlpiaoyi.framework.forwarding.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 在标准输出上展示各监听端口的连接数。<br>
 * <ul>
 *   <li><b>默认 {@code table}</b>：多行对齐表格，变化时用 ANSI（光标上移 + 清行）整段重绘；IntelliJ / Windows Terminal 等行为因环境而异。</li>
 *   <li>{@code line}：与 {@link org.wlpiaoyi.framework.utils.Progress} 相同，单行用退格 {@code \\b} 重绘（无 ANSI）。</li>
 *   <li>{@code append}：每次变化追加打印整块，适合重定向到文件。</li>
 * </ul>
 * 表格列宽按<b>终端显示宽度</b>计算（中文等宽字符计 2），避免 {@link String#format} 按字符数对齐导致的错位。<br>
 * 系统属性：{@code -Dforwarding.console.status=table|line|append}，默认 {@code table}。<br>
 * 上行/下行速率：自上次刷新间隔内的平均速度；由定时任务每秒刷新（见 {@link #startPeriodicRefresh()}）。
 */
public final class RequestPortStatusConsole {

    private static final Object LOCK = new Object();

    /** 表格模式：整块行数（含标题），用于光标上移 */
    private static int tableBlockLines;

    /** line 模式：上一行物理宽度，用于退格 */
    private static int lastPhysicalLen;

    /** 列宽为终端显示列数（非 Java char 个数） */
    private static final int COL_LISTEN = 8;
    private static final int COL_LABEL = 26;
    private static final int COL_ACTIVE = 7;
    private static final int COL_STATE = 8;
    private static final int COL_SPEED = 28;

    /** 用于计算瞬时速率：上次采样时的累计字节与时间 */
    private static final ConcurrentHashMap<Integer, RateSnap> RATE_SNAP = new ConcurrentHashMap<>();

    private static volatile boolean periodicRefreshStarted;

    private static final class RateSnap {
        long up;
        long down;
        long tNanos;
    }

    private RequestPortStatusConsole() {
    }

    /** 配置重载时清空，避免旧端口键残留 */
    public static void clearRateSnapshots() {
        RATE_SNAP.clear();
    }

    private enum Mode {
        TABLE,
        LINE,
        APPEND
    }

    private static Mode mode() {
        String p = System.getProperty("forwarding.console.status");
        if (p == null || p.isBlank()) {
            return Mode.TABLE;
        }
        if ("append".equalsIgnoreCase(p.trim())) {
            return Mode.APPEND;
        }
        if ("line".equalsIgnoreCase(p.trim())) {
            return Mode.LINE;
        }
        return Mode.TABLE;
    }

    /** 常见等宽终端下全角/汉字占 2 列，ASCII 占 1 列（与 {@link String#format} 的 {@code %s} 宽度不一致） */
    private static int displayWidth(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        int w = 0;
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            w += charDisplayWidth(cp);
            i += Character.charCount(cp);
        }
        return w;
    }

    private static int charDisplayWidth(int cp) {
        if (cp >= 0xFF01 && cp <= 0xFF5E) {
            return 2;
        }
        if (cp >= 0xFFE0 && cp <= 0xFFE6) {
            return 2;
        }
        if (Character.isIdeographic(cp)) {
            return 2;
        }
        if (cp >= 0xAC00 && cp <= 0xD7A3) {
            return 2;
        }
        if (cp >= 0x3040 && cp <= 0x309F || cp >= 0x30A0 && cp <= 0x30FF) {
            return 2;
        }
        return 1;
    }

    /** leftAlign：左对齐补空格；否则右对齐（用于数字列） */
    private static String padDisplay(String s, int targetCols, boolean leftAlign) {
        if (s == null) {
            s = "";
        }
        int w = displayWidth(s);
        if (w > targetCols) {
            return truncateToDisplayWidth(s, targetCols);
        }
        int pad = targetCols - w;
        if (leftAlign) {
            return s + " ".repeat(pad);
        }
        return " ".repeat(pad) + s;
    }

    private static String truncateToDisplayWidth(String s, int maxCols) {
        if (maxCols <= 0) {
            return "";
        }
        if (displayWidth(s) <= maxCols) {
            return s;
        }
        if (maxCols <= 3) {
            return ".".repeat(Math.min(maxCols, 3));
        }
        int ellipsis = 3;
        int budget = maxCols - ellipsis;
        StringBuilder sb = new StringBuilder();
        int w = 0;
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            int cw = charDisplayWidth(cp);
            if (w + cw > budget) {
                break;
            }
            sb.appendCodePoint(cp);
            w += cw;
            i += Character.charCount(cp);
        }
        return sb + "...";
    }

    private static String truncateLabel(String label) {
        if (label == null) {
            return "";
        }
        if (displayWidth(label) <= COL_LABEL) {
            return label;
        }
        return truncateToDisplayWidth(label, COL_LABEL);
    }

    private static String formatTableRow(String listen, String label, String activeText, String state, String speed) {
        String c1 = padDisplay(listen, COL_LISTEN, true);
        String c2 = padDisplay(label, COL_LABEL, true);
        String c3 = padDisplay(activeText, COL_ACTIVE, false);
        String c4 = padDisplay(state, COL_STATE, true);
        String c5 = padDisplay(speed, COL_SPEED, true);
        return "| " + c1 + " | " + c2 + " | " + c3 + " | " + c4 + " | " + c5 + " |";
    }

    private static String formatSpeedBps(double bps) {
        if (bps < 0 || Double.isNaN(bps) || Double.isInfinite(bps)) {
            bps = 0;
        }
        if (bps < 1024) {
            return String.format("%.0fB/s", bps);
        }
        if (bps < 1024 * 1024) {
            return String.format("%.1fKB/s", bps / 1024.0);
        }
        return String.format("%.2fMB/s", bps / 1024.0 / 1024.0);
    }

    /**
     * ↑ 上行（客户端→转发）、↓ 下行（对端→客户端），为上一刷新周期内平均速率。
     */
    private static String formatSpeedCell(int port) {
        long u = RequestTrafficRegistry.getUpstreamBytes(port);
        long d = RequestTrafficRegistry.getDownstreamBytes(port);
        RateSnap snap = RATE_SNAP.computeIfAbsent(port, x -> new RateSnap());
        long now = System.nanoTime();
        double upBps = 0;
        double downBps = 0;
        if (snap.tNanos != 0L) {
            double dt = (now - snap.tNanos) / 1_000_000_000.0;
            if (dt < 1e-9) {
                dt = 1e-9;
            }
            upBps = (u - snap.up) / dt;
            downBps = (d - snap.down) / dt;
        }
        snap.up = u;
        snap.down = d;
        snap.tNanos = now;
        return "\u2191" + formatSpeedBps(upBps) + " \u2193" + formatSpeedBps(downBps);
    }

    private static String horizontalRuleForRow(String row) {
        int w = displayWidth(row);
        if (w <= 2) {
            return "++";
        }
        return "+" + "-".repeat(w - 2) + "+";
    }

    private static void rewind(int columns) {
        for (int i = 0; i < columns; i++) {
            System.out.print('\b');
        }
    }

    private static void rewriteSameLine(String logicalLine) {
        String out = logicalLine;
        if (logicalLine.length() < lastPhysicalLen) {
            out = logicalLine + " ".repeat(lastPhysicalLen - logicalLine.length());
        }
        if (lastPhysicalLen > 0) {
            rewind(lastPhysicalLen);
        }
        System.out.print(out);
        lastPhysicalLen = out.length();
        System.out.flush();
    }

    /** ESC [ n A：光标上移 n 行；ESC [ 2 K：清当前行；CR：行首 */
    private static void redrawTableBlock(List<String> lines) {
        int h = lines.size();
        System.out.print("\u001B[" + h + "A");
        for (String line : lines) {
            System.out.print("\u001B[2K\r");
            System.out.println(line);
        }
        System.out.flush();
    }

    private static List<String> buildTableLines(List<Integer> ports) {
        List<String> lines = new ArrayList<>();
        lines.add("=== connection status (live) ===");
        if (ports.isEmpty()) {
            lines.add("  (no dict ports)");
            return lines;
        }
        String headerRow = formatTableRow("listen", "label", "active", "state", "up/down");
        String sep = horizontalRuleForRow(headerRow);
        lines.add(sep);
        lines.add(headerRow);
        lines.add(sep);
        for (int port : ports) {
            String label = truncateLabel(RequestConnectionRegistry.getLabel(port));
            int n = RequestConnectionRegistry.getActiveCount(port);
            String state = n > 0 ? "有连接" : "空闲";
            lines.add(formatTableRow(String.valueOf(port), label, String.valueOf(n), state, formatSpeedCell(port)));
        }
        lines.add(sep);
        return lines;
    }

    /** 紧凑单行（仅 {@link Mode#LINE}） */
    private static String buildSingleLine(List<Integer> ports) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int i = 0; i < ports.size(); i++) {
            if (i > 0) {
                sb.append("  ;  ");
            }
            int port = ports.get(i);
            int n = RequestConnectionRegistry.getActiveCount(port);
            String label = truncateLabel(RequestConnectionRegistry.getLabel(port));
            String state = n > 0 ? "有连接" : "空闲";
            sb.append(label).append("  ").append(port).append("  active:").append(n).append("  ").append(state);
            sb.append("  ").append(formatSpeedCell(port));
        }
        return sb.toString();
    }

    private static void printlnBlock(List<String> lines) {
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.flush();
    }

    public static void printInitialStatusSection() {
        synchronized (LOCK) {
            tableBlockLines = 0;
            lastPhysicalLen = 0;
            List<Integer> ports = RequestConnectionRegistry.getOrderedPorts();
            Mode m = mode();
            switch (m) {
                case APPEND: {
                    List<String> appendLines = buildTableLines(ports);
                    tableBlockLines = appendLines.size();
                    printlnBlock(appendLines);
                    break;
                }
                case LINE:
                    System.out.println("=== connection status (live) ===");
                    if (ports.isEmpty()) {
                        System.out.println("  (no dict ports)");
                        System.out.flush();
                        return;
                    }
                    rewriteSameLine(buildSingleLine(ports));
                    break;
                default: {
                    List<String> tableLines = buildTableLines(ports);
                    tableBlockLines = tableLines.size();
                    printlnBlock(tableLines);
                    break;
                }
            }
        }
    }

    public static void refresh() {
        synchronized (LOCK) {
            List<Integer> ports = RequestConnectionRegistry.getOrderedPorts();
            if (ports.isEmpty()) {
                return;
            }
            Mode m = mode();
            switch (m) {
                case APPEND:
                    System.out.println();
                    printlnBlock(buildTableLines(ports));
                    break;
                case LINE:
                    rewriteSameLine(buildSingleLine(ports));
                    break;
                default: {
                    List<String> tableLines = buildTableLines(ports);
                    int newH = tableLines.size();
                    if (tableBlockLines <= 0 || newH != tableBlockLines) {
                        tableBlockLines = newH;
                        printlnBlock(tableLines);
                        return;
                    }
                    redrawTableBlock(tableLines);
                    break;
                }
            }
        }
    }

    /** 每秒刷新一次表格，使速率列随流量变化更新（与连接数变化触发的 {@link #refresh()} 叠加无妨）。 */
    public static void startPeriodicRefresh() {
        if (periodicRefreshStarted) {
            return;
        }
        synchronized (LOCK) {
            if (periodicRefreshStarted) {
                return;
            }
            periodicRefreshStarted = true;
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "forwarding-console-refresh");
                t.setDaemon(true);
                return t;
            }).scheduleAtFixedRate(() -> {
                try {
                    refresh();
                } catch (Throwable ignored) {
                }
            }, 1L, 1L, TimeUnit.SECONDS);
        }
    }
}
