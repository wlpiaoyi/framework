package org.wlpiaoyi.framework.utils.encrypt.rsa;

import org.wlpiaoyi.framework.utils.StringUtils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


@Deprecated
abstract class AbstractRsa extends Coder {

    /** 公钥 **/
    protected String publicKey;

    /** 私钥 **/
    protected String privateKey;

    /** 签名算法 **/
    protected String signatureAlgorithm;

    /** 秘钥算法 **/
    protected String keyAlgorithm;

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置公钥
     * @param publicKey
     * @return
     */
    abstract AbstractRsa setPublicKey(String publicKey);

    /**
     * 设置私钥
     * @param privateKey
     * @return
     */
    abstract AbstractRsa setPrivateKey(String privateKey);

    /**
     * 设置签名算法
     * @param signatureAlgorithm
     * @return
     */
    abstract AbstractRsa setSignatureAlgorithm(String signatureAlgorithm);

    /**
     * 设置秘钥算法
     * @param keyAlgorithm
     * @return
     */
    abstract AbstractRsa setKeyAlgorithm(String keyAlgorithm);

    /**
     * 加载密钥
     * @return
     * @throws Exception
     */
    abstract Rsa loadKey() throws Exception;

    /**
     * 加载密钥
     * @param keyPairSize
     * @return
     * @throws Exception
     */
    abstract Rsa loadKey(int keyPairSize) throws Exception;

    /**
     * String类型公钥，转化为对象
     * @return
     * @throws Exception
     */
    protected PublicKey parsePublicKey(String key)throws Exception{
        //解密公钥
        byte[] keyBytes = StringUtils.base64DecodeToBytes(key);
        //构造X509EncodedKeySpec对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(this.keyAlgorithm);
        //取公钥匙对象
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        return publicKey;
    }

    /**
     * String类型私钥，转化为对象
     * @return
     * @throws Exception
     */
    protected PrivateKey parsePrivateKey(String key)throws Exception{
        //解密私钥
        byte[] keyBytes = StringUtils.base64DecodeToBytes(key);
        //构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(this.keyAlgorithm);
        //取私钥匙对象
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        return privateKey;
    }

    /**
     * 取得公钥，并转化为String类型
     * @param key 公钥对象
     * @return
     * @throws Exception
     */
    protected String getPublicKey(Key key)throws Exception{
        return StringUtils.base64Encode(key.getEncoded());
    }

    /**
     * 取得私钥，并转化为String类型
     * @param key 私钥对象
     * @return
     * @throws Exception
     */
    protected String getPrivateKey(Key key) throws Exception{
        return StringUtils.base64Encode(key.getEncoded());
    }
}
