package org.wlpiaoyi.framework.utils.security;

import org.junit.Test;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/21 11:39
 * {@code @version:}:       1.0
 */
public class SignVerifyTest {


    @Test
    public void test1() throws Exception {
        SignVerify signatureCipher = SignVerify.build().loadConfig();
        System.out.println("privateKey:\n" + signatureCipher.getPrivateKey());
        System.out.println("publicKey:\n" + signatureCipher.getPublicKey());
        String text = "我的测试按你的剑法你看得见";
        byte[] sign = signatureCipher.sign(text.getBytes(StandardCharsets.UTF_8));
        System.out.println("sign:" + new String(DataUtils.base64Encode(sign)));
        System.out.println("verify:" + signatureCipher.verify(text.getBytes(StandardCharsets.UTF_8), sign));
        System.out.println("verify:" + signatureCipher.verify("adfasdf".getBytes(StandardCharsets.UTF_8), sign));
    }
}
