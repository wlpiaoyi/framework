package org.wlpiaoyi.framework.forwarding.utils.socket;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 静默模式（{@code logEnabled=false}）下，request 侧控制台的实时监控面板。
 * <p>
 * 以 ASCII 表格形式每秒刷新各监听端口的状态、连接数、上下行速率、累计流量及异常数，不依赖 Logback 追加器。
 * 支持斑马纹、速率微型直方图、标题居中、数字右对齐、零值灰显、表头加粗等视觉优化。
 * </p>
 * <p>
 * 列宽按「终端显示宽度」计算（CJK 等宽字符计为 2），避免 Windows 控制台中英混排错位。
 * 默认使用 {@code ESC[2J ESC[H} 清屏重绘；若终端支持 VT（如 Windows Terminal），可加上 JVM 参数
 * {@code -Dforwarding.console.vt=true} 进入备用屏幕缓冲区，避免污染主屏历史。
 * </p>
 * <p>
 * 颜色输出默认开启，可通过 {@code -Dforwarding.console.color=false} 关闭。
 * </p>
 */
public final class ForwardingConsoleDashboard {

    /** 控制是否使用 VT 备用屏幕缓冲区的系统属性名 */
    private static final String PROP_VT = "forwarding.console.vt";
    /** 控制是否启用 ANSI 颜色的系统属性名 */
    private static final String PROP_COLOR = "forwarding.console.color";

    /** 定时任务调度器 */
    private static final AtomicReference<ScheduledExecutorService> SCHED = new AtomicReference<>();

    /** 已执行 {@code ESC[?1049h} 进入备用屏时为 true */
    private static final AtomicBoolean VT_ALT_ACTIVE = new AtomicBoolean(false);

    /** 是否已注册 JVM shutdown hook 恢复 VT 屏幕 */
    private static final AtomicBoolean VT_SHUTDOWN_HOOK = new AtomicBoolean(false);

    /** 防止 renderOnce 重入 */
    private static final AtomicBoolean RENDERING = new AtomicBoolean(false);

    /** 面板启动时间戳 */
    private static final long START_TIME = System.currentTimeMillis();

    /** 上次异常日志时间（纳秒），用于退避打印 */
    private static volatile long lastErrorLogNanos = 0L;
    /** 异常退避间隔（30 秒） */
    private static final long ERROR_BACKOFF_NANOS = 30_000_000_000L;

    // --- 各列内容区显示宽度（不含边框竖线）---
    private static final int W_NAME = 20;
    private static final int W_PORT = 9;
    private static final int W_STATE = 8;
    private static final int W_CONN = 6;
    private static final int W_UP = 12;
    private static final int W_DOWN = 12;
    private static final int W_TOTAL_UP = 10;
    private static final int W_TOTAL_DOWN = 10;
    private static final int W_PEAK = 6;
    private static final int W_ERR = 6;

    // 表格总显示宽度 = 内容宽 + 边框竖线（列数 + 1）
    private static final int TABLE_TOTAL_WIDTH = W_NAME + W_PORT + W_STATE + W_CONN + W_UP + W_DOWN
            + W_TOTAL_UP + W_TOTAL_DOWN + W_PEAK + W_ERR + 11;

    // --- ANSI 颜色码 ---
    private static final String CLR_RESET = "\033[0m";
    private static final String CLR_GREEN = "\033[32m";
    private static final String CLR_RED = "\033[31m";
    private static final String CLR_YELLOW = "\033[33m";
    private static final String CLR_GRAY = "\033[90m";
    private static final String CLR_BOLD = "\033[1m";
    /** 斑马纹偶数行背景色（极淡灰） */
    private static final String CLR_ZEBRA_BG = "\033[48;5;235m";

    private ForwardingConsoleDashboard() {
        // 工具类禁止实例化
    }

    /**
     * 启动控制台监控面板。
     * <p>
     * 若当前为日志模式（{@code logEnabled=true}）则直接返回。
     * 以守护线程方式每秒调度 {@link #renderOnce()}。
     * </p>
     */
    public static synchronized void start() {
        if (ForwardUtils.isLogEnabled()) {
            return;
        }
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "forwarding-console-dashboard");
            t.setDaemon(true);
            return t;
        });
        ScheduledExecutorService old = SCHED.getAndSet(ex);
        if (old != null) {
            old.shutdownNow();
        }
        ex.scheduleAtFixedRate(ForwardingConsoleDashboard::renderOnce, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 停止控制台监控面板，若处于 VT 备用屏则恢复主屏。
     */
    public static synchronized void stop() {
        ScheduledExecutorService ex = SCHED.getAndSet(null);
        if (ex != null) {
            ex.shutdownNow();
        }
        leaveVtAlternateScreenIfActive();
    }

    /** 判断是否启用 VT 备用屏 */
    private static boolean vtConsoleEnabled() {
        return Boolean.parseBoolean(System.getProperty(PROP_VT, "false"));
    }

    /** 判断是否启用 ANSI 颜色 */
    private static boolean colorEnabled() {
        return Boolean.parseBoolean(System.getProperty(PROP_COLOR, "true"));
    }

    /** 进入 VT 备用屏幕缓冲区（仅执行一次） */
    private static void enterVtAlternateScreenOnce() {
        if (!vtConsoleEnabled()) {
            return;
        }
        if (VT_ALT_ACTIVE.compareAndSet(false, true)) {
            System.out.print("\033[?1049h\033[?25l");
            System.out.flush();
            installVtShutdownHookOnce();
        }
    }

    /** 离开 VT 备用屏幕缓冲区（若当前处于备用屏） */
    private static void leaveVtAlternateScreenIfActive() {
        if (VT_ALT_ACTIVE.compareAndSet(true, false)) {
            System.out.print("\033[?25h\033[?1049l");
            System.out.flush();
        }
    }

    /** 注册 shutdown hook 确保 JVM 退出时恢复 VT 主屏 */
    private static void installVtShutdownHookOnce() {
        if (VT_SHUTDOWN_HOOK.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (VT_ALT_ACTIVE.get()) {
                    System.out.print("\033[?25h\033[?1049l");
                    System.out.flush();
                    VT_ALT_ACTIVE.set(false);
                }
            }, "forwarding-console-vt-restore"));
        }
    }

    /** 单次渲染：计算速率 → 取快照 → 绘制表格 → 输出到控制台 */
    private static void renderOnce() {
        if (!RENDERING.compareAndSet(false, true)) {
            return; // 上次渲染尚未结束，跳过本次防止堆叠
        }
        try {
            ForwardingPortStats.tickRates();
            List<ForwardingPortStats.Row> rows = ForwardingPortStats.snapshotRowsSorted();
            StringBuilder sb = new StringBuilder(rows.size() * 128 + 256);
            if (vtConsoleEnabled()) {
                enterVtAlternateScreenOnce();
                sb.append("\033[H");
            } else {
                sb.append("\033[2J\033[H");
            }
            String title = "转发端口监控  (logEnabled=false)  hub=" + ForwardUtils.getResponseServerAddress();
            sb.append(centerWithDecor(title)).append('\n');
            sb.append(centerWithDecor("运行时长 " + formatUptime())).append('\n');

            appendTableTop(sb);
            appendHeaderRow(sb);
            appendTableSep(sb);
            for (int i = 0; i < rows.size(); i++) {
                appendDataRow(sb, rows.get(i), i);
            }
            if (!rows.isEmpty()) {
                appendTableSep(sb);
                appendSummaryRow(sb, rows);
            }
            appendTableBottom(sb);

            if (vtConsoleEnabled()) {
                sb.append("\033[0J");
            }

            System.out.print(sb);
            System.out.flush();
        } catch (Throwable t) {
            long now = System.nanoTime();
            if (now - lastErrorLogNanos > ERROR_BACKOFF_NANOS) {
                lastErrorLogNanos = now;
                System.err.println("[dashboard] render error: " + t);
            }
        } finally {
            RENDERING.set(false);
        }
    }

    // --- 表格边框绘制 ---

    private static void appendTableTop(StringBuilder sb) {
        sb.append('┌')
                .append(repeat('─', W_NAME)).append('┬')
                .append(repeat('─', W_PORT)).append('┬')
                .append(repeat('─', W_STATE)).append('┬')
                .append(repeat('─', W_CONN)).append('┬')
                .append(repeat('─', W_UP)).append('┬')
                .append(repeat('─', W_DOWN)).append('┬')
                .append(repeat('─', W_TOTAL_UP)).append('┬')
                .append(repeat('─', W_TOTAL_DOWN)).append('┬')
                .append(repeat('─', W_PEAK)).append('┬')
                .append(repeat('─', W_ERR))
                .append('┐').append('\n');
    }

    private static void appendTableSep(StringBuilder sb) {
        sb.append('├')
                .append(repeat('─', W_NAME)).append('┼')
                .append(repeat('─', W_PORT)).append('┼')
                .append(repeat('─', W_STATE)).append('┼')
                .append(repeat('─', W_CONN)).append('┼')
                .append(repeat('─', W_UP)).append('┼')
                .append(repeat('─', W_DOWN)).append('┼')
                .append(repeat('─', W_TOTAL_UP)).append('┼')
                .append(repeat('─', W_TOTAL_DOWN)).append('┼')
                .append(repeat('─', W_PEAK)).append('┼')
                .append(repeat('─', W_ERR))
                .append('┤').append('\n');
    }

    private static void appendTableBottom(StringBuilder sb) {
        sb.append('└')
                .append(repeat('─', W_NAME)).append('┴')
                .append(repeat('─', W_PORT)).append('┴')
                .append(repeat('─', W_STATE)).append('┴')
                .append(repeat('─', W_CONN)).append('┴')
                .append(repeat('─', W_UP)).append('┴')
                .append(repeat('─', W_DOWN)).append('┴')
                .append(repeat('─', W_TOTAL_UP)).append('┴')
                .append(repeat('─', W_TOTAL_DOWN)).append('┴')
                .append(repeat('─', W_PEAK)).append('┴')
                .append(repeat('─', W_ERR))
                .append('┘').append('\n');
    }

    private static void appendHeaderRow(StringBuilder sb) {
        String b = colorEnabled() ? CLR_BOLD : "";
        String r = colorEnabled() ? CLR_RESET : "";
        sb.append('│')
                .append(padRightDisplay(b + "转发端口名称" + r, W_NAME)).append('│')
                .append(padRightDisplay(b + "转发端口" + r, W_PORT)).append('│')
                .append(padRightDisplay(b + "服务状态" + r, W_STATE)).append('│')
                .append(padRightDisplay(b + "连接数" + r, W_CONN)).append('│')
                .append(padRightDisplay(b + "上行速率" + r, W_UP)).append('│')
                .append(padRightDisplay(b + "下行速率" + r, W_DOWN)).append('│')
                .append(padRightDisplay(b + "累计上行" + r, W_TOTAL_UP)).append('│')
                .append(padRightDisplay(b + "累计下行" + r, W_TOTAL_DOWN)).append('│')
                .append(padRightDisplay(b + "峰值连接" + r, W_PEAK)).append('│')
                .append(padRightDisplay(b + "异常数" + r, W_ERR))
                .append('│').append('\n');
    }

    private static void appendDataRow(StringBuilder sb, ForwardingPortStats.Row r, int rowIndex) {
        boolean ce = colorEnabled();
        boolean zebra = (rowIndex % 2 == 1) && ce;
        String bg = zebra ? CLR_ZEBRA_BG : "";
        String reset = zebra ? CLR_RESET : "";

        String state = stateLabel(r.listenState);
        String stateColored = ce ? colorizeState(state, r.listenState) : state;

        int conn = r.connections.get();
        String connStr = intGrayIfZero(conn, ce);

        // 上行速率 + 微型条
        String upRate = formatRate(r.upRateBps);
        char upBar = rateBar(r.upRateBps);
        if (upBar != ' ' && displayWidth(upRate) < W_UP) {
            upRate = upRate + upBar;
        }
        String upRateColored = doubleGrayIfZero(upRate, r.upRateBps, ce);

        // 下行速率 + 微型条
        String downRate = formatRate(r.downRateBps);
        char downBar = rateBar(r.downRateBps);
        if (downBar != ' ' && displayWidth(downRate) < W_DOWN) {
            downRate = downRate + downBar;
        }
        String downRateColored = doubleGrayIfZero(downRate, r.downRateBps, ce);

        long upBytes = r.bytesUp.sum();
        String upBytesStr = formatBytes(upBytes);
        String upBytesColored = longGrayIfZero(upBytesStr, upBytes, ce);

        long downBytes = r.bytesDown.sum();
        String downBytesStr = formatBytes(downBytes);
        String downBytesColored = longGrayIfZero(downBytesStr, downBytes, ce);

        int peak = r.peakConnections;
        String peakStr = intGrayIfZero(peak, ce);

        long err = r.errors.sum();
        String errColored;
        if (ce) {
            if (err > 0) errColored = CLR_RED + err + CLR_RESET;
            else errColored = CLR_GRAY + err + CLR_RESET;
        } else {
            errColored = Long.toString(err);
        }

        sb.append(bg).append('│')
                .append(padRightDisplay(truncateToDisplayWidth(r.name, W_NAME), W_NAME)).append('│')
                .append(padLeftDisplay(Integer.toString(r.port), W_PORT)).append('│')
                .append(padRightDisplay(stateColored, W_STATE)).append('│')
                .append(padLeftDisplay(connStr, W_CONN)).append('│')
                .append(padLeftDisplay(upRateColored, W_UP)).append('│')
                .append(padLeftDisplay(downRateColored, W_DOWN)).append('│')
                .append(padLeftDisplay(upBytesColored, W_TOTAL_UP)).append('│')
                .append(padLeftDisplay(downBytesColored, W_TOTAL_DOWN)).append('│')
                .append(padLeftDisplay(peakStr, W_PEAK)).append('│')
                .append(padLeftDisplay(errColored, W_ERR))
                .append('│').append('\n').append(reset);
    }

    private static void appendSummaryRow(StringBuilder sb, List<ForwardingPortStats.Row> rows) {
        long totalConn = 0;
        long totalUp = 0;
        long totalDown = 0;
        long totalErr = 0;
        for (ForwardingPortStats.Row r : rows) {
            totalConn += r.connections.get();
            totalUp += r.bytesUp.sum();
            totalDown += r.bytesDown.sum();
            totalErr += r.errors.sum();
        }

        String b = colorEnabled() ? CLR_BOLD : "";
        String re = colorEnabled() ? CLR_RESET : "";

        sb.append('│')
                .append(padRightDisplay(b + "合计" + re, W_NAME)).append('│')
                .append(padLeftDisplay("", W_PORT)).append('│')
                .append(padRightDisplay("", W_STATE)).append('│')
                .append(padLeftDisplay(b + totalConn + re, W_CONN)).append('│')
                .append(padLeftDisplay("", W_UP)).append('│')
                .append(padLeftDisplay("", W_DOWN)).append('│')
                .append(padLeftDisplay(formatBytes(totalUp), W_TOTAL_UP)).append('│')
                .append(padLeftDisplay(formatBytes(totalDown), W_TOTAL_DOWN)).append('│')
                .append(padLeftDisplay("", W_PEAK)).append('│')
                .append(padLeftDisplay(b + totalErr + re, W_ERR))
                .append('│').append('\n');
    }

    // --- 颜色辅助 ---

    private static String intGrayIfZero(int value, boolean ce) {
        if (!ce || value != 0) return Integer.toString(value);
        return CLR_GRAY + value + CLR_RESET;
    }

    private static String longGrayIfZero(String text, long value, boolean ce) {
        if (!ce || value != 0) return text;
        return CLR_GRAY + text + CLR_RESET;
    }

    private static String doubleGrayIfZero(String text, double value, boolean ce) {
        if (!ce || value != 0) return text;
        return CLR_GRAY + text + CLR_RESET;
    }

    /** 将 ListenState 枚举转为中文标签 */
    private static String stateLabel(ForwardingPortStats.ListenState s) {
        return switch (s) {
            case RUNNING -> "运行中";
            case PENDING -> "启动中";
            case STOPPED -> "已停止";
        };
    }

    /** 给状态文字加上 ANSI 颜色 */
    private static String colorizeState(String label, ForwardingPortStats.ListenState s) {
        return switch (s) {
            case RUNNING -> CLR_GREEN + label + CLR_RESET;
            case PENDING -> CLR_YELLOW + label + CLR_RESET;
            case STOPPED -> CLR_RED + label + CLR_RESET;
        };
    }

    // --- 格式化辅助 ---

    /** 根据速率返回微型直方图条字符（利用列内剩余空间显示） */
    private static char rateBar(double bps) {
        if (bps <= 0) return ' ';
        if (bps < 10 * 1024) return '▏';
        if (bps < 100 * 1024) return '▎';
        if (bps < 1024 * 1024) return '▍';
        if (bps < 10 * 1024 * 1024) return '▋';
        return '█';
    }

    /** 将速率（字节/秒）格式化为人类可读字符串 */
    private static String formatRate(double bps) {
        if (bps < 0) {
            bps = 0;
        }
        if (bps < 1024) {
            return String.format(Locale.ROOT, "%.0f B/s", bps);
        }
        if (bps < 1024 * 1024) {
            return String.format(Locale.ROOT, "%.1f K/s", bps / 1024.0);
        }
        return String.format(Locale.ROOT, "%.2f M/s", bps / (1024.0 * 1024.0));
    }

    /** 将累计字节格式化为紧凑的人类可读字符串 */
    private static String formatBytes(long bytes) {
        if (bytes < 0) bytes = 0;
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024L * 1024L) {
            return String.format(Locale.ROOT, "%.1f K", bytes / 1024.0);
        }
        if (bytes < 1024L * 1024L * 1024L) {
            return String.format(Locale.ROOT, "%.2f M", bytes / (1024.0 * 1024.0));
        }
        return String.format(Locale.ROOT, "%.2f G", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    /** 格式化运行时长为 mm:ss 或 hh:mm:ss */
    private static String formatUptime() {
        long sec = (System.currentTimeMillis() - START_TIME) / 1000;
        long h = sec / 3600;
        long m = (sec % 3600) / 60;
        long s = sec % 60;
        if (h > 0) {
            return String.format(Locale.ROOT, "%02d:%02d:%02d", h, m, s);
        }
        return String.format(Locale.ROOT, "%02d:%02d", m, s);
    }

    /** 将文本在表格总宽度内居中，左右用 ─ 填充 */
    private static String centerWithDecor(String text) {
        int tw = displayWidth(text);
        if (tw >= TABLE_TOTAL_WIDTH) return text;
        int pad = TABLE_TOTAL_WIDTH - tw;
        int left = pad / 2;
        int right = pad - left;
        return repeat('─', left) + text + repeat('─', right);
    }

    // --- 显示宽度计算（与常见 East Asian 终端一致：宽字符宽度 2）---
    // 自动忽略 ANSI 转义序列，避免颜色码干扰对齐

    private static int displayWidth(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        int w = 0;
        for (int i = 0; i < s.length(); ) {
            char c = s.charAt(i);
            // 跳过 ANSI 转义序列 \033[...m
            if (c == '\033' && i + 1 < s.length() && s.charAt(i + 1) == '[') {
                int j = i + 2;
                while (j < s.length()) {
                    char ch = s.charAt(j);
                    if ((ch >= '0' && ch <= '9') || ch == ';' || ch == '?' || ch == '=') {
                        j++;
                    } else {
                        j++; // 消费终止字母
                        break;
                    }
                }
                i = j;
                continue;
            }
            int cp = s.codePointAt(i);
            i += Character.charCount(cp);
            w += eastAsianDisplayWidth(cp);
        }
        return w;
    }

    private static int eastAsianDisplayWidth(int cp) {
        if (cp < 0x20) {
            return 0;
        }
        if (cp < 0x7F) {
            return 1;
        }
        Character.UnicodeBlock block = Character.UnicodeBlock.of(cp);
        if (block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            // U+FF01–FF5E 全角 ASCII、FF5F 等通常占 2 列
            return (cp >= 0xFF01 && cp <= 0xFF60) || (cp >= 0xFFE0 && cp <= 0xFFE6) ? 2 : 1;
        }
        Character.UnicodeScript sc = Character.UnicodeScript.of(cp);
        if (sc == Character.UnicodeScript.HAN
                || sc == Character.UnicodeScript.HIRAGANA
                || sc == Character.UnicodeScript.KATAKANA
                || sc == Character.UnicodeScript.HANGUL) {
            return 2;
        }
        if (block != null) {
            String n = block.toString();
            if (n.startsWith("CJK") || n.contains("HIRAGANA") || n.contains("KATAKANA") || n.contains("HANGUL")) {
                return 2;
            }
            if (block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || block == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                    || (block == Character.UnicodeBlock.GENERAL_PUNCTUATION && cp >= 0x2010 && cp <= 0x2015)) {
                return 2;
            }
        }
        return 1;
    }

    /** 右补空格到目标显示宽度 */
    private static String padRightDisplay(String s, int targetDw) {
        if (s == null) {
            s = "";
        }
        int dw = displayWidth(s);
        if (dw > targetDw) {
            return truncateToDisplayWidth(s, targetDw);
        }
        return s + repeat(' ', targetDw - dw);
    }

    /** 左补空格到目标显示宽度（数字列专用） */
    private static String padLeftDisplay(String s, int targetDw) {
        if (s == null) {
            s = "";
        }
        int dw = displayWidth(s);
        if (dw > targetDw) {
            return truncateToDisplayWidth(s, targetDw);
        }
        return repeat(' ', targetDw - dw) + s;
    }

    /** 截断到不超过 maxDw 显示宽度（不补空格，供外层再 pad） */
    private static String truncateToDisplayWidth(String s, int maxDw) {
        if (s == null || maxDw <= 0) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        int w = 0;
        for (int i = 0; i < s.length(); ) {
            char c = s.charAt(i);
            // 保留 ANSI 转义序列（不截断颜色码）
            if (c == '\033' && i + 1 < s.length() && s.charAt(i + 1) == '[') {
                int j = i + 2;
                while (j < s.length()) {
                    char ch = s.charAt(j);
                    if ((ch >= '0' && ch <= '9') || ch == ';' || ch == '?' || ch == '=') {
                        j++;
                    } else {
                        j++;
                        break;
                    }
                }
                out.append(s, i, j);
                i = j;
                continue;
            }
            int cp = s.codePointAt(i);
            int cw = eastAsianDisplayWidth(cp);
            if (w + cw > maxDw) {
                break;
            }
            w += cw;
            out.appendCodePoint(cp);
            i += Character.charCount(cp);
        }
        return out.toString();
    }

    /** 重复字符 n 次 */
    private static String repeat(char c, int n) {
        if (n <= 0) {
            return "";
        }
        char[] arr = new char[n];
        Arrays.fill(arr, c);
        return new String(arr);
    }
}
