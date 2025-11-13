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

    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJ+8wUIHMZHsdSJKfXkBaYRHKzuN\n" +
            "SP23Q6Jvsi0X/GVN3320ylAvs7QSwRWS+FEXMRqpzMcpivjUarz7q/Qw58WFH0aMzmx2pmxhlkUs\n" +
            "Y/LxUVAtM1DfPKPohZ+a06D9tk4+hnGwVQGEwX9oBwp78VZzMzdAY5KdbceD2oQS/qP7AgMBAAEC\n" +
            "gYBF2FLofBzAoZPWGpwifOuWW0gcEfsIdUmtjQlrjkFeSl6eqJ6N0U3SPyEOPeU2D934uqY/r3qE\n" +
            "sty5JZJag8fTdP8StHJzUm2b2Mr6Sfb/ROTBhx6mdN5+S0wr8M3I6918ZZ1qAiIFuAkKWsQGxbvU\n" +
            "FUwQVrHEtDJNjXeS4miiAQJBANTNwdPRIBzw0w5kakWPRYQ9HoWE3TSXdf9rxNpDcgpFftsR7QbF\n" +
            "6SSBIKl7xGLqaxQFbAmZdQNt4WEfEwpBJOsCQQDAKW1kd7nL6FtgSLKMoEexFLVWNLD0f9g0MTtC\n" +
            "+kxz4jprqGhdaLfNwJAqnmnjQm+4p/Ra7wjSij8LYz8PWvkxAkBnivYUqlyFuGf5SMKstdmNTm/b\n" +
            "Z5p6THgNn9JYoRiMBuSCk2ZRNVsLeAj8bkxQFN+lDj5TLWfSE1TmfMg25RuhAkBvEkMJ1G5PX3IZ\n" +
            "uEuEH0zxHTAnsPMrkA3vNRm1ACpavUPZYJFalKHRSuHJ0KER3B/pkyMZwJrP31rLgUU84e+xAkA4\n" +
            "mpZ88Vn8vDbOZY35DbgH6hipcIh09tN/V03v/TBsfD/pDbEaiU8LIvK8jm1Z9qhl4rahXj4XdImv\n" +
            "FmkVhTLy";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfvMFCBzGR7HUiSn15AWmERys7jUj9t0Oib7It\n" +
            "F/xlTd99tMpQL7O0EsEVkvhRFzEaqczHKYr41Gq8+6v0MOfFhR9GjM5sdqZsYZZFLGPy8VFQLTNQ\n" +
            "3zyj6IWfmtOg/bZOPoZxsFUBhMF/aAcKe/FWczM3QGOSnW3Hg9qEEv6j+wIDAQAB";

    @Test
    public void test() throws Exception {
        byte[] shaBytes = DataUtils.sha256("admin".getBytes(StandardCharsets.UTF_8));
        System.out.println("shaBase64:" + new String(DataUtils.base64Encode(shaBytes)));
        RsaCipher RSACipher = RsaCipher.build(0).loadRandomKey().loadConfig();
        System.out.println("privateKey:\n" + RSACipher.getPrivateKey());
        System.out.println("publicKey:\n" + RSACipher.getPublicKey());
        String eText = new String(
                DataUtils.base64Encode(
                        RSACipher.encrypt(shaBytes)
                ),
                StandardCharsets.UTF_8
        );
        System.out.println("eText:" + eText);
        byte[] dBytes = RSACipher.decrypt(
                        DataUtils.base64Decode(eText.getBytes(StandardCharsets.UTF_8))
                );
        String dText = new String(DataUtils.base64Encode(dBytes));
        System.out.println("dText:" + dText);
        System.out.println("oText:" + new String(DataUtils.base64Encode(shaBytes)));
    }

    @Test
    public void test1() throws Exception {
        RsaCipher RSACipher = RsaCipher.build(0)
//                .loadRandomKey()
                .setPrivateKey(this.privateKey).setPublicKey(this.publicKey)
                .loadConfig();
        System.out.println("privateKey:\n" + RSACipher.getPrivateKey());
        System.out.println("publicKey:\n" + RSACipher.getPublicKey());
        String text = "20260210";
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
