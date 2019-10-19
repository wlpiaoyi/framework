package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.websocket.WsUtile;

import java.security.MessageDigest;
import java.util.UUID;

public class StringUtils {
    public static final int UUID64_LENGHT = 64;

    public static boolean isBlank(String arg){
        return arg == null || arg.length() == 0;
    }

    public static String toJson(Object obj){
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(obj);
    }

    public static String MD5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        byte[] b = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            hex = (hex.length() == 1 ? "0" : "") + hex;
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 获得指定数目的UUID
     * @param number int 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static String[] getUUID32S(int number){
        if(number < 1){
            return null;
        }
        StringBuilder uuid = new StringBuilder();
        String[] retArray = new String[number];
        for(int i=0;i<number;i++){
            retArray[i] = StringUtils.getUUID32();
        }
        return retArray;
    }

    /**
     * 获得指定数目的UUID
     * @param number int 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static String[] getUUID64S(int number){
        if(number < 1){
            return null;
        }
        String[] retArray = new String[number];
        for(int i=0;i<number;i++){
            retArray[i] = StringUtils.getUUID64();
        }
        return retArray;
    }


    /**
     * 获得一个UUID
     * @return String UUID
     */
    public static String getUUID64(){
        String uuid32_1 = StringUtils.getUUID32();
        String uuid32_2 = StringUtils.getUUID32();
        return uuid32_1 + uuid32_2;
    }

    /**
     * 获得一个UUID
     * @return String UUID
     */
    public static String getUUID32(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid;
    }
}
