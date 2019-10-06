package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.MessageDigest;

public class StringUtils {

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
}
