package org.wlpiaoyi.framework.proxy.rule;

import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class Socket4Rule implements SocketRule{
    //匿名代理
    public static final byte[] REQUEST_ANONYMITY = {SOCKET4_VERSION, 0x1};


    //允许转发
    public static final byte[] RESPONSE_OK = {0x5A};
    //拒绝转发，一般性失败
    public static final byte[] RESPONSE_REFUSE_GE= {0x5B};
    //拒绝转发，SOCKS 4 Server无法连接到SOCS 4 Client所在主机的  IDENT服务
    public static final byte[] RESPONSE_REFUSE_UC= {0x5C};
    //拒绝转发，请求报文中的USERID与IDENT服务返回值不相符
    public static final byte[] RESPONSE_REFUSE_UE= {0x5D};

    public static final byte[] CONNECT_OK = {SOCKET5_VERSION, 0x0, 0x0, 0x1, 0, 0, 0, 0, 0, 0};

    @Override
    public int getVersion() {
        return SOCKET4_VERSION;
    }

    @Override
    public SocketRuleUtils.ProxyType getProxyType(byte[] buffer) {
        if(SocketRuleUtils.isEqualBytes(REQUEST_ANONYMITY, buffer)){
            return SocketRuleUtils.ProxyType.Anonymity;
        }else{
            return SocketRuleUtils.ProxyType.Unknown;
        }
    }

    @Override
    public SocketRuleUtils.AddressType getAddressType(byte[] buffer) {
        return SocketRuleUtils.AddressType.IP;
    }

    @Override
    public void proxyTypeHandle(SocketRuleUtils.ProxyType proxyType, OutputStream osIn, byte[][] encryptionDatas) throws IOException {
        byte[] vBytes = new byte[1];
        vBytes[0] = 0x0;
        osIn.write(vBytes);
        if(proxyType == SocketRuleUtils.ProxyType.Unknown){
            osIn.write(RESPONSE_REFUSE_GE);
            return;
        }
        osIn.write(RESPONSE_OK);
    }

    @Override
    public boolean doVerifyEncryption(byte[] buffer, byte[][] encryptionDatas, InputStream isIn, OutputStream osIn) throws IOException {
        return true;
    }

    @Override
    public void doAddress(byte[] buffer, SocketRuleUtils.ProxyType proxyType, InputStream isIn, OutputStream osIn, Map<Object, Object> userMap) throws IOException {
        int responsePort = getPort(buffer, 8);
        String responseDomain = getDomain(buffer, 8, proxyType);
        userMap.put("responsePort", responsePort);
        if(ValueUtils.isNotBlank(responseDomain)){
            userMap.put("responseDomain", responseDomain);
        }
        byte[] ipBytes = new byte[4];
        for(int i = 4; i < 8; i++){
            ipBytes[i - 4] = buffer[i];
        }
        userMap.put("ipBytes", ipBytes);

        byte[] portBytes = new byte[2];
        portBytes[0] = buffer[2];
        portBytes[1] = buffer[3];
        userMap.put("portBytes", portBytes);
    }

    @Override
    public void doConnectData(OutputStream osIn, Map<Object, Object> userMap) throws IOException {
        byte[] ipBytes = MapUtils.get(userMap, "ipBytes");
        byte[] portBytes = MapUtils.get(userMap, "portBytes");
        //response handle
        osIn.write(portBytes);
        osIn.write(ipBytes);
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
        //说明是ip地址
        for(int i = 4; i < 8; i++){
            int A = buffer[i];
            if(A < 0) A = 256 + A;
            domain.append(".");
            domain.append(A);
        }
        return domain.toString().substring(1);
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
        int thod = buffer[2];
        int port = buffer[3];
        if(thod < 0){
            thod = (256 + thod);
        }
        if(port < 0){
            port = (256 + port);
        }
        return 256 * thod + port;
    }
    byte getHostLength(byte[] buffer){
        return buffer[4];
    }
}
