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
//        SignVerify signatureCipher = SignVerify.build().loadConfig();
        SignVerify signatureCipher = SignVerify.build()
//                .setPrivateKey(
//                "MIIBTAIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2\n" +
//                "USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4\n" +
//                "O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmC\n" +
//                "ouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCB\n" +
//                "gLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhR\n" +
//                "kImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFwIVAJTJSY9uol8Ir8PB2mMryGMP4WnX"
//        ).setPublicKey(
//                "MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZp\n" +
//                "RV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fn\n" +
//                "xqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuE\n" +
//                "C/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJ\n" +
//                "FnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImo\n" +
//                "g9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAd+cgvEmlenrnIecVYCqI+R+q89KaWbYnXKZf\n" +
//                "CFMIHD8s9isx0ZiOEGa6T+d8+Y0ZLHXzyynWbUHNNZvesBs2Ip84v6MdHVUfFbaTovxXsyjd5CMB\n" +
//                "xVAOMjQkSYgL8Puuo6SR1LQ1oLMYpn/BI/bmSld5LRB9iA1TlgW6BJ1Iv94="
//
//        )
                .loadConfig();
        System.out.println("privateKey:\n" + signatureCipher.getPrivateKey());
        System.out.println("publicKey:\n" + signatureCipher.getPublicKey());
        String text = "admin";
        int i = 10;
        while (i -- > 0){
            text += text;
        }
        byte[] sign = signatureCipher.sign(text.getBytes(StandardCharsets.UTF_8));
//        String signStr = "MCwCFDmNjqJJV2sUuPabsoYZxJG33HNWAhRaBfnBW/e4Xbf018ik5vWnICKheA==";
//        byte[] sign = DataUtils.base64Decode(signStr.getBytes());
        System.out.println("sign:" + new String(DataUtils.base64Encode(sign)));
        System.out.println("verify:" + signatureCipher.verify(text.getBytes(StandardCharsets.UTF_8), sign));
        System.out.println("verify:" + signatureCipher.verify("adfasdf".getBytes(StandardCharsets.UTF_8), sign));
    }
}
