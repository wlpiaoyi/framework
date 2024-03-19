package org.wlpiaoyi.framework.proxy.rule;

import lombok.Getter;

import static org.wlpiaoyi.framework.proxy.rule.SocketRule.SOCKET4_VERSION;
import static org.wlpiaoyi.framework.proxy.rule.SocketRule.SOCKET5_VERSION;

/**
 * <p><b>{@code @description:}</b>  </p>
 * <p><b>{@code @date:}</b>         2024-03-19 17:27:55</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class SocketRuleUtils {

    /**
     * <p><b>{@code @description:}</b>  Socket代理类型</p>
     * <p><b>{@code @date:}</b>         2024/3/19 17:43</p>
     * <p><b>{@code @author:}</b>       wlpiaoyi</p>
     * <p><b>{@code @version:}</b>      1.0</p>
     */
    @Getter
    public enum ProxyType {

        Unknown((byte) 0),
        Anonymity((byte) 1),
        Encryption((byte) 2),
        Custom((byte) 3);

        private final byte value;

        ProxyType(byte value){
            this.value = value;
        }

        public ProxyType getEnum(byte value){
            for (ProxyType enumObj : ProxyType.values()){
                if(enumObj.getValue() == value){
                    return enumObj;
                }
            }
            return Unknown;
        }
    }

    /**
     * <p><b>{@code @description:}</b>  地址类型</p>
     * <p><b>{@code @date:}</b>         2024/3/19 17:55</p>
     * <p><b>{@code @author:}</b>       wlpiaoyi</p>
     * <p><b>{@code @version:}</b>      1.0</p>
     */
    @Getter
    public enum AddressType{

        Unknown((byte) 0),
        HOST((byte) 1),
        IP((byte) 2);


        private final byte value;

        AddressType(byte value){
            this.value = value;
        }

        public AddressType getEnum(byte value){
            for (AddressType enumObj : AddressType.values()){
                if(enumObj.getValue() == value){
                    return enumObj;
                }
            }
            return Unknown;
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * byte数据是否相同
     * </p>
     *
     * <p><b>@param</b> <b>bytes0</b>
     * {@link byte}
     * </p>
     *
     * <p><b>@param</b> <b>bytes1</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 17:33</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static boolean isEqualBytes(byte[] bytes0, byte[] bytes1){
        int len = Math.min(bytes0.length, bytes1.length);
        for (int i = 0; i < len; i++) {
            if(bytes0[i] != bytes1[i]) return false;
        }
        return true;
    }

    /**
     * <p><b>{@code @description:}</b>
     * Socket代理版本号
     * </p>
     *
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 17:38</p>
     * <p><b>{@code @return:}</b>{@link byte}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static byte socketProxyVersion(byte[] buffer){
        return buffer[0];
    }

    public static SocketRule getSocketRule(int version){
        switch (version){
            case SOCKET4_VERSION:{
                return null;
            }
            case SOCKET5_VERSION:{
                return new Socket5Rule();
            }
        }
        return null;
    }

}
