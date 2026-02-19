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
 * <p>
 * 该类提供了AES加密和解密的基本功能，支持：
 * <ul>
 *     <li>设置密钥（支持字符串密钥或指定长度的密钥生成）</li>
 *     <li>设置初始化向量（IV）以支持CBC等模式</li>
 *     <li>对字节数组进行加密/解密</li>
 *     <li>对流数据进行分段加密/解密</li>
 *     <li>带有填充功能的加密/解密，可控制输出数据的固定长度</li>
 * </ul>
 * </p>
 *
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    对称加密工具类
 * {@code @date:}           2023/12/21 12:20
 * {@code @version:}:       1.0
 */
public class AesCipher extends Security {

    /**
     * 初始化向量（IV），用于CBC等加密模式。
     * 如果不设置IV，默认使用ECB模式（即不需要IV）。
     */
    @Getter
    private String IV;

    /** 加密/解密所使用的密钥对象 */
    @Getter
    private Key key;

    /** 解密用Cipher实例，由{@link #loadConfig()}初始化并确保线程安全（通过同步方法使用） */
    private Cipher dCipher;

    /** 加密用Cipher实例，由{@link #loadConfig()}初始化并确保线程安全（通过同步方法使用） */
    private Cipher eCipher;

    /** 分段加密/解密时使用的缓冲区大小（字节） */
    private static final int ME_DATA_SIZE = 512;

    /**
     * 构建一个空的AesCipher实例，后续需要通过{@link #setKey(String)}、{@link #setIV(String)}等配置，
     * 并调用{@link #loadConfig()}完成初始化。
     *
     * @return AesCipher实例
     */
    public static AesCipher build() {
        return new AesCipher();
    }

    /**
     * 私有构造方法，禁止外部直接实例化。
     */
    private AesCipher() {}

