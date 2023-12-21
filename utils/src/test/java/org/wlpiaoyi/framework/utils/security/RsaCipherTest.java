package org.wlpiaoyi.framework.utils.security;

import org.junit.Test;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/21 11:37
 * {@code @version:}:       1.0
 */
public class RsaCipherTest {


    @Test
    public void test1() throws Exception {
        RsaCipher RSACipher = RsaCipher.build(0).loadConfig();
        System.out.println("privateKey:\n" + RSACipher.getPrivateKey());
        System.out.println("publicKey:\n" + RSACipher.getPublicKey());
        String text = "我的加密测试我的加密测试我的加密测";
        System.out.println("text:" + text);
        String eText = new String(
                DataUtils.base64Encode(
                        RSACipher.encrypt(text.getBytes(StandardCharsets.UTF_8))
                ),
                StandardCharsets.UTF_8
        );
        System.out.println("eText:" + eText);
        String dText = new String(
                RSACipher.decrypt(
                        DataUtils.base64Decode(eText.getBytes(StandardCharsets.UTF_8))
                ),
                StandardCharsets.UTF_8
        );
        System.out.println("dText:" + dText);
    }
}
