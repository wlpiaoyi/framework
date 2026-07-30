package org.wlpiaoyi.framework.utils.security;

import lombok.Getter;
import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.security.condition.ConditionSm2;

import javax.crypto.Cipher;

/**
 * 国密 SM2 非对称加密工具类。
 * <p>
 * SM2 仅支持公钥加密、私钥解密（type=1）。密钥对基于 sm2p256v1 曲线，
 * 依赖 BouncyCastle Provider。
 * </p>
 *
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    国密 SM2 非对称加密
 * {@code @date:}           2026/7/30
 * {@code @version:}:       1.0
 */
public class Sm2Cipher extends Security {

    /**
     * 加密解密类型
     * 1: 公钥加密，私钥解密（SM2 唯一支持的加解密模式）
     */
    @Getter
    private final int type;

    @Getter
    protected final String keyAlgorithm;

    @Getter
    protected final String cipherAlgorithm;

    @Getter
    private String publicKey = null;

    @Getter
    private String privateKey = null;

    private Cipher eCipher;
    private Cipher dCipher;

    /**
     * 创建 SM2 实例，默认 type=1（公钥加密，私钥解密）
     */
    public static Sm2Cipher build() {
        return new Sm2Cipher(1);
    }

    /**
     * @param type 仅支持 1：公钥加密，私钥解密
     */
    public static Sm2Cipher build(int type) {
        return new Sm2Cipher(type);
    }

    private Sm2Cipher(int type) {
        if (type != 1) {
            throw new BusinessException("SM2 only supports type=1: public encrypt, private decrypt");
        }
        SecurityTools.ensureBcProvider();
        this.keyAlgorithm = ConditionSm2.KEY_PAIR_ALGORITHM;
        this.cipherAlgorithm = ConditionSm2.CIPHER_ALGORITHM;
        this.type = type;
    }

    @SneakyThrows
    public Sm2Cipher loadRandomKey() {
        String[] keys = SecurityTools.initSm2Key();
        this.privateKey = keys[0];
        this.publicKey = keys[1];
        return this;
    }

    @SneakyThrows
    @Override
    public Sm2Cipher loadConfig() {
        if (ValueUtils.isBlank(this.publicKey) && ValueUtils.isBlank(this.privateKey)) {
            throw new BusinessException("all key is null");
        }
        if (ValueUtils.isNotBlank(publicKey)) {
            this.eCipher = SecurityTools.createSm2PublicCipher(this.publicKey, Cipher.ENCRYPT_MODE);
        }
        if (ValueUtils.isNotBlank(privateKey)) {
            this.dCipher = SecurityTools.createSm2PrivateCipher(this.privateKey, Cipher.DECRYPT_MODE);
        }
        return this;
    }

    @SneakyThrows
    public synchronized byte[] encrypt(byte[] buffer) {
        return this.encrypt(buffer, 0, buffer.length);
    }

    @SneakyThrows
    public synchronized byte[] encrypt(byte[] buffer, int offset, int len) {
        if (this.eCipher == null) {
            throw new BusinessException("not support encrypt");
        }
        return this.eCipher.doFinal(buffer, offset, len);
    }

    @SneakyThrows
    public synchronized byte[] decrypt(byte[] buffer) {
        return this.decrypt(buffer, 0, buffer.length);
    }

    @SneakyThrows
    public synchronized byte[] decrypt(byte[] buffer, int offset, int len) {
        if (this.dCipher == null) {
            throw new BusinessException("not support decrypt");
        }
        return this.dCipher.doFinal(buffer, offset, len);
    }

    public Sm2Cipher setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public Sm2Cipher setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }
}
