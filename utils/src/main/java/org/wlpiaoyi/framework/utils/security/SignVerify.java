package org.wlpiaoyi.framework.utils.security;

import lombok.Getter;
import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.security.condition.ConditionDsa;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Signature;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    验签
 * {@code @date:}           2023/12/21 11:14
 * {@code @version:}:       1.0
 */
@Getter
public class SignVerify extends Security{


    /** 秘钥算法 **/
    protected final String keyAlgorithm;

    /** 签名算法 **/
    protected final String signatureAlgorithm;

    /** 密钥长度 ,512,1024,... **/
    protected final int keyPairSize;

    /** 公钥 **/
    private String publicKey = null;

    /** 私钥 **/
    private String privateKey = null;


    public static SignVerify build(){
        return new SignVerify(1024, ConditionDsa.KEY_ALGORITHM, ConditionDsa.SIGNATURE_ALGORITHM_SHA1);
    }
    public static SignVerify build(String keyAlgorithm, String signatureAlgorithm){
        return new SignVerify(1024, keyAlgorithm, signatureAlgorithm);
    }
    public static SignVerify build(int keyPairSize, String keyAlgorithm, String signatureAlgorithm){
        return new SignVerify(keyPairSize, keyAlgorithm, signatureAlgorithm);
    }
    private SignVerify(int keyPairSize, String keyAlgorithm, String signatureAlgorithm) {
        this.keyPairSize = keyPairSize;
        this.keyAlgorithm = keyAlgorithm;
        this.signatureAlgorithm = signatureAlgorithm;
    }


    @SneakyThrows
    @Override
    public SignVerify loadConfig() {
        if(ValueUtils.isBlank(this.publicKey) && ValueUtils.isBlank(this.privateKey)){
            String[] keys = SecurityTools.intKey(this.keyPairSize, this.keyAlgorithm);
            this.privateKey = keys[0];
            this.publicKey = keys[1];
        }
        return this;
    }

    /**
     * 私钥签名
     * 用私钥对信息生成数字签名
     * @param dataBytes
     * @return
     * @throws Exception
     */
    public byte[] sign(byte[] dataBytes)throws Exception{
        ByteArrayInputStream inputStream = new ByteArrayInputStream(dataBytes);
        return this.sign(inputStream);
    }

    /**
     * 私钥签名
     * 用私钥对信息生成数字签名
     * @param dataIn
     * @return
     * @throws Exception
     */
    public byte[] sign(InputStream dataIn)throws Exception{
        if(ValueUtils.isBlank(this.privateKey)){
            throw new BusinessException("not have private key, can't sign!");
        }
        //用私钥对信息生成数字签名
        Signature signature = SecurityTools.createSignature(this.privateKey, this.keyAlgorithm, this.signatureAlgorithm);
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = dataIn.read(data, 0, data.length)) != -1) {
            signature.update(data, 0, nRead);
        }
        return signature.sign();
    }

    /**
     * 公钥校验
     * 校验数字签名
     * @param dataBytes  加密数据
     * @param sign 数字签名
     * @return
     * @throws Exception
     */
    public boolean verify(byte[] dataBytes, byte[] sign) throws Exception{
        ByteArrayInputStream dataIn = new ByteArrayInputStream(dataBytes);
        //验证签名是否正常
        return this.verify(dataIn, sign);
    }

    /**
     * 公钥校验
     * 校验数字签名
     * @param dataIn    加密数据流
     * @param signBytes 数字签名
     * @return
     * @throws Exception
     */
    public boolean verify(InputStream dataIn, byte[] signBytes)throws Exception{
        if(ValueUtils.isBlank(this.publicKey)){
            throw new BusinessException("not have public key, can't verify!");
        }
        Signature signature = SecurityTools.createVerifySign(this.publicKey, this.keyAlgorithm, this.signatureAlgorithm);
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = dataIn.read(data, 0, data.length)) != -1) {
            signature.update(data, 0, nRead);
        }
        //验证签名是否正常
        return signature.verify(signBytes);
    }


    public SignVerify setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public SignVerify setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }
}
