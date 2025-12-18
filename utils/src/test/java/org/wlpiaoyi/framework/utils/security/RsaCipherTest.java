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

    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAI1zh4zfLhks3J8aI8KNa6vqeMFo\n" +
            "VE4w6k1iv3ehIR/I8EfnmxhyPMYtWp0Y3fo95Zn7kMqJPqetYPMM06f4iUclgZjP+FvMV7CxNVfM\n" +
            "rXQhA/1XtM6hOA9PowCYHwWUIp8wgGGNtE/MZNjuCLvfvGqAfKPykky7b4ejwqD+xStHAgMBAAEC\n" +
            "gYAHdnQURdq6tS8K0Su7s+Sy2aMMi2RkJpG8NVke/6803exYttHPqvpd92IKv96AckgbSJK2hMyX\n" +
            "7/5gmal5scybyvucDukXv/bTHhkwpR97tWTz0emiv8K+cFUlp6MKqVJXg8ZzeOsmpvsIV2NQk9mf\n" +
            "IAbFXH/07JjgN6lP/+x6pQJBANsPTgqWy3Zk0CWxaQe7DlyxtR2JFWOYsWUqnAd+HmCFcVTP0hjT\n" +
            "Zx07e3n5wjLff/vDsupB9C3397WUiyNbWCUCQQClTeglDLLQOk+fUqZKS4IlOzxg6LsCRalQvtaa\n" +
            "FCX24XfSv/iYGqE+JabNu8g1b0tMOcEFv48XUZ5LoMkQjxP7AkBlrFrai1bwIqaBeDB5iBaIa2rW\n" +
            "xJOK4IolnHtC9wR+ZDFP3g1zvFs1tDABUy0Rk67BWfmmxOnilB8Cxmk2BeWJAkA3QO1BxRbYB0Wq\n" +
            "CaRP3SFpdH1gHyqzPbm0pbVx1x5BgWfd6BEeNniDH268AfKP+d1/Yyaj1z3rG3r/6ISMpmaVAkA/\n" +
            "8RLymj8PIJHvVtBkxP0pHsnhMmlKwKO4C1xu26HtQXu/GDwlKnayQGTqq2Oiuu0hki+HQBwmBLjF\n" +
            "pRbgUsVd";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNc4eM3y4ZLNyfGiPCjWur6njBaFROMOpNYr93\n" +
            "oSEfyPBH55sYcjzGLVqdGN36PeWZ+5DKiT6nrWDzDNOn+IlHJYGYz/hbzFewsTVXzK10IQP9V7TO\n" +
            "oTgPT6MAmB8FlCKfMIBhjbRPzGTY7gi737xqgHyj8pJMu2+Ho8Kg/sUrRwIDAQAB";

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
//        String localName = "海南省";
//        String browserUlr = "https://hi.122.gov.cn/views/memrent/vehlist.html";
        String localName = "四川省";
        String browserUlr = "https://sc.122.gov.cn/views/memrent/vehlist.html";
        String text = "20260210" + "," + localName + "," + browserUlr;
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
