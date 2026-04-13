package org.wlpiaoyi.framework.forwarding.utils.socket;

import org.wlpiaoyi.framework.utils.ValueUtils;

/**
 * Debug-friendly payload summaries (hex preview + lengths) for forwarding logs.
 */
public final class ForwardingLog {

    /** Default number of raw bytes to show as hex in log lines. */
    public static final int DEFAULT_HEX_PREVIEW_BYTES = 32;

    private ForwardingLog() {}

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

    public static String hexPreview(byte[] data) {
        if (data == null) {
            return "(null)";
        }
        return hexPreview(data, 0, data.length, DEFAULT_HEX_PREVIEW_BYTES);
    }
}
