package org.wlpiaoyi.framework.utils.xml;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * XML解析异常
 * </p>
 * <p><b>{@code @date:}</b>2026-02-04 11:30:45</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class XmlParseException extends Exception {

    public XmlParseException(String message) {
        super(message);
    }

    public XmlParseException(String message, Throwable cause) {
        super(message, cause);
    }
}