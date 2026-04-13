package org.wlpiaoyi.framework.utils.socket;

import java.net.SocketException;
import java.nio.channels.ClosedChannelException;

/**
 * Normal teardown of TCP channels often throws {@link SocketException} etc.; these are not application faults.
 */
public final class SocketQuietErrors {

    private SocketQuietErrors() {}

    public static boolean isBenignClose(Throwable e) {
        while (e != null) {
            if (e instanceof ClosedChannelException) {
                return true;
            }
            if (e instanceof SocketException) {
                String m = e.getMessage();
                if (m != null) {
                    String lower = m.toLowerCase();
                    if (lower.contains("socket closed") || lower.contains("connection reset")
                            || lower.contains("broken pipe")) {
                        return true;
                    }
                }
            }
            e = e.getCause();
        }
        return false;
    }
}
