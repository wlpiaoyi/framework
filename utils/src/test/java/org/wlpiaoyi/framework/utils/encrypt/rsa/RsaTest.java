package org.wlpiaoyi.framework.utils.encrypt.rsa;


import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RsaTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void initKey() throws Exception {
//        String a = "我的名字是 {{name}}，我永远是 {{age}}。";
//
//        //String reg = "[1-9]\\d{4,14}";
//
//        String reg = "\\{\\{([a-zA-Z0-9\\-])+\\}\\}";//\b表示的是单词的边界 找三个字母的单词
//        Pattern p = Pattern.compile(reg);
//        Matcher m = p.matcher(a);
//        List<Integer[]> indexs = new ArrayList<>();
//        while (m.find()){
//            String name = m.group();
//            System.out.println(m.group()); //用于获取匹配过的结果
//            System.out.println(m.start()+"..."+m.end());//返回字串的索引位置
//        }
//        String b = a.replace("{{name}}", "11").replace("{{age}}", "121");

        Rsa rsa = Rsa.create().loadKey(2048);
        String publicKey = rsa.publicKey;
        String privateKey = rsa.privateKey;
//        String aesKey = StringUtils.getUUID32();
//        String aesIV = StringUtils.getUUID32().substring(0, 16);
        System.out.println("publicKey:" + publicKey);
        System.out.println("privateKey:" + privateKey);
//        System.out.println("aesKey:" + aesKey);
//        System.out.println("aesIV:" + aesIV);

    }

    @Test
    public void doSecurity() throws Exception {
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoJ04P9Mfgw0BMHULsWJYDOngZcdjTwdU\n" +
                "GudO992xHR7zA0t4yGVgkd3awgYEUxIqyxS3rnbokmv00EJTPsBKCMHbyJfqQJC9lDk/cg/MuyCu\n" +
                "4ijR7QgnZEFacrOxbrTmD5me63FS5WmlTFnFKcmNDj6rIRjYICusnro9+VSW89STjN3iR3T5bOW2\n" +
                "dlkWeQTCrLfQAea5wadg+WWtZb1ctTPqgyscXLpZx9oY68gJmqcaMqVEaRWn9x60T54cDBj3zcy5\n" +
                "9k6kCfpQjlzmZ87Z00oh5wdveY9FfNB/+GjTywDjkDrhz0f6Rm2WkuoLpD0Gp4J053tR0Z9w449B\n" +
                "HEe5QQIDAQAB";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCgnTg/0x+DDQEwdQuxYlgM6eBl\n" +
                "x2NPB1Qa50733bEdHvMDS3jIZWCR3drCBgRTEirLFLeuduiSa/TQQlM+wEoIwdvIl+pAkL2UOT9y\n" +
                "D8y7IK7iKNHtCCdkQVpys7FutOYPmZ7rcVLlaaVMWcUpyY0OPqshGNggK6yeuj35VJbz1JOM3eJH\n" +
                "dPls5bZ2WRZ5BMKst9AB5rnBp2D5Za1lvVy1M+qDKxxculnH2hjryAmapxoypURpFaf3HrRPnhwM\n" +
                "GPfNzLn2TqQJ+lCOXOZnztnTSiHnB295j0V80H/4aNPLAOOQOuHPR/pGbZaS6gukPQangnTne1HR\n" +
                "n3Djj0EcR7lBAgMBAAECggEAGShchdgdTiWx/f5sv8grtToc+qmUle22mtAzFQLAmf3IYB0Tj4Vx\n" +
                "mZIqkHbozn+V/GFZCWwkBGtXLsJ0z5YcV+rQRkdQBfT7UkWf+SRzE7c22BiUvo/8Zj5mWyO9CHOF\n" +
                "Za7RBF1SeA9oOOFcJK4mo13lkuBABNSny+UC4r8BNgfigfOMJMqezzc91EDFGwefHalvVm6BINpn\n" +
                "9OFw07wqrs3gTXpdq0mBY2D6mZTlgtb8ECPQD5mNB5kPnJ6bUkrpkQf3FV+bH1KGXcMYbWlxLxTk\n" +
                "fwkUfzgnJz9yV5PBFxvPvThVA2nBxAKCjKABP1GyZteRqY0Z0/HN7e6L+0r2EQKBgQDEvwh3wqEP\n" +
                "6rAU8r6mDVS63Gf95tiIR3v5/rVHeVbZqJjTmqHyQ3PpUhbQsnl89LNKCDw5bnynDmv++vYmUeVp\n" +
                "EBem24zGQQX3P8NU/fkvlhD8WL2uDzlJDZqdxn4u7hi7zllFKG1L8XYDt5bjzooIR2H6N7t24PlC\n" +
                "R9fW1CRH9wKBgQDQ/G8EXPUSyz6EIfrVV7gGFf4ICtD0rVJAqJcvOrcV4N3hQdkfDSi6sAay/uWy\n" +
                "ujCfm5Ea/MCF+/+BN7ShR8uVwihz9vqDiPi6qLUDFg/SqM4fPWpabekpozeBqIEOIYO7iFrsMddT\n" +
                "lDQo6Dqi2OrpjACctSq9WpAfhNPuHBLqhwKBgGBwMzpg0In4w67A/epvBmVBvEeca+8A+ygjTcBn\n" +
                "Hcc6+Q5f8ZnKrnpjnoNVuG8jku5q8vpDyh4o1474fMdzxTumg7FqVKqpHL+BJcH3a7mDmXfYF8HF\n" +
                "/JJudKd5sEhYtQ75qpBXrJtZFDvmNbQcfVRpiQEZaG5xAD0Y8hd/jPkpAoGBAJLjCminqX1HKy6V\n" +
                "74Zp/CcS6BTg/VMsSya1GGYSv8J55ZnAvYmspxboSB0hOXMwmKJmJkwwoGTL5R7+d0/wdXBSIjq3\n" +
                "ugYfjKstjuSXahGnohClCw56OCqS1J2mQOccuUljInzm3zCYdzzVVPlGu33hcEC0uVCTyb7NjmPK\n" +
                "iaUbAoGBAK8EEQc11LeRkm0tfgcFeelDsR3brbmIXdgNjKQnoPEQ8F28IFf1XQWaGDYCx3819w2b\n" +
                "gTvSrPHmiUmAv4JPeVBECZzgR8fGs3/HRLGjJYWwrkhXwA6zev30EzgOfOt57h/9xdgpxfv0NR4b\n" +
                "dHT1/VDf6ZQ0TS6uiIdS47jpUKJI";


        Rsa rsa = Rsa.create().setPublicKey(publicKey).setPrivateKey(privateKey).loadKey();

        System.out.println("私人钥加密——公钥解密");
        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        source = "uydh@75YG3.,安抚卡了阿斯兰的空间发叫阿里雕刻技法拉克丝的积分啊啊扣税的金发科技士大夫离开家";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = rsa.encryptByPublicKey(data);
        System.out.println("加密后文字16：\r\n" + new BigInteger(1, encodedData).toString(16));
        System.out.println("加密后文字64：\r\n" + StringUtils.base64Encode(encodedData));
        byte[] decodedData = rsa.decryptByPrivateKey(encodedData);
        decodedData = rsa.decryptByPrivateKey(StringUtils.base64DecodeToBytes(StringUtils.base64Encode(encodedData)));
        System.out.println("解密后文字: \r\n" + new String(decodedData));
    }
    @Test
    public void sign() throws Exception {

        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoJ04P9Mfgw0BMHULsWJYDOngZcdjTwdU\n" +
                "GudO992xHR7zA0t4yGVgkd3awgYEUxIqyxS3rnbokmv00EJTPsBKCMHbyJfqQJC9lDk/cg/MuyCu\n" +
                "4ijR7QgnZEFacrOxbrTmD5me63FS5WmlTFnFKcmNDj6rIRjYICusnro9+VSW89STjN3iR3T5bOW2\n" +
                "dlkWeQTCrLfQAea5wadg+WWtZb1ctTPqgyscXLpZx9oY68gJmqcaMqVEaRWn9x60T54cDBj3zcy5\n" +
                "9k6kCfpQjlzmZ87Z00oh5wdveY9FfNB/+GjTywDjkDrhz0f6Rm2WkuoLpD0Gp4J053tR0Z9w449B\n" +
                "HEe5QQIDAQAB";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCgnTg/0x+DDQEwdQuxYlgM6eBl\n" +
                "x2NPB1Qa50733bEdHvMDS3jIZWCR3drCBgRTEirLFLeuduiSa/TQQlM+wEoIwdvIl+pAkL2UOT9y\n" +
                "D8y7IK7iKNHtCCdkQVpys7FutOYPmZ7rcVLlaaVMWcUpyY0OPqshGNggK6yeuj35VJbz1JOM3eJH\n" +
                "dPls5bZ2WRZ5BMKst9AB5rnBp2D5Za1lvVy1M+qDKxxculnH2hjryAmapxoypURpFaf3HrRPnhwM\n" +
                "GPfNzLn2TqQJ+lCOXOZnztnTSiHnB295j0V80H/4aNPLAOOQOuHPR/pGbZaS6gukPQangnTne1HR\n" +
                "n3Djj0EcR7lBAgMBAAECggEAGShchdgdTiWx/f5sv8grtToc+qmUle22mtAzFQLAmf3IYB0Tj4Vx\n" +
                "mZIqkHbozn+V/GFZCWwkBGtXLsJ0z5YcV+rQRkdQBfT7UkWf+SRzE7c22BiUvo/8Zj5mWyO9CHOF\n" +
                "Za7RBF1SeA9oOOFcJK4mo13lkuBABNSny+UC4r8BNgfigfOMJMqezzc91EDFGwefHalvVm6BINpn\n" +
                "9OFw07wqrs3gTXpdq0mBY2D6mZTlgtb8ECPQD5mNB5kPnJ6bUkrpkQf3FV+bH1KGXcMYbWlxLxTk\n" +
                "fwkUfzgnJz9yV5PBFxvPvThVA2nBxAKCjKABP1GyZteRqY0Z0/HN7e6L+0r2EQKBgQDEvwh3wqEP\n" +
                "6rAU8r6mDVS63Gf95tiIR3v5/rVHeVbZqJjTmqHyQ3PpUhbQsnl89LNKCDw5bnynDmv++vYmUeVp\n" +
                "EBem24zGQQX3P8NU/fkvlhD8WL2uDzlJDZqdxn4u7hi7zllFKG1L8XYDt5bjzooIR2H6N7t24PlC\n" +
                "R9fW1CRH9wKBgQDQ/G8EXPUSyz6EIfrVV7gGFf4ICtD0rVJAqJcvOrcV4N3hQdkfDSi6sAay/uWy\n" +
                "ujCfm5Ea/MCF+/+BN7ShR8uVwihz9vqDiPi6qLUDFg/SqM4fPWpabekpozeBqIEOIYO7iFrsMddT\n" +
                "lDQo6Dqi2OrpjACctSq9WpAfhNPuHBLqhwKBgGBwMzpg0In4w67A/epvBmVBvEeca+8A+ygjTcBn\n" +
                "Hcc6+Q5f8ZnKrnpjnoNVuG8jku5q8vpDyh4o1474fMdzxTumg7FqVKqpHL+BJcH3a7mDmXfYF8HF\n" +
                "/JJudKd5sEhYtQ75qpBXrJtZFDvmNbQcfVRpiQEZaG5xAD0Y8hd/jPkpAoGBAJLjCminqX1HKy6V\n" +
                "74Zp/CcS6BTg/VMsSya1GGYSv8J55ZnAvYmspxboSB0hOXMwmKJmJkwwoGTL5R7+d0/wdXBSIjq3\n" +
                "ugYfjKstjuSXahGnohClCw56OCqS1J2mQOccuUljInzm3zCYdzzVVPlGu33hcEC0uVCTyb7NjmPK\n" +
                "iaUbAoGBAK8EEQc11LeRkm0tfgcFeelDsR3brbmIXdgNjKQnoPEQ8F28IFf1XQWaGDYCx3819w2b\n" +
                "gTvSrPHmiUmAv4JPeVBECZzgR8fGs3/HRLGjJYWwrkhXwA6zev30EzgOfOt57h/9xdgpxfv0NR4b\n" +
                "dHT1/VDf6ZQ0TS6uiIdS47jpUKJI";

//        Rsa rsa = Rsa.create().setPublicKey(publicKey).setPrivateKey(privateKey).loadKey();

        Rsa rsa = Rsa.create().setPublicKey(publicKey).setPrivateKey(privateKey).loadKey();


        System.out.println("私人钥加密——公钥解密");

        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗\n" +
                "这是一行没有任何意义的文字，你看完了等于没看，不是吗\n" +
                "这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        for (int i = 0; i < 100; i ++){
            source += "这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        }
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();

        System.out.println("私钥签名——公钥验证签名");
        String sign = rsa.sign(data);
        System.out.println("签名:" + sign);
        boolean status = rsa.verify(data, sign);
        System.out.println("验证结果:" + status);

        String path = DataUtils.USER_DIR + "/target/test/aes_simple.mp4";
        File file = new File(path);
        sign = StringUtils.base64Encode(rsa.sign(new FileInputStream(file)));
        System.out.println("签名:" + sign);
        status = rsa.verify(new FileInputStream(file), StringUtils.base64DecodeToBytes(sign));
        System.out.println("验证结果:" + status);

    }


    @After
    public void tearDown() throws Exception {

    }

}
