package org.wlpiaoyi.framework.utils.security;

import lombok.Getter;
import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
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
import java.util.Map;
import java.util.Random;

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
        if(ValueUtils.isBlank(this.getIV())){
            this.dCipher = Cipher.getInstance(ConditionAes.CIPHER_ALGORITHM_AE5_5);
            this.eCipher = Cipher.getInstance(ConditionAes.CIPHER_ALGORITHM_AE5_5);
            this.eCipher.init(Cipher.ENCRYPT_MODE, this.key);
            this.dCipher.init(Cipher.DECRYPT_MODE, this.key);
        }else{
            this.dCipher = Cipher.getInstance(ConditionAes.CIPHER_ALGORITHM_AC5_5);
            this.eCipher = Cipher.getInstance(ConditionAes.CIPHER_ALGORITHM_AC5_5);
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
     * 填充加密
     * @param dataBytes 数据
     * @param fil       填充长度
     * @return: byte[]
     * @author: wlpia
     * @date: 1/24/2024 1:36 PM
     */
    public byte[] encryptFill(byte[] dataBytes, int fil) throws IllegalBlockSizeException, BadPaddingException {
        int data_l = dataBytes.length;
        if(fil > 255){
            throw new BusinessException("fil can't greater than 255");
        }
        if(fil < data_l + 3){
            throw new BusinessException("fil length can't less than " + (data_l + 3));
        }
        //填充数量
        int fil_ud_l = fil - data_l;
        final int fil_head_byte_l;
        final int fil_tail_byte_l;
        final int max_fil_data_l = data_l - 1;
        final int fil_data_unit_c;
        if(fil_ud_l < max_fil_data_l + 2){
            fil_head_byte_l = 1;
            fil_tail_byte_l = 1;
            fil_data_unit_c = fil_ud_l - fil_head_byte_l - fil_tail_byte_l;
        }else{
            fil_data_unit_c = max_fil_data_l;
            fil_head_byte_l = (fil_ud_l - max_fil_data_l) / 2;
            fil_tail_byte_l = fil_ud_l - fil_data_unit_c - fil_head_byte_l;
        }
        return this.encrypt(FillTools.putIn(dataBytes, fil_head_byte_l, fil_tail_byte_l, fil_data_unit_c));
    }
    /**
     * 填充解密
     * @param eFillDataBytes 填充后的加密数据
     * @param fil            填充长度
     * @return: byte[]
     * @author: wlpia
     * @date: 1/24/2024 1:36 PM
     */
    public byte[] decryptFill(byte[] eFillDataBytes, int fil) throws IllegalBlockSizeException, BadPaddingException {
        byte[] fil_data_bytes = this.decrypt(eFillDataBytes);
        if(fil > 255){
            throw new BusinessException("fil can't greater than 255");
        }
        int fil_head_byte_l =  FillTools.getFillHeadByteLength(fil_data_bytes);
        int body_l = FillTools.getBodyByteLength(fil_data_bytes, fil_head_byte_l);
        int data_l = FillTools.getDataByteLength(fil_data_bytes, fil_head_byte_l);
        int fil_data_unit_c = body_l - data_l;

        return FillTools.putOut(fil_data_bytes, fil_data_unit_c);
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
