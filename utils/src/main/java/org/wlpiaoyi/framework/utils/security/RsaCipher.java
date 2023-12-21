package org.wlpiaoyi.framework.utils.security;

import lombok.Getter;
import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.ValueUtils;
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
        return new RsaCipher(0);
    }
    public static RsaCipher build(int type){
        return new RsaCipher(type);
    }

    private RsaCipher(int type){
        this.keyAlgorithm = ConditionRsa.KEY_ALGORITHM;
        this.type = type;
    }

    @SneakyThrows
    @Override
    public RsaCipher loadConfig() {
        if(ValueUtils.isBlank(this.publicKey) || ValueUtils.isBlank(this.privateKey)){
            String[] keys = SecurityTools.intKey(1024, this.keyAlgorithm);
            this.privateKey = keys[0];
            this.publicKey = keys[1];
        }
        switch (this.type){
            case 1:{
                this.eCipher = SecurityTools.createPublicCipher(this.publicKey, this.keyAlgorithm, Cipher.ENCRYPT_MODE);
                this.dCipher = SecurityTools.createPrivateCipher(this.privateKey, this.keyAlgorithm, Cipher.DECRYPT_MODE);
            }
            break;
            default:{
                this.eCipher = SecurityTools.createPrivateCipher(this.privateKey, this.keyAlgorithm, Cipher.ENCRYPT_MODE);
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
        return this.eCipher.doFinal(buffer);
    }

    /**
     * 解密
     * @param buffer
     * @return
     */
    @SneakyThrows
    public synchronized byte[] decrypt(byte[] buffer){
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
