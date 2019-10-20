package org.wlpiaoyi.framework.proxy.utils;

public class Utils {

    public enum SocketProxyType{
        Unkown,
        Anonymity,
        Encryption,
        Custom
    }
    public static final byte SOCKET_VERSION = 0x5;

    //匿名代理
    public static final byte[] REQUEST_ANONYMITY = {SOCKET_VERSION, 0x1, 0x0};
    //以用户名密码方式验证代理
    public static final byte[] REQUEST_ENCRYPTION = {SOCKET_VERSION, 0x1, 0x2};
    //匿名或者用户名密码方式代理
    public static final byte[] REQUEST_CUSTOM = {SOCKET_VERSION, 0x2, 0x0, 0x2};

    //代理允许匿名
    public static final byte[] RESPONSE_ANONYMITY = {SOCKET_VERSION, 0x0};
    //代理用户名密码方式验证
    public static final byte[] RESPONSE_ENCRYPTION = {SOCKET_VERSION, 0x2};


    public static final byte[] CONNECT_OK = {SOCKET_VERSION, 0x0, 0x0, 0x1, 0, 0, 0, 0, 0, 0};


    //TCP单独部分
//    public static final byte[] DATA_REQUEEST_TCP = {0x5, 0x1};


    public static boolean IS_EQUES_BYTES(byte[] bytes0, byte[] bytes1){
        int len = Math.min(bytes0.length, bytes1.length);
        boolean flag = true;
        for (int i = 0; i < len; i++) {
            flag = flag & (bytes0[i] == bytes1[i]);
            if(!flag) return false;
        }
        return true;
    }

    public static boolean REQUEST_DATA_IS_HOST(byte[] buffer){
        if(buffer[3] == 0x3) return true;
        return false;
    }

    public static byte REQUEST_DATA_HOST_LENGTH(byte[] buffer){
        return buffer[4];
    }

    public static boolean REQUEST_DATA_IS_IP(byte[] buffer){
        if(buffer[3] == 0x1) return true;
        return false;
    }

    public static String bytesToHexString(byte[] bArray, int begin, int end) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = begin; i < end; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            sb.append(" ");
        }
        return sb.toString();
    }

    public static final byte[] getConnectBytes(byte[] buffer ,int len , SocketProxyType proxyType){
        byte domainL = 0;
        byte[] domain = new byte[256];
        byte type = 0x01;
        if(Utils.REQUEST_DATA_IS_HOST(buffer)){
            type = 0x03;
            //说明是网址地址
            int size = Utils.REQUEST_DATA_HOST_LENGTH(buffer); //网址长度
            for(int i = 5; i < (5 + size); i++){
                domain[domainL] = buffer[i];
                domainL ++;
            }

        }else if(Utils.REQUEST_DATA_IS_IP(buffer)){
            type = 0x01;
            if(proxyType == Utils.SocketProxyType.Anonymity){
                //说明是ip地址
                for(int i = 7; i >= 4; i--){
                    domain[domainL] = buffer[i];
                    domainL++;
                }
            }else{
                //说明是ip地址
                for(int i = 4; i <= 7; i++){
                    domain[domainL] = buffer[i];
                    domainL++;
                }
            }
        }
        byte pmL = 2;
        byte[] pm = new byte[]{buffer[len-2], buffer[len-1]};

        byte[] results = new byte[4 + domainL + pmL];
        results[0] = 0x5;
        results[1] = 0x0;
        results[2] = 0x0;
        results[3] = type;
        for (int i = 0; i < domainL; i++) {
            results[i + 4] = domain[i];
        }
        for (int i = 0; i < 2; i++) {
            results[i + domainL + 4] = pm[i];
        }

        return results;
    }

    /**
     * 解析Domain
     * @param buffer
     * @param len
     * @return
     */
    public static final String getDomain(byte[] buffer, int len, SocketProxyType proxyType){
        if(len<8){
            return null;
        }
        StringBuffer domain =new StringBuffer();
        if(Utils.REQUEST_DATA_IS_HOST(buffer)){
            //说明是网址地址
            int size = Utils.REQUEST_DATA_HOST_LENGTH(buffer); //网址长度
            for(int i = 5; i < (5 + size); i++){
                domain.append((char)buffer[i]);
            }

        }else if(Utils.REQUEST_DATA_IS_IP(buffer)){
            if(proxyType == Utils.SocketProxyType.Anonymity){
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
        return domain.toString();
    }

    /**
     * 解析Port
     * @param buffer
     * @param len
     * @return
     */
    public static final int getPort(byte[] buffer,int len){
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
}
