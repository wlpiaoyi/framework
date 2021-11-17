package org.wlpiaoyi.framework.utils.aes;

import lombok.Getter;
import lombok.Setter;
import org.wlpiaoyi.framework.utils.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
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
//    @Getter @Setter
//    static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    @Getter @Setter
    static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
    /*
     * AES/CBC/NoPadding 要求
     * 密钥必须是16字节长度的；Initialization vector (IV) 必须是16字节
     * 待加密内容的字节长度必须是16的倍数，如果不是16的倍数，就会出如下异常：
     * javax.crypto.IllegalBlockSizeException: Input length not multiple of 16 bytes
     *
     *  由于固定了位数，所以对于被加密数据有中文的, 加、解密不完整
     *
     *  可 以看到，在原始数据长度为16的整数n倍时，假如原始数据长度等于16*n，则使用NoPadding时加密后数据长度等于16*n，
     *  其它情况下加密数据长 度等于16*(n+1)。在不足16的整数倍的情况下，假如原始数据长度等于16*n+m[其中m小于16]，
     *  除了NoPadding填充之外的任何方 式，加密数据长度都等于16*(n+1).
     */
//    @Getter @Setter
//    static final String CIPHER_ALGORITHM = "AES/CBC/NoPadding";

    @Getter
    private String signatureAlgorithm;//签名算法

    @Getter
    private String keyAlgorithm;//秘钥算法

    @Getter
    private String IV;//秘钥算法

    @Getter
    private Key key;//秘钥

    private Cipher dCipher;
    private Cipher eCipher;

    public Aes setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return  this;
    }

    public Aes setIV(String IV){
        this.IV = IV;
        return this;
    }

    public Aes setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
        return  this;
    }

    public static Aes create(){
        Aes aes = new Aes();
        return aes;
    }

    public Aes load() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        dCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        eCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        if(this.getIV() == null){
            eCipher.init(Cipher.ENCRYPT_MODE, this.key);
            dCipher.init(Cipher.DECRYPT_MODE, this.key);
        }else{
            dCipher.init(Cipher.DECRYPT_MODE, this.key, new IvParameterSpec(this.getIV().getBytes("UTF-8")));
            eCipher.init(Cipher.ENCRYPT_MODE, this.key, new IvParameterSpec(this.getIV().getBytes("UTF-8")));
        }
        return this;
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
    public Aes setKey(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        SecureRandom random = SecureRandom.getInstance(this.signatureAlgorithm);
//        random.setSeed(key.getBytes("UTF-8"));
//        KeyGenerator kgen = KeyGenerator.getInstance(this.keyAlgorithm);
//        kgen.init(256, random);
//        this.key = kgen.generateKey();
        this.key =  new SecretKeySpec(key.getBytes("UTF-8"), this.keyAlgorithm);
        return this;
    }

    /**
     * @description: AES对称加密字符串，并通过Jdk自带Base64转换为ASCII
     * @date: 2017年11月7日 上午9:37:48
     * @param data
     * @return
     */
    public byte[] encrypt(byte[] data) {
        try {
            byte[] doFinal = eCipher.doFinal(data);
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
    public byte[] decrypt(byte[] data) {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            byte[] doFinal = dCipher.doFinal(bytes);
            return doFinal;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
