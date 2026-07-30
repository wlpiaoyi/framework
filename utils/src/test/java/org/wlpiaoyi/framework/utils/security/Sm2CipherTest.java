package org.wlpiaoyi.framework.utils.security;

import org.junit.Assert;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    SM2 单元测试
 * {@code @date:}           2026/7/30
 * {@code @version:}:       1.0
 */
public class Sm2CipherTest {

    @Test
    public void testRandomKeyEncryptDecrypt() throws Exception {
        Sm2Cipher sm2 = Sm2Cipher.build().loadRandomKey().loadConfig();
        System.out.println("privateKey:\n" + sm2.getPrivateKey());
        System.out.println("publicKey:\n" + sm2.getPublicKey());

        String text = "国密SM2非对称加密测试";
        byte[] cipherBytes = sm2.encrypt(text.getBytes(StandardCharsets.UTF_8));
        String eText = new String(DataUtils.base64Encode(cipherBytes), StandardCharsets.UTF_8);
        System.out.println("eText: " + eText);

        byte[] plainBytes = sm2.decrypt(DataUtils.base64Decode(eText.getBytes(StandardCharsets.UTF_8)));
        String dText = new String(plainBytes, StandardCharsets.UTF_8);
        System.out.println("dText: " + dText);
        Assert.assertEquals(text, dText);
    }

    @Test
    public void testReuseKey() throws Exception {
        Sm2Cipher generator = Sm2Cipher.build().loadRandomKey();
        String privateKey = generator.getPrivateKey();
        String publicKey = generator.getPublicKey();

        Sm2Cipher encryptor = Sm2Cipher.build()
                .setPublicKey(publicKey)
                .loadConfig();
        Sm2Cipher decryptor = Sm2Cipher.build()
                .setPrivateKey(privateKey)
                .loadConfig();

        byte[] data = "reuse-key-sm2".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = encryptor.encrypt(data);
        byte[] decrypted = decryptor.decrypt(encrypted);
        Assert.assertArrayEquals(data, decrypted);
    }
}
