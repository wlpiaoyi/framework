package org.wlpiaoyi.framework.utils.security;

import lombok.Getter;
import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.security.condition.ConditionSm4;

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
import java.security.SecureRandom;

/**
 * 国密 SM4 对称加密工具类。
 * <p>
 * SM4 为分组对称密码，密钥长度固定 128 位。支持：
 * <ul>
 *     <li>设置密钥（字符串密钥或按种子生成）</li>
 *     <li>设置 IV（CBC）；未设置时使用 ECB</li>
 *     <li>字节数组加解密、流式分段加解密、填充加解密</li>
 * </ul>
 * 依赖 BouncyCastle Provider。
 * </p>
 *
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    国密 SM4 对称加密
 * {@code @date:}           2026/7/30
 * {@code @version:}:       1.0
 */
public class Sm4Cipher extends Security {

    @Getter
    private String IV;

    @Getter
    private Key key;

    private Cipher dCipher;

    private Cipher eCipher;

    private static final int ME_DATA_SIZE = 512;

    public static Sm4Cipher build() {
        return new Sm4Cipher();
    }

    private Sm4Cipher() {
        SecurityTools.ensureBcProvider();
    }

    @SneakyThrows
    @Override
    public Sm4Cipher loadConfig() {
        SecurityTools.ensureBcProvider();
        if (ValueUtils.isBlank(this.getIV())) {
            this.dCipher = Cipher.getInstance(ConditionSm4.CIPHER_ALGORITHM_ECB, ConditionSm4.PROVIDER);
            this.eCipher = Cipher.getInstance(ConditionSm4.CIPHER_ALGORITHM_ECB, ConditionSm4.PROVIDER);
            this.eCipher.init(Cipher.ENCRYPT_MODE, this.key);
            this.dCipher.init(Cipher.DECRYPT_MODE, this.key);
        } else {
            this.dCipher = Cipher.getInstance(ConditionSm4.CIPHER_ALGORITHM_CBC, ConditionSm4.PROVIDER);
            this.eCipher = Cipher.getInstance(ConditionSm4.CIPHER_ALGORITHM_CBC, ConditionSm4.PROVIDER);
            byte[] ivBytes = this.getIV().getBytes(StandardCharsets.UTF_8);
            this.dCipher.init(Cipher.DECRYPT_MODE, this.key, new IvParameterSpec(ivBytes));
            this.eCipher.init(Cipher.ENCRYPT_MODE, this.key, new IvParameterSpec(ivBytes));
        }
        return this;
    }

    public synchronized byte[] encrypt(byte[] dataBytes)
            throws IllegalBlockSizeException, BadPaddingException {
        return eCipher.doFinal(dataBytes);
    }

    public synchronized byte[] encrypt(byte[] dataBytes, int off, int len)
            throws IllegalBlockSizeException, BadPaddingException {
        return eCipher.doFinal(dataBytes, off, len);
    }

    public synchronized void encryptSection(InputStream dataIn, OutputStream dataOut)
            throws IOException, IllegalBlockSizeException, BadPaddingException {
        int nRead;
        byte[] data = new byte[ME_DATA_SIZE];
        while ((nRead = dataIn.read(data, 0, data.length)) != -1) {
            byte[] outBytes = eCipher.doFinal(data, 0, nRead);
            int dataL = outBytes.length;
            byte[] lbs = ValueUtils.longToBytes(dataL, 8);
            dataOut.write(lbs);
            dataOut.write(outBytes);
            dataOut.flush();
        }
    }

    public byte[] encryptFill(byte[] dataBytes, int fil) throws IllegalBlockSizeException, BadPaddingException {
        int data_l = dataBytes.length;
        if (fil > 255) {
            throw new BusinessException("fil can't greater than 255");
        }
        if (fil < data_l + 3) {
            throw new BusinessException("fil length can't less than " + (data_l + 3));
        }
        int fil_ud_l = fil - data_l;
        final int fil_head_byte_l;
        final int fil_tail_byte_l;
        final int max_fil_data_l = data_l - 1;
        final int fil_data_unit_c;
        if (fil_ud_l < max_fil_data_l + 2) {
            fil_head_byte_l = 1;
            fil_tail_byte_l = 1;
            fil_data_unit_c = fil_ud_l - fil_head_byte_l - fil_tail_byte_l;
        } else {
            fil_data_unit_c = max_fil_data_l;
            fil_head_byte_l = (fil_ud_l - max_fil_data_l) / 2;
            fil_tail_byte_l = fil_ud_l - fil_data_unit_c - fil_head_byte_l;
        }
        return this.encrypt(FillTools.putIn(dataBytes, fil_head_byte_l, fil_tail_byte_l, fil_data_unit_c));
    }

