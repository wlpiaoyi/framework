package org.wlpiaoyi.framework.utils.security;

import lombok.Getter;
import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.security.condition.ConditionAes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES是高级加密标准，在密码学中又称Rijndael加密法，是美国联邦政府采用的一种区块加密标准。
 * 这个标准用来替代原先的DES，目前已经被全世界广泛使用，同时AES已经成为对称密钥加密中最流行的算法之一。
 * AES支持三种长度的密钥：128位，192位，256位。
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    对称加密
 * {@code @date:}           2023/12/21 12:20
 * {@code @version:}:       1.0
 */
public class AesCipher extends Security{

//    /** 秘钥算法 **/
//    @Getter
//    protected final String keyAlgorithm;
//
//    @Getter
//    /** 签名算法 **/
//    protected final String signatureAlgorithm;

    @Getter
    private String IV;

    /** 秘钥 **/
    @Getter
    private Key key;

    private Cipher dCipher;
    private Cipher eCipher;


    public static AesCipher build(){
        return new AesCipher();
    }
//    public static AesCipher build(){
//        return new AesCipher(ConditionAes.SIGNATURE_ALGORITHM_SHA1PRNG);
//    }
//    public static AesCipher build(String signatureAlgorithm){
//        return new AesCipher(signatureAlgorithm);
//    }
    private AesCipher() {}
//    private AesCipher(String signatureAlgorithm) {
//        this.keyAlgorithm = ConditionAes.KEY_ALGORITHM;
//        this.signatureAlgorithm = signatureAlgorithm;
//    }

    @SneakyThrows
    @Override
    public AesCipher loadConfig() {
        this.dCipher = Cipher.getInstance(ConditionAes.CIPHER_ALGORITHM_AC5_7);
        this.eCipher = Cipher.getInstance(ConditionAes.CIPHER_ALGORITHM_AC5_7);
        if(ValueUtils.isBlank(this.getIV())){
            this.eCipher.init(Cipher.ENCRYPT_MODE, this.key);
            this.dCipher.init(Cipher.DECRYPT_MODE, this.key);
        }else{
            this.dCipher.init(Cipher.DECRYPT_MODE, this.key,
                    new IvParameterSpec(this.getIV().getBytes(StandardCharsets.UTF_8)));
            this.eCipher.init(Cipher.ENCRYPT_MODE, this.key,
                    new IvParameterSpec(this.getIV().getBytes(StandardCharsets.UTF_8)));
        }
        return this;
    }



    /**
     * @description: AES对称加密字符串，并通过Jdk自带Base64转换为ASCII
     * @date: 2017年11月7日 上午9:37:48
     * @param dataBytes
     * @return
     */
    public synchronized byte[] encrypt(byte[] dataBytes)
            throws IllegalBlockSizeException,
            BadPaddingException {
        return eCipher.doFinal(dataBytes);
    }

    /**
     * @description: AES对称加密字符串，并通过Jdk自带Base64转换为ASCII
     * @date: 2017年11月7日 上午9:37:48
     * @param dataBytes
     * @param off
     * @param len
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public synchronized byte[] encrypt(byte[] dataBytes, int off, int len)
            throws IllegalBlockSizeException,
            BadPaddingException {
        return eCipher.doFinal(dataBytes, off, len);
    }

    private static final int ME_DATA_SIZE = 512;
    /**
     * @description: AES对称加密字符串，并通过Jdk自带Base64转换为ASCII
     * @date: 2017年11月7日 上午9:37:48
     * @param dataIn
     * @param dataOut
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public synchronized void encryptSection(InputStream dataIn, OutputStream dataOut)
            throws IOException,
            IllegalBlockSizeException,
            BadPaddingException {
        int nRead;
        byte[] data = new byte[ME_DATA_SIZE];
        while ((nRead = dataIn.read(data, 0, data.length)) != -1) {
            byte[] outBytes = eCipher.doFinal(data, 0, nRead);
            int dataL = outBytes.length;
            byte[] lbs = ValueUtils.toBytes(dataL, 8);
            dataOut.write(lbs);
            dataOut.write(outBytes);
            dataOut.flush();
        }
    }

    /**
     * @description: 对AES加密字符串进行解密
     * @date: 2017年11月7日 上午10:14:00
     * @param dataBytes
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public synchronized byte[] decrypt(byte[] dataBytes) throws IllegalBlockSizeException, BadPaddingException {
        return dCipher.doFinal(dataBytes);
    }

    /**
     * @description: 对AES加密字符串进行解密
     * @date: 2017年11月7日 上午10:14:00
     * @param dataBytes
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public synchronized byte[] decrypt(byte[] dataBytes, int off, int len) throws IllegalBlockSizeException, BadPaddingException {
        return dCipher.doFinal(dataBytes, off, len);
    }

    /**
     * @description: 对AES加密字符串进行解密
     * @date: 2017年11月7日 上午10:14:00
     * @param dataIn
     * @param dataOut
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public synchronized void decryptSection(InputStream dataIn, OutputStream dataOut) throws IOException, IllegalBlockSizeException, BadPaddingException {

        byte[] lbs = new byte[8];
        while (dataIn.read(lbs, 0, lbs.length) != -1) {
            final int dataL = (int) ValueUtils.toLong(lbs);
            byte[] data = new byte[dataL];
            final int dataI = dataIn.read(data, 0, dataL);
            if (dataI == -1){
                break;
            }
            byte[] outData = dCipher.doFinal(data, 0, dataI);
            dataOut.write(outData);
            dataOut.flush();
            lbs = new byte[8];
        }
    }


    /**
     * 设置秘钥
     * 128位，192位，256位
     * @param key
     * @return
     */
    public AesCipher setKey(String key){
        this.key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ConditionAes.KEY_ALGORITHM);
        return this;
    }
    /**
     * 设置秘钥
     * 128位，192位，256位
     * @param key
     * @param size 默认256
     * @return
     * @throws NoSuchAlgorithmException
     */
    public AesCipher setKey(String key, int size) throws NoSuchAlgorithmException {
        if(size <= 0){
            size = 256;
        }
        SecureRandom random = SecureRandom.getInstance(ConditionAes.SIGNATURE_ALGORITHM_SHA1PRNG);
        random.setSeed(key.getBytes(StandardCharsets.UTF_8));
        KeyGenerator kgen = KeyGenerator.getInstance(ConditionAes.KEY_ALGORITHM);
        kgen.init(size, random);
        this.key = kgen.generateKey();
        return this;
    }
    public AesCipher setIV(String IV){
        this.IV = IV;
        return this;
    }

}
