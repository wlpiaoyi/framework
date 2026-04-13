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
 * {@code logEnabled:false} 且运行于 request 侧时，在控制台周期性刷新固定列表格（不依赖 Logback 追加器）。
 * <p>
 * 列宽按「终端显示宽度」计算（CJK 等宽字符计为 2），避免 Windows 控制台中英混排错位。
 * <p>
 * 默认使用 {@code ESC[2J ESC[H} 清屏重绘，向上滚动仍可能看到历史帧。若终端支持 VT（如 Windows Terminal、新版 conhost），可加上 JVM 参数
 * {@code -Dforwarding.console.vt=true}：进入备用屏幕缓冲区并仅在当前屏内重绘（{@code ESC[H} + {@code ESC[0J}），退出时恢复主屏与光标。
 */
public final class ForwardingConsoleDashboard {

    private static final String PROP_VT = "forwarding.console.vt";

    private static final AtomicReference<ScheduledExecutorService> SCHED = new AtomicReference<>();

    /** 已执行 {@code ESC[?1049h} 进入备用屏时为 true */
    private static final AtomicBoolean VT_ALT_ACTIVE = new AtomicBoolean(false);

    private static final AtomicBoolean VT_SHUTDOWN_HOOK = new AtomicBoolean(false);

    /** 各列内容区显示宽度（不含边框竖线） */
    private static final int W_NAME = 30;
    private static final int W_PORT = 8;
    private static final int W_STATE = 10;
    private static final int W_CONN = 8;
    private static final int W_UP = 14;
    private static final int W_DOWN = 14;

    private ForwardingConsoleDashboard() {
    }

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

    public static synchronized void stop() {
        ScheduledExecutorService ex = SCHED.getAndSet(null);
        if (ex != null) {
            ex.shutdownNow();
        }
        leaveVtAlternateScreenIfActive();
    }

    private static boolean vtConsoleEnabled() {
        return Boolean.parseBoolean(System.getProperty(PROP_VT, "false"));
    }

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

    private static void leaveVtAlternateScreenIfActive() {
        if (VT_ALT_ACTIVE.compareAndSet(true, false)) {
            System.out.print("\033[?25h\033[?1049l");
            System.out.flush();
        }
    }

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

    private static String stateLabel(ForwardingPortStats.ListenState s) {
        return switch (s) {
            case RUNNING -> "运行中";
            case PENDING -> "启动中";
            case STOPPED -> "已停止";
        };
    }

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

    // --- 显示宽度（与常见 East Asian 终端一致：宽字符宽度 2）---

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

    private static String repeat(char c, int n) {
        if (n <= 0) {
            return "";
        }
        char[] arr = new char[n];
        Arrays.fill(arr, c);
        return new String(arr);
    }
}
