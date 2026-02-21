package org.wlpiaoyi.framework.forwarding.response;

import lombok.Getter;
import org.wlpiaoyi.framework.forwarding.utils.socket.Security;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-19 19:28:02</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class SecurityUtils {


    @Getter
    private static final String lineStart = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";

    @Getter
    private static final String lineEnd   = "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";

    @Getter
    private static final Security security = new Security(0);
}
