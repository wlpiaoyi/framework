package org.wlpiaoyi.framework.utils.security.utils;

import org.wlpiaoyi.framework.utils.StringUtils;

import javax.crypto.NoSuchPaddingException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/21 10:59
 * {@code @version:}:       1.0
 */
public class SecurityUtils {

    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";

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
