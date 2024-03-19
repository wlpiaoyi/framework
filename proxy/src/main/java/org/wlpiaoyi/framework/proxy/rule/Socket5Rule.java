package org.wlpiaoyi.framework.proxy.rule;

import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * <p><b>{@code @description:}</b>  </p>
 * <p><b>{@code @date:}</b>         2024-03-19 17:27:27</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class Socket5Rule implements SocketRule {

    public static final byte ENCRYPTION_VERSION = 0x1;

    //匿名代理
    public static final byte[] REQUEST_ANONYMITY = {SOCKET5_VERSION, 0x1, 0x0};
    //以用户名密码方式验证代理
    public static final byte[] REQUEST_ENCRYPTION = {SOCKET5_VERSION, 0x1, 0x2};
    //匿名或者用户名密码方式代理
    public static final byte[] REQUEST_CUSTOM = {SOCKET5_VERSION, 0x2, 0x0, 0x2};

    //代理回应状态 无验证需求
    public static final byte[] RESPONSE_ANONYMITY = {SOCKET5_VERSION, 0x0};
    //代理回应状态 用户名/密码
    public static final byte[] RESPONSE_ENCRYPTION= {SOCKET5_VERSION, 0x2};
    //代理回应状态 无可接受方法
    public static final byte[] RESPONSE_UNKOWN= {0xF, 0xF};
    //代理回应状态
    public static final byte[] ENCRYPTION_OK = {0x1, 0x0};

    public static final byte[] CONNECT_OK = {SOCKET5_VERSION, 0x0, 0x0, 0x1, 0, 0, 0, 0, 0, 0};



    public static final byte ADDRESS_IP = 0x1;
    public static final byte ADDRESS_HOST = 0x3;

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
    @Override
    public int getVersion() {
        return SOCKET5_VERSION;
    }

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
    public SocketRuleUtils.ProxyType getProxyType(byte[] buffer){
        if(SocketRuleUtils.isEqualBytes(REQUEST_ANONYMITY, buffer)){
            return SocketRuleUtils.ProxyType.Anonymity;
        }else if(SocketRuleUtils.isEqualBytes(REQUEST_ENCRYPTION, buffer)){
            return SocketRuleUtils.ProxyType.Encryption;
        }else if(SocketRuleUtils.isEqualBytes(REQUEST_CUSTOM, buffer)){
            return SocketRuleUtils.ProxyType.Custom;
        }else{
            return SocketRuleUtils.ProxyType.Unknown;
        }
    }

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
    @Override
    public SocketRuleUtils.AddressType getAddressType(byte[] buffer) {
        if(buffer[3] == ADDRESS_IP){
            return SocketRuleUtils.AddressType.IP;
        }
        if(buffer[3] == ADDRESS_HOST){
            return SocketRuleUtils.AddressType.HOST;
        }
        return SocketRuleUtils.AddressType.Unknown;
    }


    /**
     * <p><b>{@code @description:}</b>
     * 获取认证用户名长度
     * </p>
     *
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:06</p>
     * <p><b>{@code @return:}</b>{@link byte}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public byte getEncryptionNameLength(byte[] buffer){
        if(buffer.length < 5) return -1;
        return buffer[1];
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取认证用户名
     * </p>
     *
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>@param</b> <b>len</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:07</p>
     * <p><b>{@code @return:}</b>{@link byte[]}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public byte[] getEncryptionName(byte[] buffer, int len){
        if(len < 1) return null;
        if(buffer.length < len + 4) return null;
        byte[] nameBuffer = new byte[len];
        for (int i = 0; i < len; i++) {
            nameBuffer[i] = buffer[i + 2];
        }
        return nameBuffer;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取认证密码长度
     * </p>
     *
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>@param</b> <b>nameLen</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:07</p>
     * <p><b>{@code @return:}</b>{@link byte}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public byte getEncryptionPwdLength(byte[] buffer, int nameLen){
        if(buffer.length < 4 + nameLen) return -1;
        return buffer[2 + nameLen];
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取认证密码
     * </p>
     *
     * <p><b>@param</b> <b>buffer</b>
     * {@link byte}
     * </p>
     *
     * <p><b>@param</b> <b>nameLen</b>
     * {@link int}
     * </p>
     *
     * <p><b>@param</b> <b>len</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:07</p>
     * <p><b>{@code @return:}</b>{@link byte[]}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public byte[] getEncryptionPwd(byte[] buffer, int nameLen, int len){
        if(len < 1) return null;
        if(buffer.length < len + nameLen + 3) return null;
        byte[] pwdBuffer = new byte[len];
        for (int i = 0; i < len; i++) {
            pwdBuffer[i] = buffer[i + nameLen + 3];
        }
        return pwdBuffer;
    }

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
    public void proxyTypeHandle(SocketRuleUtils.ProxyType proxyType, OutputStream osIn, byte[][] encryptionDatas) throws IOException {
        if(proxyType == SocketRuleUtils.ProxyType.Unknown){
            osIn.write(RESPONSE_UNKOWN);
            return;
        }
        //response handle
        if(encryptionDatas != null && encryptionDatas.length == 2){
            osIn.write(RESPONSE_ENCRYPTION);
        }else {
            osIn.write(RESPONSE_ANONYMITY);
        }
    }

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
    public boolean doVerifyEncryption(byte[] buffer,  byte[][] encryptionDatas, InputStream isIn, OutputStream osIn) throws IOException {
        if(encryptionDatas != null && encryptionDatas.length == 2){
            //read username and password
            isIn.read(buffer);
            //verify username and password
            byte nameL = getEncryptionNameLength(buffer);
            byte[] name = getEncryptionName(buffer, nameL);
            if(nameL < 1 || name == null || name.length != nameL) {
                return false;
            }

            byte pwdL = getEncryptionPwdLength(buffer, nameL);
            byte[] pwd = getEncryptionPwd(buffer, nameL, pwdL);
            if(pwdL < 1 || pwd == null || pwd.length != pwdL) {
                return false;
            }

            boolean flag = SocketRuleUtils.isEqualBytes(encryptionDatas[0], name) && SocketRuleUtils.isEqualBytes(encryptionDatas[1], pwd);
            if(flag){
                //verify passed
                osIn.write(ENCRYPTION_OK);
                osIn.flush();
            }else {
                //verify missed
                byte[] response = ENCRYPTION_OK;
                response[1] = 0x01;
                osIn.write(response);
                osIn.flush();
                return false;
            }
        }
        return true;
    }

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
     * {@link Map<Object>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/19 18:46</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public void doAddress(byte[] buffer, SocketRuleUtils.ProxyType proxyType, InputStream isIn, OutputStream osIn, Map<Object, Object> userMap) throws IOException {
        int len = isIn.read(buffer);
        String responseDomain = getDomain(buffer, len, proxyType);
        int responsePort = getPort(buffer, len);
        userMap.put("responsePort", responsePort);
        if(ValueUtils.isNotBlank(responseDomain)){
            userMap.put("responseDomain", responseDomain);
        }
    }


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
    public void doConnectData(OutputStream osIn, Map<Object, Object> userMap) throws IOException{
        osIn.write(CONNECT_OK);
        osIn.flush();
    }

    /**
     * 解析Domain
     * @param buffer
     * @param len
     * @return
     */
    String getDomain(byte[] buffer, int len, SocketRuleUtils.ProxyType proxyType){
        if(len<8){
            return null;
        }
        StringBuffer domain =new StringBuffer();
        switch (getAddressType(buffer)){
            case IP:{
                if(proxyType == SocketRuleUtils.ProxyType.Anonymity){
                    //说明是ip地址
                    for(int i = 7; i >= 4; i--){
                        int A = buffer[i];
                        if(A < 0) A = 256 + A;
                        domain.append(A);
                        domain.append(".");
                    }
                }else{
                    //说明是ip地址
                    for(int i = 4; i <= 7; i++){
                        int A = buffer[i];
                        if(A < 0) A = 256 + A;
                        domain.append(A);
                        domain.append(".");
                    }
                }
                domain.deleteCharAt(domain.length()-1);
            }
            break;
            case HOST:{
                //说明是网址地址
                int size = getHostLength(buffer); //网址长度
                for(int i = 5; i < (5 + size); i++){
                    domain.append((char)buffer[i]);
                }
            }
            break;
        }
        return domain.toString();
    }

    /**
     * 解析Port
     * @param buffer
     * @param len
     * @return
     */
    int getPort(byte[] buffer,int len){
        if(len<4){
            return 0;
        }
        int port = buffer[len-1];
        int thod = buffer[len-2];
        if(port > 0){
            return 256 * thod + port;
        }else{
            return 256 * thod + (256 + port);
        }
    }
    byte getHostLength(byte[] buffer){
        return buffer[4];
    }
}
