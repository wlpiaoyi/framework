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

    public static RsaCipher build(){
        return new RsaCipher(0, 1024);
    }
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
        if(this.eCipher == null){
            throw new BusinessException("not support encrypt");
        }
        return this.eCipher.doFinal(buffer);
    }

    /**
     * 解密
     * @param buffer
     * @return
     */
    @SneakyThrows
    public synchronized byte[] decrypt(byte[] buffer){
        if(this.dCipher == null){
            throw new BusinessException("not support decrypt");
        }
        return this.dCipher.doFinal(buffer);
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
