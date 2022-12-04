package org.wlpiaoyi.framework.mybatis.utils.rsa;

import org.wlpiaoyi.framework.mybatis.utils.ValueUtils;

import java.io.Serializable;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


/**
 * RSA加密算法是一种非对称加密算法，公钥加密私钥解密。
 * RSA是1977年由罗纳德·李维斯特（Ron Rivest）、阿迪·萨莫尔（Adi Shamir）和伦纳德·阿德曼（Leonard Adleman）一起提出的。
 * 当时他们三人都在麻省理工学院工作。RSA就是他们三人姓氏开头字母拼在一起组成的。
 * RSA非对称加密解密内容长度是有限制的，加密长度不超过117Byte，解密长度不超过128Byte
 */
public class Rsa extends ProtectedRsa  implements Serializable {

    /*
    * 秘钥算法
    * */
    //============================================>
    public static final String KEY_ALGORTHM_RSA="RSA";
//    public static final String KEY_ALGORTHM_DSA ="DSA";
//    public static final String KEY_ALGORTHM_ECC ="ECC";
    //<============================================

    /*
    * 签名算法
    * 512~65536位（密钥长度必须是64的倍数）
    * */
    //============================================>
    public static final String SIGNATURE_ALGORITHM_MD5withRSA = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA1withRSA = "SHA1withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA224withRSA = "SHA224withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA256withRSA = "SHA256withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA384withRSA = "SHA384withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA512withRSA = "SHA512withRSA";
    public static final String SIGNATURE_ALGORITHM_RIPEMD128withRSA = "RIPEMD128withRSA";
    public static final String SIGNATURE_ALGORITHM_RIPEMD160withRSA = "RIPEMD160withRSA";
    //<============================================

    @Override
    public Rsa setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    @Override
    public Rsa setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    @Override
    public Rsa setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return this;
    }

    @Override
    public Rsa setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
        return this;
    }

    protected Rsa(){
        this.signatureAlgorithm = Rsa.SIGNATURE_ALGORITHM_SHA1withRSA;//签名算法
        this.keyAlgorithm = Rsa.KEY_ALGORTHM_RSA;//秘钥算法
    }

    public static Rsa create(){
        Rsa rsa = new Rsa();
        return rsa;
    }

    public Rsa loadKey() throws Exception{
        return this.loadKey(1024);
    }

    public Rsa loadKey(int keyPairSize) throws Exception {
        /*
        * 初始化密钥
        * RSA加密解密的实现，需要有一对公私密钥，公私密钥的初始化如下：
        * */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(this.keyAlgorithm);
        keyPairGenerator.initialize(keyPairSize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        if(ValueUtils.isBlank(this.publicKey)){
            this.setPublicKey(this.getPublicKey(keyPair.getPublic()));
        }
        if( ValueUtils.isBlank(this.privateKey)){
            this.setPrivateKey(this.getPrivateKey(keyPair.getPrivate()));
        }

        return this;
    }

    //两种加密形式
    //方法一
    /**
     * 用私钥加密
     * @param data 加密数据
     * @return
     * @throws Exception
     */
    public byte[] encryptByPrivateKey(byte[] data)throws Exception{
        //解密密钥
        byte[] keyBytes = decryptBASE64(this.privateKey);
        //取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     * @param data  加密数据
     * @return
     * @throws Exception
     */
    public byte[] decryptByPrivateKey(byte[] data)throws Exception{
        //对私钥解密
        byte[] keyBytes = decryptBASE64(this.privateKey);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(this.keyAlgorithm);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        //对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    //方式二
    /**
     * 用公钥加密
     * @param data  加密数据
     * @return
     * @throws Exception
     */
    public byte[] encryptByPublicKey(byte[] data)throws Exception{
        //对公钥解密
        byte[] keyBytes = decryptBASE64(this.publicKey);
        //取公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(this.keyAlgorithm);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        //对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }


    /**
     * 用公钥解密
     * @param data  加密数据
     * @return
     * @throws Exception
     */
    public byte[] decryptByPublicKey(byte[] data)throws Exception{
        //对私钥解密
        byte[] keyBytes = decryptBASE64(this.publicKey);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(this.keyAlgorithm);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        //对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }




    /**
     * 私钥签名
     * 用私钥对信息生成数字签名
     * @param data
     * @return
     * @throws Exception
     */
    public String sign(byte[] data)throws Exception{
        //解密私钥
        byte[] keyBytes = decryptBASE64(this.privateKey);
        //构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(this.keyAlgorithm);
        //取私钥匙对象
        PrivateKey privateKey2 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        //用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(this.signatureAlgorithm);
        signature.initSign(privateKey2);
        signature.update(data);

        return encryptBASE64(signature.sign());
    }


    /**
     * 公钥校验
     * 校验数字签名
     * @param data  加密数据
     * @param sign  数字签名
     * @return
     * @throws Exception
     */
    public boolean verify(byte[] data, String sign)throws Exception{
        //解密公钥
        byte[] keyBytes = decryptBASE64(this.publicKey);
        //构造X509EncodedKeySpec对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(this.keyAlgorithm);
        //取公钥匙对象
        PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);

        Signature signature = Signature.getInstance(this.signatureAlgorithm);
        signature.initVerify(publicKey2);
        signature.update(data);
        //验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }


}