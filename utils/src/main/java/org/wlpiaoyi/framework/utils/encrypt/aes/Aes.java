package org.wlpiaoyi.framework.utils.encrypt.aes;

import lombok.Getter;
import lombok.Setter;
import org.wlpiaoyi.framework.utils.ValueUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	Aes基础加密工具
 * {@code @date:} 			2021-02-09 17:14:36
 * {@code @version:}: 		1.0
 */
@Deprecated
public class Aes {

    /** 秘钥算法 **/
    public static final String KEY_ALGORITHM_AES="AES";

    /**
     * 签名算法
     * 512~65536位（密钥长度必须是64的倍数）
     * */
    public static final String SIGNATURE_ALGORITHM_SHA1PRNG = "SHA1PRNG";

    /**
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
    @Getter @Setter
    static final String CIPHER_ALGORITHM_AEN = "AES/EBC/NoPadding";
    /** 加密-解密算法 / 工作模式 / 填充方式 **/
    @Getter @Setter
    static final String CIPHER_ALGORITHM_AE5_7 = "AES/ECB/PKCS5Padding";
    /** 加密-解密算法 / 工作模式 / 填充方式 **/
    @Getter @Setter
    static final String CIPHER_ALGORITHM_AC5_7 = "AES/CBC/PKCS5Padding";


    @Getter
    private Charset typeEncoding = StandardCharsets.UTF_8;

    /** 签名算法 **/
    @Getter
    private String signatureAlgorithm;

    /** 秘钥算法 **/
    @Getter
    private String keyAlgorithm;

    @Getter
    private String IV;

    /** 秘钥 **/
    @Getter
    private Key key;

    private Cipher dCipher;
    private Cipher eCipher;

    public Aes setTypeEncoding(Charset typeEncoding){
        this.typeEncoding = typeEncoding;
        return this;
    }

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
//    /**
//     * 解决java不支持AES/CBC/PKCS7Padding模式解密
//     */
//    static {
//        Security.addProvider(new BouncyCastleProvider());
//    }

    public Aes setDCipherAlgorithm(String dCipherAlgorithm) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.dCipher = Cipher.getInstance(dCipherAlgorithm);
        return this;
    }

    public Aes setECipherAlgorithm(String dCipherAlgorithm) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.eCipher = Cipher.getInstance(dCipherAlgorithm);
        return this;
    }

    public Aes load() throws Exception {
        if(this.dCipher == null){
            this.setDCipherAlgorithm(CIPHER_ALGORITHM_AC5_7);
        }
        if(this.eCipher == null){
            this.setECipherAlgorithm(CIPHER_ALGORITHM_AC5_7);
        }
        if(this.key == null){
            throw new Exception("the aes key can't be null");
        }
        if(this.getIV() == null){
            this.eCipher.init(Cipher.ENCRYPT_MODE, this.key);
            this.dCipher.init(Cipher.DECRYPT_MODE, this.key);
        }else{
            this.dCipher.init(Cipher.DECRYPT_MODE, this.key,
                    new IvParameterSpec(this.getIV().getBytes(this.typeEncoding)));
            this.eCipher.init(Cipher.ENCRYPT_MODE, this.key,
                    new IvParameterSpec(this.getIV().getBytes(this.typeEncoding)));
        }
        return this;
    }

    private Aes(){
        this.setKeyAlgorithm(Aes.KEY_ALGORITHM_AES)
                .setSignatureAlgorithm(Aes.SIGNATURE_ALGORITHM_SHA1PRNG);
    }

    /**
     * 设置秘钥
     * @param key
     * @return
     */
    public Aes setKey(String key) {
        this.key =  new SecretKeySpec(key.getBytes(this.typeEncoding), this.keyAlgorithm);
        return this;
    }

    /**
     * 设置秘钥
     * @param key
     * @param size 默认256
     * @return
     * @throws NoSuchAlgorithmException
     */
    public Aes setKey(String key, int size) throws NoSuchAlgorithmException {
        if(size <= 0){
            size = 256;
        }
        SecureRandom random = SecureRandom.getInstance(this.signatureAlgorithm);
        random.setSeed(key.getBytes(this.typeEncoding));
        KeyGenerator kgen = KeyGenerator.getInstance(this.keyAlgorithm);
        kgen.init(size, random);
        this.key = kgen.generateKey();
        return this;
    }

    /**
     * @description: AES对称加密字符串，并通过Jdk自带Base64转换为ASCII
     * @date: 2017年11月7日 上午9:37:48
     * @param dataBytes
     * @return
     */
    public byte[] encrypt(byte[] dataBytes)
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
    public byte[] encrypt(byte[] dataBytes, int off, int len)
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
    public void encryptSection(InputStream dataIn, OutputStream dataOut)
            throws IOException,
            IllegalBlockSizeException,
            BadPaddingException {
        int nRead;
        byte[] data = new byte[ME_DATA_SIZE];
        final int tempsL = 7;
        byte[] temps = new byte[tempsL];
        while ((nRead = dataIn.read(data, 0, data.length)) != -1) {
            byte[] outBytes = eCipher.doFinal(data, 0, nRead);
            int length = outBytes.length;
            byte[] lbs = ValueUtils.toBytes(length);
            final int lbsL = lbs.length;
            if(lbsL == 7){
                dataOut.write(lbs);
            }else{
                int offL = tempsL - lbsL;
                for (int i = tempsL - 1; i >= 0; i--){
                    if(i < offL){
                        temps[i] = -128;
                    }else{
                        temps[i] = lbs[i - offL];
                    }
                }
                dataOut.write(temps);
            }
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
    public byte[] decrypt(byte[] dataBytes) throws IllegalBlockSizeException, BadPaddingException {
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
    public byte[] decrypt(byte[] dataBytes, int off, int len) throws IllegalBlockSizeException, BadPaddingException {
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
    public void decryptSection(InputStream dataIn, OutputStream dataOut) throws IOException, IllegalBlockSizeException, BadPaddingException {

        byte[] temps = new byte[7];
        while (dataIn.read(temps, 0, temps.length) != -1) {
            int lbsL = 0;
            for (byte bt : temps){
                if(bt > -128){
                    lbsL ++;
                }
            }
            byte[] lbs = new byte[lbsL];
            int lbsi = 0;
            for (byte bt : temps){
                if(bt > -128){
                    lbs[lbsi] = bt;
                    lbsi ++;
                }
            }

            final int dataL = (int) ValueUtils.toLong(lbs);
            byte[] data = new byte[dataL];
            final int dataI = dataIn.read(data, 0, dataL);
            if (dataI == -1){
                break;
            }
            byte[] outData = dCipher.doFinal(data, 0, dataI);
            dataOut.write(outData);
            dataOut.flush();
        }
    }
}
