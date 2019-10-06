package org.wlpiaoyi.framework.utils;

import lombok.Getter;
import lombok.Setter;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * @description: AES加密工具类
 * @author wlpiaoyi
 * @date: 2019年10月06日
 */
public class AESUtils {

    //实例化密钥
    private static Key SECRET_KEY;

    //运算法则
    @Getter
    private static final String ALGORITHM = "SHA1PRNG";

    //原始密钥
    @Getter @Setter
    private static String KEY_STR = "ikamboile2416-pdd";

    //编码
    @Getter @Setter
    private static String CHARSETNAME = "UTF-8";

    //密钥算法
    @Getter @Setter
    private static String KEY_ALGORITHM = "AES";

    //加密-解密算法 / 工作模式 / 填充方式
    @Getter @Setter
    private static String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 初始化key
     */
    public static void initialize() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(ALGORITHM);
        random.setSeed(KEY_STR.getBytes());
        KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);
        kgen.init(128, random);
        SECRET_KEY = kgen.generateKey();
    }

    /**
     * @description: AES对称加密字符串，并通过Jdk自带Base64转换为ASCII
     * @author: Administrator
     * @date: 2017年11月7日 上午9:37:48
     * @param str
     * @return
     */
    public static String getEncryptString(String str) {
        try {
            byte[] bytes = str.getBytes(CHARSETNAME);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            byte[] doFinal = cipher.doFinal(bytes);
            return Base64.getEncoder().encodeToString(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @description: 对AES加密字符串进行解密
     * @author: maojialong
     * @date: 2017年11月7日 上午10:14:00
     * @param str
     * @return
     */
    public static String getDecryptString(String str) {
        try {
            byte[] bytes = Base64.getDecoder().decode(str);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            byte[] doFinal = cipher.doFinal(bytes);
            return new String(doFinal, CHARSETNAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

