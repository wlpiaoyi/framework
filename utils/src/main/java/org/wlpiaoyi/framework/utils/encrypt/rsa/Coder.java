package org.wlpiaoyi.framework.utils.encrypt.rsa;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.wlpiaoyi.framework.utils.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Coder {

    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";

//    /**
//     * BASE64解密
//     * @param key
//     * @return
//     * @throws Exception
//     */
//    public static byte[] decryptBASE64(String key) throws Exception{
//        return (new BASE64Decoder()).decodeBuffer(key);
//    }
//    /**
//     * BASE64加密
//     * @param key
//     * @return
//     * @throws Exception
//     */
//    public static String encryptBASE64(byte[] key)throws Exception{
//        return (new BASE64Encoder()).encodeBuffer(key);
//    }

    /**
     * MD5加密
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encryptMD5(byte[] data)throws Exception{
        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(data);
        return md5.digest();
    }

    /**
     * MD5加密
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static byte[] encryptMD5(InputStream inputStream) throws Exception{
        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        int nRead;
        byte[] data = new byte[8];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            md5.update(data, 0, nRead);
        }
        return md5.digest();
    }

    /**
     * SHA加密
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encryptSHA(byte[] data)throws Exception{
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        sha.update(data);
        return sha.digest();
    }


    /**
     * SHA加密
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static byte[] encryptSHA(InputStream inputStream)throws Exception{
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        int nRead;
        byte[] data = new byte[8];
        StringBuilder sb = new StringBuilder();
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            sha.update(data, 0, nRead);
        }
        return sha.digest();
    }

}
