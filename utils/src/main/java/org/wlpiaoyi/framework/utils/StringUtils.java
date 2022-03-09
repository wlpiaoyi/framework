package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
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

    public static String toJson(Object obj){
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(obj);
    }

    public static String MD5(String data) throws NoSuchAlgorithmException {
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
     * 对一个文件获取md5值
     * @return md5串
     */
    @SneakyThrows
    public static String MD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(md.digest()));
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
     * 从文件读取String
     * @param PATH
     * @return
     */
    public static String readFile(String PATH){
        BufferedReader br = null;
        try {
            StringBuffer sb = new StringBuffer();
            String str;
            br = new BufferedReader(new FileReader(PATH));
            while((str = br.readLine()) != null) {
                sb.append(str);
                sb.append("\r\n");
            }
            return sb.toString();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null) {
                try {br.close();} catch (IOException e) { e.printStackTrace();}
            }
        }
        return null;
    }

    /**
     * 从String写入文件
     * @param source
     * @param PATH
     * @return
     */
    public static boolean writeFile(String source, String PATH){
        File f = new File(PATH);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(source);
            bw.flush();
            bw.close();
            return true;
        }catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String toHump(String name){
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
