package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
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
     * 从String写入文件g
     * @param source
     * @param PATH
     * @return
     */
    public static boolean readFile(String source, String PATH){
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
}
