package org.wlpiaoyi.framework.proxy.socket;

public class SocketProxyTools {

    public enum SocketProxyType{
        Unkown,
        Anonymity,
        Encryption,
        ALL
    }

    public static final byte[] CONNECT_OK = {0x5, 0x0, 0x0, 0x1, 0, 0, 0, 0, 0, 0};

    //要求匿名代理
    public static final byte[] HANDLE_REQUEEST1 = {0x5, 0x1, 0x0};
    //以用户名密码方式验证代理
    public static final byte[] HANDLE_REQUEEST2 = {0x5, 0x1, 0x2};
    //要求以匿名或者用户名密码方式代理
    public static final byte[] HANDLE_REQUEEST3 = {0x5, 0x2, 0x0, 0x2};

    //代理允许匿名
    public static final byte[] HANDLE_RESPONSE1 = {0x5, 0x0};
    //要求验证
    public static final byte[] HANDLE_RESPONSE2 = {0x5, 0x2};

    //TCP单独部分
    public static final byte[] DATA_REQUEEST_TCP = {0x5, 0x1};


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
        if(SocketProxyTools.REQUEST_DATA_IS_HOST(buffer)){
            //说明是网址地址
            int size = SocketProxyTools.REQUEST_DATA_HOST_LENGTH(buffer); //网址长度
            for(int i = 5; i < (5 + size); i++){
                domain.append((char)buffer[i]);
            }

        }else if(SocketProxyTools.REQUEST_DATA_IS_IP(buffer)){
            if(proxyType == SocketProxyTools.SocketProxyType.Anonymity){
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
