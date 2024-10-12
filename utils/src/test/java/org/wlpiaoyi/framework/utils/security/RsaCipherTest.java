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

    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAL/WZ0qP04/DkrwzXYyZUYLsomm7\n" +
            "3OC7J9TiDjaJq90mSBju4vrUXiC8aZGHJ0gU32nTTsC4jTBWI/qpH2r3Nh6e4vYblx7duwHiLLEG\n" +
            "iyg/DWBs9FESplYJQPyuW6+qgdk2N3Bsd3HlIwvDC0XrEMbp0SrhWCuLC3ekY8QXtywJAgMBAAEC\n" +
            "gYACcXXvT9WfRxu0pJLUb5THg2wDcg5mGPwbjKV8XFSBWd4hMymrjXjaweDg/EUBI1g/vh6Kpc8R\n" +
            "MTJUHENM1sksT6x56ApEh0lCeyKDfiSo3lg025jKezsCOV0KX3ihr4uiT0vir/I5sAaGgUkc47Yz\n" +
            "iALftFNNYrdZejB9Q2TgYQJBAOvzdg8QepIbARMvrreSy2veZXoxPbdaSoDyUFZHr5DhNy2iw8Sl\n" +
            "HTF1u9tMDMiZkWvHlsN9N4b8J3KVtj89CWsCQQDQI1xP8taHMQu6pRkCCqjJCgLHUSkGMDMWBzBG\n" +
            "zH0u5ZWvKo7aqO9pLv1bL9WCB2PXrT57zvpZTdKVzWdwxDlbAkAzVhZCstAoR83VEdPOxxaRUqL7\n" +
            "kgkegnuhUJdgHlq5h/bMbBRyDYargzUrQoGph5gXPia0Q+M38FbY3G/5lFsfAkBwAdd3nrMb5aTu\n" +
            "OFKQ6rOSHLUKFf8BuYaEYDH2p6zmQ96deUWPHkJYVFU7cS5w3xE3y59IeUX4MQ0WWIdGBBLpAkB/\n" +
            "8nhUeo8eVsRBcIhtwPl7KcKDryPJ+NwuRdD92ArXbGNtqKUegdMbksjJzFJCglqR+f6yizSJSB10\n" +
            "pQpipaYj";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/1mdKj9OPw5K8M12MmVGC7KJpu9zguyfU4g42\n" +
            "iavdJkgY7uL61F4gvGmRhydIFN9p007AuI0wViP6qR9q9zYenuL2G5ce3bsB4iyxBosoPw1gbPRR\n" +
            "EqZWCUD8rluvqoHZNjdwbHdx5SMLwwtF6xDG6dEq4Vgriwt3pGPEF7csCQIDAQAB";

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
