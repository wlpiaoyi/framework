package org.wlpiaoyi.framework.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * @description: AES加密工具类
 * @author wlpiaoyi
 * @date: 2019年10月06日
 */
public class AESUtils {

//    //实例化密钥
//    private static Key SECRET_KEY;

    //运算法则
    @Getter
    private static final String ALGORITHM = "SHA1PRNG";

//    //原始密钥
//    @Getter @Setter
//    private static String KEY_STR = "ikamboile2416-pdd";

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
    public static Key initializeSecretKey(String key) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(ALGORITHM);
        random.setSeed(key.getBytes());
        KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);
        kgen.init(128, random);
        return  kgen.generateKey();
    }

    /**
     * @description: AES对称加密字符串，并通过Jdk自带Base64转换为ASCII
     * @date: 2017年11月7日 上午9:37:48
     * @param data
     * @return
     */
    public static byte[] getEncrypt(byte[] data, Key SECRET_KEY) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            byte[] doFinal = cipher.doFinal(data);
            return Base64.getEncoder().encode(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @description: AES对称加密字符串，并通过Jdk自带Base64转换为ASCII
     * @date: 2017年11月7日 上午9:37:48
     * @param data
     * @return
     */
    public static String getEncrypt(String data, Key SECRET_KEY) {
        try {
            byte[] bytes = data.getBytes(CHARSETNAME);
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
     * @date: 2017年11月7日 上午10:14:00
     * @param data
     * @return
     */
    public static byte[] getDecrypt(byte[] data, Key SECRET_KEY) {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            byte[] doFinal = cipher.doFinal(bytes);
            return doFinal;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @description: 对AES加密字符串进行解密
     * @date: 2017年11月7日 上午10:14:00
     * @param data
     * @return
     */
    public static String getDecrypt(String data, Key SECRET_KEY) {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            byte[] doFinal = cipher.doFinal(bytes);
            return new String(doFinal, CHARSETNAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES加密字符串
     *
     * @param content
     *            需要被加密的字符串
     * @param password
     *            加密需要的密码
     * @return 密文
     */
    public static String encrypt(String content, String password) {
        try {
            byte[] byteContent = content.getBytes("utf-8");
            byte[] bytes = AESUtils.encrypt(byteContent, password);
            return new String(bytes, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param content
     *            AES加密过过的内容
     * @param password
     *            加密时的密码
     * @return 明文
     */
    public static String decrypt(String content, String password){
        try {
            byte[] byteContent = content.getBytes("utf-8");
            byte[] bytes = AESUtils.decrypt(byteContent, password);
            return new String(bytes, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    /**
     * AES加密字符串
     *
     * @param content
     *            需要被加密的字符串
     * @param password
     *            加密需要的密码
     * @return 密文
     */
    public static byte[] encrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
            //加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
//            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(content);// 加密
            return result;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param content
     *            AES加密过过的内容
     * @param password
     *            加密时的密码
     * @return 明文
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
            byte[] result = cipher.doFinal(content);
            return result; // 明文
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

