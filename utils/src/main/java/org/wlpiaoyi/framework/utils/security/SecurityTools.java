package org.wlpiaoyi.framework.utils.security;

import org.wlpiaoyi.framework.utils.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/21 10:59
 * {@code @version:}:       1.0
 */
class SecurityTools {

    /**
     * 签名工具
     * 用私钥对信息生成数字签名
     * @param privateKey Base64私钥
     * @param keyAlgorithm 秘钥算法
     * @param signatureAlgorithm 签名算法
     * @return
     * @throws Exception
     */
    static Signature createSignature(String privateKey, String keyAlgorithm, String signatureAlgorithm)throws Exception{
        //解密私钥
        byte[] keyBytes = StringUtils.base64DecodeToBytes(privateKey);
        //构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        //用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(signatureAlgorithm);
        signature.initSign(keyFactory.generatePrivate(pkcs8EncodedKeySpec));
        return signature;
    }

    /**
     * 验签工具
     * 校验数字签名
     * @param publicKey Base64公钥
     * @param keyAlgorithm 秘钥算法
     * @param signatureAlgorithm 签名算法
     * @return
     * @throws Exception
     */
    static Signature createVerifySign(String publicKey, String keyAlgorithm, String signatureAlgorithm) throws Exception{
        //解密公钥
        byte[] keyBytes = StringUtils.base64DecodeToBytes(publicKey);
        //构造X509EncodedKeySpec对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);

        Signature signature = Signature.getInstance(signatureAlgorithm);
        signature.initVerify(keyFactory.generatePublic(x509EncodedKeySpec));
        return signature;
    }

    /**
     * 创建私钥加密解密工具
     * @param privateKey Base64私钥
     * @param keyAlgorithm 秘钥算法
     * @param opmode Cipher.ENCRYPT_MODE,DECRYPT_MODE
     * @return
     * @throws Exception
     */
    static Cipher createPrivateCipher(String privateKey, String keyAlgorithm, int opmode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException {
        //私钥
        byte[] keyBytes = StringUtils.base64DecodeToBytes(privateKey);
        //取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        Key key = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        //对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(opmode, key);
        return cipher;
    }

    /**
     * 创建公钥加密解密工具
     * @param publicKey Base64公钥
     * @param keyAlgorithm 秘钥算法
     * @param opmode Cipher.ENCRYPT_MODE,DECRYPT_MODE
     * @return
     * @throws Exception
     */
    static Cipher createPublicCipher(String publicKey, String keyAlgorithm, int opmode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException {
        //公钥
        byte[] keyBytes = StringUtils.base64DecodeToBytes(publicKey);
        //取公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        Key key = keyFactory.generatePublic(x509EncodedKeySpec);

        //对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(opmode, key);
        return cipher;
    }

    /**
     * 初始化密钥
     * @param keyPairSize 密钥长度 ,512,1024,...
     * @param keyAlgorithm 秘钥算法
     * @return [privateKey, publicKey]
     * @throws Exception
     */
    static String[] intKey(int keyPairSize, String keyAlgorithm) throws Exception {
        /*
         * 初始化密钥
         * RSA加密解密的实现，需要有一对公私密钥，公私密钥的初始化如下：
         * */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithm);
        keyPairGenerator.initialize(keyPairSize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        String[] keyArgs = {
                StringUtils.base64Encode(keyPair.getPrivate().getEncoded()),
                StringUtils.base64Encode(keyPair.getPublic().getEncoded()),
        };

        return keyArgs;
    }
}
