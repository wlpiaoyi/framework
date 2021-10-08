package org.wlpiaoyi.framework.utils.rsa;

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



public class Rsa extends ProtectedRsa {

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
    ProtectedRsa setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    @Override
    ProtectedRsa setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    @Override
    public Rsa setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return this;
    }

    @Override
    ProtectedRsa setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
        return this;
    }

    private Rsa(){
        this.signatureAlgorithm = Rsa.SIGNATURE_ALGORITHM_SHA256withRSA;//签名算法
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
        //公钥
        this.setPublicKey(this.getPublicKey(keyPair.getPublic()))
                .setPrivateKey( this.getPrivateKey(keyPair.getPrivate()));

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
        byte[] keyBytes = decryptBASE64(this.publicKey);
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



//    public static void main(String[] args) {
//        try {
//            test();
////            testSign();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    private static void test() throws Exception {
//        Rsa rsa = Rsa.create().loadKey();
//        System.err.println("公钥加密——私钥解密");
//        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？";
//        System.out.println("\r加密前文字：\r\n" + source);
//        byte[] data = source.getBytes();
//        byte[] encodedData = rsa.encryptByPublicKey(data);
//        System.out.println("加密后文字：\r\n" + new String(encodedData));
//        byte[] decodedData = rsa.decryptByPrivateKey(encodedData);
//        String target = new String(decodedData);
//        System.out.println("解密后文字: \r\n" + target);
//    }
//
//    private static void testSign() throws Exception {
//        Map<String, Object> keyMap = initKey();
//        String publicKey = getPublicKey(keyMap);
//        System.out.println(publicKey+"===================================");
//        String privateKey = getPrivateKey(keyMap);
//        System.err.println("私钥加密——公钥解密");
//        String source = "这是一行测试RSA数字签名的无意义文字";
//        System.out.println("原文字：\r\n" + source);
//        byte[] data = source.getBytes();
//        byte[] encodedData = encryptByPrivateKey(data, privateKey);
//        System.out.println("加密后：\r\n" + new String(encodedData));
//        byte[] decodedData = decryptByPublicKey(encodedData, publicKey);
//        String target = new String(decodedData);
//        System.out.println("解密后: \r\n" + target);
//        System.err.println("私钥签名——公钥验证签名");
//        String sign = sign(encodedData, privateKey);
//        System.err.println("签名:\r" + sign);
//        boolean status = verify(encodedData, publicKey, sign);
//        System.err.println("验证结果:\r" + status);
//    }


}