package org.wlpiaoyi.framework.utils.http;

import java.util.Map;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2024-09-23 11:39:05</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public interface HttpMessage<T> {

    Map<String, String> getHeaders();

    T getBody();

}
