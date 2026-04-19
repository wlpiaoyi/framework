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
 * 以 ASCII 表格形式每秒刷新各监听端口的状态、连接数、上下行速率，不依赖 Logback 追加器。
 * </p>
 * <p>
 * 列宽按「终端显示宽度」计算（CJK 等宽字符计为 2），避免 Windows 控制台中英混排错位。
 * 默认使用 {@code ESC[2J ESC[H} 清屏重绘；若终端支持 VT（如 Windows Terminal），可加上 JVM 参数
 * {@code -Dforwarding.console.vt=true} 进入备用屏幕缓冲区，避免污染主屏历史。
 * </p>
 */
public final class ForwardingConsoleDashboard {

    /** 控制是否使用 VT 备用屏幕缓冲区的系统属性名 */
    private static final String PROP_VT = "forwarding.console.vt";

    /** 定时任务调度器 */
    private static final AtomicReference<ScheduledExecutorService> SCHED = new AtomicReference<>();

    /** 已执行 {@code ESC[?1049h} 进入备用屏时为 true */
    private static final AtomicBoolean VT_ALT_ACTIVE = new AtomicBoolean(false);

    /** 是否已注册 JVM shutdown hook 恢复 VT 屏幕 */
    private static final AtomicBoolean VT_SHUTDOWN_HOOK = new AtomicBoolean(false);

    /** 各列内容区显示宽度（不含边框竖线） */
    private static final int W_NAME = 30;
    private static final int W_PORT = 8;
    private static final int W_STATE = 10;
    private static final int W_CONN = 8;
    private static final int W_UP = 14;
    private static final int W_DOWN = 14;

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
        try {
            ForwardingPortStats.tickRates();
            List<ForwardingPortStats.Row> rows = ForwardingPortStats.snapshotRowsSorted();
            StringBuilder sb = new StringBuilder(rows.size() * 96 + 256);
            if (vtConsoleEnabled()) {
                enterVtAlternateScreenOnce();
                sb.append("\033[H");
            } else {
                sb.append("\033[2J\033[H");
            }
            String title = "转发端口监控  (logEnabled=false)  hub=" + ForwardUtils.getResponseServerAddress();
            sb.append(title).append('\n');
            sb.append(repeat('─', displayWidth(title))).append('\n');

            appendTableTop(sb);
            appendHeaderRow(sb);
            appendTableSep(sb);
            for (ForwardingPortStats.Row r : rows) {
                appendDataRow(sb, r);
            }
            appendTableBottom(sb);

            if (vtConsoleEnabled()) {
                sb.append("\033[0J");
            }

            System.out.print(sb);
            System.out.flush();
        } catch (Throwable ignored) {
            // 静默模式避免抛到未捕获处理器刷屏
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
                .append(repeat('─', W_DOWN))
                .append('┐').append('\n');
    }

    private static void appendTableSep(StringBuilder sb) {
        sb.append('├')
                .append(repeat('─', W_NAME)).append('┼')
                .append(repeat('─', W_PORT)).append('┼')
                .append(repeat('─', W_STATE)).append('┼')
                .append(repeat('─', W_CONN)).append('┼')
                .append(repeat('─', W_UP)).append('┼')
                .append(repeat('─', W_DOWN))
                .append('┤').append('\n');
    }

    private static void appendTableBottom(StringBuilder sb) {
        sb.append('└')
                .append(repeat('─', W_NAME)).append('┴')
                .append(repeat('─', W_PORT)).append('┴')
                .append(repeat('─', W_STATE)).append('┴')
                .append(repeat('─', W_CONN)).append('┴')
                .append(repeat('─', W_UP)).append('┴')
                .append(repeat('─', W_DOWN))
                .append('┘').append('\n');
    }

    private static void appendHeaderRow(StringBuilder sb) {
        sb.append('│')
                .append(padRightDisplay("转发端口名称", W_NAME)).append('│')
                .append(padRightDisplay("转发端口", W_PORT)).append('│')
                .append(padRightDisplay("服务状态", W_STATE)).append('│')
                .append(padRightDisplay("连接数", W_CONN)).append('│')
                .append(padRightDisplay("上行速率", W_UP)).append('│')
                .append(padRightDisplay("下行速率", W_DOWN))
                .append('│').append('\n');
    }

    private static void appendDataRow(StringBuilder sb, ForwardingPortStats.Row r) {
        sb.append('│')
                .append(padRightDisplay(truncateToDisplayWidth(r.name, W_NAME), W_NAME)).append('│')
                .append(padRightDisplay(Integer.toString(r.port), W_PORT)).append('│')
                .append(padRightDisplay(stateLabel(r.listenState), W_STATE)).append('│')
                .append(padRightDisplay(Integer.toString(r.connections.get()), W_CONN)).append('│')
                .append(padRightDisplay(formatRate(r.upRateBps), W_UP)).append('│')
                .append(padRightDisplay(formatRate(r.downRateBps), W_DOWN))
                .append('│').append('\n');
    }

    /** 将 ListenState 枚举转为中文标签 */
    private static String stateLabel(ForwardingPortStats.ListenState s) {
        return switch (s) {
            case RUNNING -> "运行中";
            case PENDING -> "启动中";
            case STOPPED -> "已停止";
        };
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
            return String.format(Locale.ROOT, "%.1f KB/s", bps / 1024.0);
        }
        return String.format(Locale.ROOT, "%.2f MB/s", bps / (1024.0 * 1024.0));
    }

    // --- 显示宽度计算（与常见 East Asian 终端一致：宽字符宽度 2） ---

    private static int displayWidth(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        int w = 0;
        for (int i = 0; i < s.length(); ) {
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

    /** 截断到不超过 maxDw 显示宽度（不补空格，供外层再 pad） */
    private static String truncateToDisplayWidth(String s, int maxDw) {
        if (s == null || maxDw <= 0) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        int w = 0;
        for (int i = 0; i < s.length(); ) {
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
