package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.UUID;

public class StringUtils {

    static {

    }

    /**
     * 将bytes编码成base64
     * @param bytes
     * @return
     */
    public static String base64Encode(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }

        /**
         * 将字符编码成base64
         * @param encodeStr
         * @param charseName
         * @return
         */
    public static String base64Encode(String encodeStr, String charseName){
        try {
            return new BASE64Encoder().encode(encodeStr.getBytes(charseName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符编码成base64
     * @param encodeStr
     * @return
     */
    public static String base64Encode(String encodeStr){
        return StringUtils.base64Encode(encodeStr, "UTF-8");
    }

    /**
     * 将base64字符解码
     * @param decodeStr
     * @param charseName
     * @return
     */
    public static String base64Decode(String decodeStr, String charseName){
        try {
            byte[] bytes = StringUtils.base64DecodeToBytes(decodeStr);
            return new String(bytes, charseName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将base64字符解码
     * @param decodeStr
     * @return
     */
    public static byte[] base64DecodeToBytes(String decodeStr){

        try {
            return new BASE64Decoder().decodeBuffer(decodeStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    /**
     * 将base64字符解码
     * @param decodeStr
     * @return
     */
    public static String base64Decode(String decodeStr){
        return StringUtils.base64Decode(decodeStr, "UTF-8");
    }

    /**
     * 已废弃请使用org.wlpiaoyi.framework.utils.ValueUtils.isBlank
     * @param arg
     * @return
     */
    @Deprecated
    public static boolean isBlank(String arg){
        return arg == null || arg.length() == 0;
    }
    @Deprecated
    public static String toJson(Object obj){
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(obj);
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

    /**
     * 转换成驼峰
     * @param name
     * @return
     */
    public static String parseUnderlineToHump(String name){
        String[] args = name.split("_");
        StringBuffer sb = new StringBuffer();
        for (String arg : args){
            if(ValueUtils.isBlank(arg)) continue;
            sb.append(arg.substring(0, 1).toUpperCase());
            if(arg.length() == 1) continue;
            sb.append(arg.substring(1).toLowerCase());
        }
        if(sb.length() == 0) return name;
        return sb.toString();
    }
}
