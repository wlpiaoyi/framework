package org.wlpiaoyi.framework.utils.aes;

import lombok.Getter;
import lombok.Setter;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Aes {
    /*
     * 秘钥算法
     * */
    //============================================>
    public static final String KEY_ALGORTHM_AES="AES";
    //<============================================

    /*
     * 签名算法
     * 512~65536位（密钥长度必须是64的倍数）
     * */
    //============================================>
    public static final String SIGNATURE_ALGORITHM_SHA1PRNG = "SHA1PRNG";
    //<============================================

    //加密-解密算法 / 工作模式 / 填充方式
    @Getter @Setter
    private static String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    @Getter
    private String signatureAlgorithm;//签名算法

    @Getter
    private String keyAlgorithm;//秘钥算法

    @Getter
    private Key key;//秘钥

    public Aes setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return  this;
    }

    public Aes setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
        return  this;
    }

    public static Aes create(){
        Aes aes = new Aes();
        return aes;
    }

    private Aes(){
        this.setKeyAlgorithm(Aes.KEY_ALGORTHM_AES)
                .setSignatureAlgorithm(Aes.SIGNATURE_ALGORITHM_SHA1PRNG);
    }

    /**
     * 设置秘钥
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     */
    public Aes setKey(String key) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance(this.signatureAlgorithm);
        random.setSeed(key.getBytes());
        KeyGenerator kgen = KeyGenerator.getInstance(this.keyAlgorithm);
        kgen.init(128, random);
        this.key = kgen.generateKey();
        return this;
    }

    /**
     * @description: AES对称加密字符串，并通过Jdk自带Base64转换为ASCII
     * @date: 2017年11月7日 上午9:37:48
     * @param data
     * @return
     */
    public byte[] getEncrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, this.key);
            byte[] doFinal = cipher.doFinal(data);
            return Base64.getEncoder().encode(doFinal);
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
    public byte[] getDecrypt(byte[] data) {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, this.key);
            byte[] doFinal = cipher.doFinal(bytes);
            return doFinal;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
