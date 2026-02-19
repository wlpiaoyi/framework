package org.wlpiaoyi.framework.utils.security;

import lombok.Getter;
import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.security.condition.ConditionRsa;

import javax.crypto.Cipher;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    非对称加密
 * {@code @date:}           2023/12/21 11:01
 * {@code @version:}:       1.0
 */
public class RsaCipher extends Security{

    /** 加密解密类型
     * 0: 私钥加密，公钥解密
     * 1: 私钥解密，公钥加密
     * **/
    @Getter
    private final int type;

    /** 秘钥算法 **/
    @Getter
    protected final String keyAlgorithm;

    /** 密钥长度 ,512,1024,... **/
    protected final int keyPairSize;

    /** 公钥 **/
    @Getter
    private String publicKey = null;

    /** 私钥 **/
    @Getter
    private String privateKey = null;

    /** 加密 **/
    private Cipher eCipher;
    /** 解密 **/
    private Cipher dCipher;

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 创建RSA密钥对
     * <br/>
     * type默认0: 私钥加密，公钥解密
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/21 16:22</p>
     * <p><b>{@code @return:}</b>{@link RsaCipher}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static RsaCipher build(){
        return new RsaCipher(0, 1024);
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 创建RSA密钥对
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>type</b>
     * {@link int}
     * type=0: 私钥加密，公钥解密,
     * type=1: 私钥解密，公钥加密
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/21 16:23</p>
     * <p><b>{@code @return:}</b>{@link RsaCipher}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static RsaCipher build(int type){
        return new RsaCipher(type, 1024);
    }
    public static RsaCipher build(int type, int keyPairSize){
        return new RsaCipher(type, keyPairSize);
    }

    private RsaCipher(int type, int keyPairSize){
        this.keyAlgorithm = ConditionRsa.KEY_ALGORITHM;
        this.keyPairSize = keyPairSize;
        this.type = type;
    }

    @SneakyThrows
    public RsaCipher loadRandomKey(){
        String[] keys = SecurityTools.intKey(this.keyPairSize, this.keyAlgorithm);
        this.privateKey = keys[0];
        this.publicKey = keys[1];
        return this;
    }

    @SneakyThrows
    @Override
    public RsaCipher loadConfig() {
        if(ValueUtils.isBlank(this.publicKey) && ValueUtils.isBlank(this.privateKey)){
            throw new BusinessException("all key is null");
        }
        switch (this.type){
            case 1:{
                if(ValueUtils.isNotBlank(publicKey))
                    this.eCipher = SecurityTools.createPublicCipher(this.publicKey, this.keyAlgorithm, Cipher.ENCRYPT_MODE);
                if(ValueUtils.isNotBlank(privateKey))
                    this.dCipher = SecurityTools.createPrivateCipher(this.privateKey, this.keyAlgorithm, Cipher.DECRYPT_MODE);
            }
            break;
            default:{
                if(ValueUtils.isNotBlank(privateKey))
                    this.eCipher = SecurityTools.createPrivateCipher(this.privateKey, this.keyAlgorithm, Cipher.ENCRYPT_MODE);
                if(ValueUtils.isNotBlank(publicKey))
                    this.dCipher = SecurityTools.createPublicCipher(this.publicKey, this.keyAlgorithm, Cipher.DECRYPT_MODE);
            }
            break;
        }
        return this;
    }

    /**
     * 加密
     * @param buffer
     * @return
     */
    @SneakyThrows
    public synchronized byte[] encrypt(byte[] buffer){
        return this.encrypt(buffer, 0, buffer.length);
    }
    @SneakyThrows
    public synchronized byte[] encrypt(byte[] buffer, int offset, int len){
        if(this.eCipher == null){
            throw new BusinessException("not support encrypt");
        }
        return this.eCipher.doFinal(buffer, offset, len);
    }
    /**
     * 解密
     * @param buffer
     * @return
     */
    @SneakyThrows
    public synchronized byte[] decrypt(byte[] buffer){
        return this.decrypt(buffer, 0, buffer.length);
    }
    @SneakyThrows
    public synchronized byte[] decrypt(byte[] buffer, int offset, int len){
        if(this.dCipher == null){
            throw new BusinessException("not support decrypt");
        }
        return this.dCipher.doFinal(buffer, offset, len);
    }

    public RsaCipher setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public RsaCipher setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }
}