    public byte[] decryptFill(byte[] eFillDataBytes, int fil) throws IllegalBlockSizeException, BadPaddingException {
        byte[] fil_data_bytes = this.decrypt(eFillDataBytes);
        if (fil > 255) {
            throw new BusinessException("fil can't greater than 255");
        }
        int fil_head_byte_l = FillTools.getFillHeadByteLength(fil_data_bytes);
        int body_l = FillTools.getBodyByteLength(fil_data_bytes, fil_head_byte_l);
        int data_l = FillTools.getDataByteLength(fil_data_bytes, fil_head_byte_l);
        int fil_data_unit_c = body_l - data_l;

        return FillTools.putOut(fil_data_bytes, fil_data_unit_c);
    }

    public synchronized byte[] decrypt(byte[] dataBytes) throws IllegalBlockSizeException, BadPaddingException {
        return dCipher.doFinal(dataBytes);
    }

    public synchronized byte[] decrypt(byte[] dataBytes, int off, int len) throws IllegalBlockSizeException, BadPaddingException {
        return dCipher.doFinal(dataBytes, off, len);
    }

    public synchronized void decryptSection(InputStream dataIn, OutputStream dataOut)
            throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] lbs = new byte[8];
        while (dataIn.read(lbs, 0, lbs.length) != -1) {
            final int dataL = (int) ValueUtils.byteToLong(lbs);
            byte[] data = new byte[dataL];
            final int dataI = dataIn.read(data, 0, dataL);
            if (dataI == -1) {
                break;
            }
            byte[] outData = dCipher.doFinal(data, 0, dataI);
            dataOut.write(outData);
            dataOut.flush();
            lbs = new byte[8];
        }
    }

    /**
     * 使用字符串 UTF-8 字节作为密钥材料（长度应为 16 字节）。
     */
    public Sm4Cipher setKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != ConditionSm4.IV_SIZE) {
            throw new BusinessException("SM4 key length must be 16 bytes, actual: " + keyBytes.length);
        }
        this.key = new SecretKeySpec(keyBytes, ConditionSm4.KEY_ALGORITHM);
        return this;
    }

    /**
     * 按种子生成确定性 SM4 密钥（固定 128 位）。
     *
     * @param key  密钥种子
     * @param size 密钥长度（位），≤0 或非 128 时按 128 处理
     */
    /**
     * 按种子生成确定性 SM4 密钥（固定 128 位）。
     *
     * @param key  密钥种子
     * @param size 密钥长度（位），≤0 或非 128 时按 128 处理
     */
    @SneakyThrows
    public Sm4Cipher setKey(String key, int size) {
        if (size <= 0 || size != ConditionSm4.KEY_SIZE) {
            size = ConditionSm4.KEY_SIZE;
        }
        SecurityTools.ensureBcProvider();
        SecureRandom random = SecureRandom.getInstance(ConditionSm4.SIGNATURE_ALGORITHM_SHA1PRNG);
        random.setSeed(key.getBytes(StandardCharsets.UTF_8));
        KeyGenerator kgen = KeyGenerator.getInstance(ConditionSm4.KEY_ALGORITHM, ConditionSm4.PROVIDER);
        kgen.init(size, random);
        this.key = kgen.generateKey();
        return this;
    }

    /**
     * 设置 IV（CBC），UTF-8 字节长度须为 16。
     */
    public Sm4Cipher setIV(String IV) {
        if (ValueUtils.isNotBlank(IV)
                && IV.getBytes(StandardCharsets.UTF_8).length != ConditionSm4.IV_SIZE) {
            throw new BusinessException("SM4 IV length must be 16 bytes");
        }
        this.IV = IV;
        return this;
    }
}