    /**
     * 加载配置，初始化Cipher实例。
     * <p>
     * 根据是否设置了IV，选择不同的加密算法/模式：
     * <ul>
     *     <li>若IV为null或空，使用ECB模式（{@link ConditionAes#CIPHER_ALGORITHM_AE5_5}）</li>
     *     <li>若IV不为空，使用CBC模式（{@link ConditionAes#CIPHER_ALGORITHM_AC5_5}）并设置IV</li>
     * </ul>
     * 初始化后，加密和解密Cipher分别处于加密和解密模式，可供后续调用。
     * </p>
     *
     * @return 当前AesCipher实例（已配置）
     * @throws Exception 如果Cipher初始化失败（实际由@SneakyThrows抛出，调用者需处理）
     */
    @SneakyThrows
    @Override
    public AesCipher loadConfig() {
        if (ValueUtils.isBlank(this.getIV())) {
            // 无IV，使用ECB模式
            this.dCipher = Cipher.getInstance(ConditionAes.CIPHER_ALGORITHM_AE5_5);
            this.eCipher = Cipher.getInstance(ConditionAes.CIPHER_ALGORITHM_AE5_5);
            this.eCipher.init(Cipher.ENCRYPT_MODE, this.key);
            this.dCipher.init(Cipher.DECRYPT_MODE, this.key);
        } else {
            // 有IV，使用CBC模式
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
     * 加密字节数组（全部数据）。
     *
     * @param dataBytes 待加密的字节数组
     * @return 加密后的字节数组
     * @throws IllegalBlockSizeException 如果输入数据长度不符合算法要求（例如不是块大小的倍数）
     * @throws BadPaddingException       如果数据填充不正确（通常不会发生）
     */
    public synchronized byte[] encrypt(byte[] dataBytes)
            throws IllegalBlockSizeException, BadPaddingException {
        return eCipher.doFinal(dataBytes);
    }

    /**
     * 加密字节数组的指定部分。
     *
     * @param dataBytes 待加密的字节数组
     * @param off       起始偏移量
     * @param len       要加密的长度
     * @return 加密后的字节数组
     * @throws IllegalBlockSizeException 如果输入数据长度不符合算法要求
     * @throws BadPaddingException       如果数据填充不正确
     */
    public synchronized byte[] encrypt(byte[] dataBytes, int off, int len)
            throws IllegalBlockSizeException, BadPaddingException {
        return eCipher.doFinal(dataBytes, off, len);
    }

    /**
     * 分段加密输入流中的数据，并将加密结果写入输出流。
     * <p>
     * 每次从输入流读取最多 {@link #ME_DATA_SIZE} 字节的数据，调用doFinal加密，
     * 并在输出流中先写入8字节的长度信息（表示加密后数据的长度），再写入加密后的数据。
     * 此格式与 {@link #decryptSection(InputStream, OutputStream)} 对应。
     * </p>
     *
     * @param dataIn  输入流（明文数据）
     * @param dataOut 输出流（加密后数据，包含长度头）
     * @throws IOException               如果读写流时发生I/O错误
     * @throws IllegalBlockSizeException 如果输入数据长度不符合算法要求
     * @throws BadPaddingException       如果数据填充不正确
     */
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

    /**
     * 带填充功能的加密。
     * <p>
     * 将原始数据填充到指定的长度 {@code fil}（必须大于原始数据长度+3），
     * 填充内容包括头部、数据单元和尾部，具体算法由 {@link FillTools} 实现。
     * 填充后的数据再执行加密。
     * </p>
     *
     * @param dataBytes 原始数据
     * @param fil       期望的填充后总长度（必须 ≤ 255）
     * @return 加密后的数据（长度与原始数据加密后长度有关，不是固定的fil）
     * @throws IllegalBlockSizeException 如果加密过程中数据块大小不符合要求
     * @throws BadPaddingException       如果加密填充错误
     * @throws BusinessException         如果fil参数不合法（大于255或小于最小要求）
     */
    public byte[] encryptFill(byte[] dataBytes, int fil) throws IllegalBlockSizeException, BadPaddingException {
        int data_l = dataBytes.length;
        if (fil > 255) {
            throw new BusinessException("fil can't greater than 255");
        }
        if (fil < data_l + 3) {
            throw new BusinessException("fil length can't less than " + (data_l + 3));
        }
        // 计算填充量
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
        // 填充后加密
        return this.encrypt(FillTools.putIn(dataBytes, fil_head_byte_l, fil_tail_byte_l, fil_data_unit_c));
    }

    /**
     * 解密由 {@link #encryptFill(byte[], int)} 加密的数据。
     * <p>
     * 先解密数据，然后根据填充规则去除填充部分，恢复原始数据。
     * </p>
     *
     * @param eFillDataBytes 加密后的数据（包含填充信息）
     * @param fil            原始填充时的目标长度，用于验证和计算
     * @return 解密后的原始数据
     * @throws IllegalBlockSizeException 如果解密过程中数据块大小不符合要求
     * @throws BadPaddingException       如果解密填充错误
     * @throws BusinessException         如果fil参数不合法（大于255）
     */
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

    /**
     * 解密字节数组（全部数据）。
     *
     * @param dataBytes 待解密的字节数组（密文）
     * @return 解密后的明文数据
     * @throws IllegalBlockSizeException 如果输入数据长度不符合算法要求
     * @throws BadPaddingException       如果数据填充不正确（可能密钥错误或数据损坏）
     */
    public synchronized byte[] decrypt(byte[] dataBytes) throws IllegalBlockSizeException, BadPaddingException {
        return dCipher.doFinal(dataBytes);
    }

    /**
     * 解密字节数组的指定部分。
     *
     * @param dataBytes 待解密的字节数组
     * @param off       起始偏移量
     * @param len       要解密的长度
     * @return 解密后的明文数据
     * @throws IllegalBlockSizeException 如果输入数据长度不符合算法要求
     * @throws BadPaddingException       如果数据填充不正确
     */
    public synchronized byte[] decrypt(byte[] dataBytes, int off, int len) throws IllegalBlockSizeException, BadPaddingException {
        return dCipher.doFinal(dataBytes, off, len);
    }

    /**
     * 分段解密由 {@link #encryptSection(InputStream, OutputStream)} 加密的数据。
     * <p>
     * 从输入流中读取8字节的长度头，然后读取相应长度的密文，解密后写入输出流。
     * </p>
     *
     * @param dataIn  输入流（密文，包含长度头）
     * @param dataOut 输出流（解密后的明文）
     * @throws IOException               如果读写流时发生I/O错误
     * @throws IllegalBlockSizeException 如果输入数据长度不符合算法要求
     * @throws BadPaddingException       如果数据填充不正确
     */
    public synchronized void decryptSection(InputStream dataIn, OutputStream dataOut) throws IOException, IllegalBlockSizeException, BadPaddingException {
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
     * 设置密钥（直接使用字符串的UTF-8字节作为密钥材料）。
     * <p>
     * 注意：密钥长度必须符合AES要求（128/192/256位），即对应字节长度应为16/24/32。
     * 如果字符串长度不足，可能会导致安全性降低；如果过长，多余部分会被忽略（取决于SecretKeySpec实现）。
     * 推荐使用 {@link #setKey(String, int)} 生成指定长度的密钥。
     * </p>
     *
     * @param key 密钥字符串（UTF-8编码）
     * @return 当前AesCipher实例
     */
    public AesCipher setKey(String key) {
        this.key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ConditionAes.KEY_ALGORITHM);
        return this;
    }

    /**
     * 使用指定的密钥种子和长度生成密钥。
     * <p>
     * 利用 {@link SecureRandom} 和 {@link KeyGenerator} 根据种子生成确定性的密钥，
     * 可以保证相同的种子和长度总是生成相同的密钥。长度支持128、192、256位。
     * </p>
     *
     * @param key  密钥种子字符串（UTF-8编码），用于初始化SecureRandom
     * @param size 密钥长度（位），若≤0则默认为256
     * @return 当前AesCipher实例
     * @throws NoSuchAlgorithmException 如果系统不支持指定的随机数算法（SHA1PRNG）或密钥算法（AES）
     */
    public AesCipher setKey(String key, int size) throws NoSuchAlgorithmException {
        if (size <= 0) {
            size = 256;
        }
        SecureRandom random = SecureRandom.getInstance(ConditionAes.SIGNATURE_ALGORITHM_SHA1PRNG);
        random.setSeed(key.getBytes(StandardCharsets.UTF_8));
        KeyGenerator kgen = KeyGenerator.getInstance(ConditionAes.KEY_ALGORITHM);
        kgen.init(size, random);
        this.key = kgen.generateKey();
        return this;
    }

    /**
     * 设置初始化向量（IV）。
     * <p>
     * IV用于CBC等加密模式，增加加密的随机性。如果不设置，则默认使用ECB模式。
     * IV的长度必须与算法要求的块大小一致（AES为16字节）。本方法直接使用字符串的UTF-8字节作为IV，
     * 请确保字节长度为16。
     * </p>
     *
     * @param IV 初始化向量字符串（UTF-8编码）
     * @return 当前AesCipher实例
     */
    public AesCipher setIV(String IV) {
        this.IV = IV;
        return this;
    }
}