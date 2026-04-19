package org.wlpiaoyi.framework.forwarding.utils.socket;

import org.wlpiaoyi.framework.utils.ValueUtils;

/**
 * 转发框架的调试日志辅助类。
 * <p>
 * 提供二进制数据的十六进制预览功能，便于在日志中排查粘包、拆包、协议异常等问题。
 * 当数据量较大时，仅显示前 N 字节并在末尾标注总长度，避免日志膨胀。
 * </p>
 */
public final class ForwardingLog {

    /** 默认预览的最大原始字节数（用于无参重载） */
    public static final int DEFAULT_HEX_PREVIEW_BYTES = 32;

    private ForwardingLog() {
        // 工具类禁止实例化
    }

    /**
     * 将字节数组片段转为 HEX 预览字符串。
     *
     * @param data           字节数组
     * @param off            起始偏移量
     * @param len            有效长度
     * @param maxPreviewBytes 最大预览字节数，超出部分以 "… total=XXB" 代替
     * @return HEX 字符串，例如 "48656c6c6f… total=100B"
     */
    public static String hexPreview(byte[] data, int off, int len, int maxPreviewBytes) {
        if (data == null || len <= 0) {
            return "(empty)";
        }
        int cap = Math.max(1, maxPreviewBytes);
        int n = Math.min(len, cap);
        byte[] slice = new byte[n];
        System.arraycopy(data, off, slice, 0, n);
        String hex = ValueUtils.bytesToHex(slice);
        if (len > n) {
            return hex + "… total=" + len + "B";
        }
        return hex;
    }

    /**
     * 将完整字节数组转为 HEX 预览字符串（使用默认预览长度 {@link #DEFAULT_HEX_PREVIEW_BYTES}）。
     *
     * @param data 字节数组
     * @return HEX 字符串；若 data 为 null 返回 "(null)"
     */
    public static String hexPreview(byte[] data) {
        if (data == null) {
            return "(null)";
        }
        return hexPreview(data, 0, data.length, DEFAULT_HEX_PREVIEW_BYTES);
    }
}
