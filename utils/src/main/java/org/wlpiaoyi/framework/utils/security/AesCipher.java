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

    private static byte[] randomBytes(int length){
        byte[] rbs = new byte[length];
        int i = 0;
        Random random = new Random();
        while (i < length){
            byte[] rts = ValueUtils.toBytes(Math.abs(random.nextLong()));
            int rtsl = rts.length;
            int rtsi = 0;
            while (rtsi < rtsl){
                if(i >= length){
                    break;
                }
                rbs[i ++] = rts[rtsi ++];
            }
        }
        return rbs;
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
        int dl = dataBytes.length;
        if(fil > 255){
            throw new BusinessException("fil can't greater than 255");
        }
        if(dl > fil - 1){
            throw new BusinessException("dataBytes length can't greater than " + (fil -1));
        }
        //原始数据长度和数据值
        int vi = 0;
        byte[] values = new byte[dl + 1];
        values[vi ++] = (byte) dl;
        int vl = values.length;
        while (vi < vl){
            values[vi] = dataBytes[vi - 1];
            vi ++;
        }
        //第一部分数据, 填充后的原始数据
        byte[] part1 = new byte[fil];
        if(vl < fil){
            byte[] fillBytes = randomBytes(fil);
            int fl = fil - vl;
            int di = 0;
            while (di < fl){
                part1[di] = fillBytes[di];
                di ++;
            }
            di = vl;
            vi = 0;
            while (di > 0){
                di --;
                part1[di + fl] = values[vi ++];
            }
        }else if(vl == fil){
            part1 = values;
        }else{
            throw new BusinessException("values length can't greater than " + fil);
        }
        byte[] part2 = randomBytes(fil);
        byte[] data = new byte[fil * 2];
        int di = 0;
        int pi = 0;
        while (pi < fil){
            data[di ++] = part1[pi];
            data[di ++] = part2[pi];
            pi ++;
        }
        return this.encrypt(data);
    }
    /**
     * 填充解密
     * @param dataBytes 数据
     * @param fil       填充长度
     * @return: byte[]
     * @author: wlpia
     * @date: 1/24/2024 1:36 PM
     */
    public byte[] decryptFill(byte[] dataBytes, int fil) throws IllegalBlockSizeException, BadPaddingException {
        if(fil > 255){
            throw new BusinessException("fil can't greater than 255");
        }
        byte[] data = this.decrypt(dataBytes);
        int dtl = data.length;
        if(dtl != fil * 2){
            throw new BusinessException("decrypted data length must be " + (fil * 2));
        }
        int di = 0;
        //第一部分数据
        byte[] part1 = new byte[fil];
        int pi = 0;
        while (di < fil * 2){
            part1[pi ++] = data[di ++];
            di ++;
        }
        //原始数据长度数据
        byte[] vb = {part1[fil - 1]};
        int vl = (int)ValueUtils.toLong(vb);
        if(vl > fil - 1){
            throw new BusinessException("value length can't greater than " + (fil - 1));
        }
        //原始数据长度数据
        byte[] value = new byte[vl];
        int vi = 0;
        pi = fil - 2;
        while (vi < vl){
            value[vi ++] = part1[pi --];
        }
        return value;
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
