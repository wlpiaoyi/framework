package org.wlpiaoyi.framework.utils.token.model;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 认证信息
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/28 10:37</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
public interface AuthBody {

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * token数据
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/28 11:36</p>
     * <p><b>{@code @return:}</b>{@link byte[]}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    byte[] getTokenValue();
    AuthBody setTokenValue(byte[] tokenValue);

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * token过期时间(秒)
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/28 11:36</p>
     * <p><b>{@code @return:}</b>{@link long}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    long getExpireSeconds();
    AuthBody setExpireSeconds(long expireSeconds);

}
