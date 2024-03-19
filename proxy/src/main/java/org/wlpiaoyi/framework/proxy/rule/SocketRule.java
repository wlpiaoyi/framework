package org.wlpiaoyi.framework.proxy.rule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * <p><b>{@code @description:}</b>  </p>
 * <p><b>{@code @date:}</b>         2024-03-19 17:28:16</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public interface SocketRule {

    byte SOCKET4_VERSION = 0x4;
    byte SOCKET5_VERSION = 0x5;
    int BUFFER_LEN = 1024;

    /**
     * <p><b>{@code @description:}</b>
     * Socket代理版本
     * </p>
     *
     * <p><b>@param</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 17:47</p>
     * <p><b>{@code @return:}</b>{@link int}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    int getVersion();


    /**
     * <p><b>{@code @description:}</b>
     * Socket代理类型
     * </p>
     *
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 17:47</p>
     * <p><b>{@code @return:}</b>{@link SocketRuleUtils.ProxyType}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
     SocketRuleUtils.ProxyType getProxyType(byte[] buffer);

    /**
     * <p><b>{@code @description:}</b> 
     * 地址类型
     * </p>
     * 
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:00</p>
     * <p><b>{@code @return:}</b>{@link SocketRuleUtils.AddressType}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
     SocketRuleUtils.AddressType getAddressType(byte[] buffer);

    /**
     * <p><b>{@code @description:}</b>
     * ProxyType 处理
     * </p>
     *
     * <p><b>@param</b> <b>proxyType</b>
     * {@link SocketRuleUtils.ProxyType}
     * </p>
     *
     * <p><b>@param</b> <b>osIn</b>
     * {@link OutputStream}
     * </p>
     *
     * <p><b>@param</b> <b>encryptionDatas</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:30</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void proxyTypeHandle(SocketRuleUtils.ProxyType proxyType, OutputStream osIn, byte[][] encryptionDatas) throws IOException;

    /**
     * <p><b>{@code @description:}</b>
     * 认证处理
     * </p>
     *
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>@param</b> <b>encryptionDatas</b>
     * {@link byte}
     * </p>
     *
     * <p><b>@param</b> <b>isIn</b>
     * {@link InputStream}
     * </p>
     *
     * <p><b>@param</b> <b>osIn</b>
     * {@link OutputStream}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:35</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    boolean doVerifyEncryption(byte[] buffer,  byte[][] encryptionDatas, InputStream isIn, OutputStream osIn) throws IOException;


    /**
     * <p><b>{@code @description:}</b>
     * 地址处理
     * </p>
     *
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>@param</b> <b>proxyType</b>
     * {@link SocketRuleUtils.ProxyType}
     * </p>
     *
     * <p><b>@param</b> <b>isIn</b>
     * {@link InputStream}
     * </p>
     *
     * <p><b>@param</b> <b>osIn</b>
     * {@link OutputStream}
     * </p>
     *
     * <p><b>@param</b> <b>userMap</b>
     * {@link Map <Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:46</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void doAddress(byte[] buffer, SocketRuleUtils.ProxyType proxyType, InputStream isIn, OutputStream osIn, Map<Object, Object> userMap) throws IOException;


    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>osIn</b>
     * {@link OutputStream}
     * </p>
     *
     * <p><b>@param</b> <b>userMap</b>
     * {@link Map<Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:53</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    void doConnectData(OutputStream osIn, Map<Object, Object> userMap) throws IOException;
}
