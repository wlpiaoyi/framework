package org.wlpiaoyi.framework.forwarding.utils.socket;

import org.slf4j.Logger;

/**
 * 仅用于 forwarding：默认不打 payload；仅在 TRACE 级别输出有限长度的十六进制预览，避免刷屏与泄密。
 */
public final class ForwardingLog {

    private static final int PREVIEW_HEX_BYTES = 24;

    private ForwardingLog() {
    }

    private static String hexPreview(byte[] b, int maxBytes) {
        if (b == null) return "";
        int n = Math.min(b.length, Math.max(0, maxBytes));
        StringBuilder sb = new StringBuilder(n * 2 + 4);
        for (int i = 0; i < n; i++) {
            int v = b[i] & 0xFF;
            if (v < 16) sb.append('0');
            sb.append(Integer.toHexString(v));
        }
        if (b.length > n) sb.append("…");
        return sb.toString();
    }

    public static void traceCipher(Logger log, String scope, int clientId, int messageId, String host, int port, byte[] cipher) {
        if (!log.isTraceEnabled()) return;
        log.trace("[{}] clientId={} messageId={} -> {}:{} cipherLen={} hexPreview={}",
                scope, clientId, messageId, host, port, cipher == null ? 0 : cipher.length, hexPreview(cipher, PREVIEW_HEX_BYTES));
    }

    public static void tracePlain(Logger log, String scope, int clientId, int messageId, String host, int port, byte[] plain) {
        if (!log.isTraceEnabled()) return;
        log.trace("[{}] clientId={} messageId={} -> {}:{} plainLen={} hexPreview={}",
                scope, clientId, messageId, host, port, plain == null ? 0 : plain.length, hexPreview(plain, PREVIEW_HEX_BYTES));
    }

    public static void traceChunk(Logger log, String scope, int clientId, int messageId, int chunkLen, String targetHost, int targetPort) {
        if (!log.isTraceEnabled()) return;
        log.trace("[{}] clientId={} messageId={} chunkLen={} target={}:{}", scope, clientId, messageId, chunkLen, targetHost, targetPort);
    }

    public static void traceResponseChunk(Logger log, String scope, int clientId, int messageId, int chunkLen, String peerHost, int peerPort) {
        if (!log.isTraceEnabled()) return;
        log.trace("[{}] clientId={} messageId={} chunkLen={} peer={}:{}", scope, clientId, messageId, chunkLen, peerHost, peerPort);
    }
}
