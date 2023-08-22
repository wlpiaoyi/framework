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

        Rsa rsa = Rsa.create().loadKey(512);
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
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxWB6Lfjs2VspgZtf3Zd42H7vS7V7NxZCSD+yI\n" +
                "+zpMKukXFdbinMq/TkKstxq8A1vznCRnMQZye3yWgsAvJNQYDOkgXOwn2P0v8M6eaklu1i2tFeEp\n" +
                "SqWrUPluilNe2WJqF/apdGwODhC/7aKr0lAObEKtqPcNDPY3Ft5FUBeqrwIDAQAB";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALFYHot+OzZWymBm1/dl3jYfu9Lt\n" +
                "Xs3FkJIP7Ij7Okwq6RcV1uKcyr9OQqy3GrwDW/OcJGcxBnJ7fJaCwC8k1BgM6SBc7CfY/S/wzp5q\n" +
                "SW7WLa0V4SlKpatQ+W6KU17ZYmoX9ql0bA4OEL/toqvSUA5sQq2o9w0M9jcW3kVQF6qvAgMBAAEC\n" +
                "gYByjyjUFUvssy3yDh1OfCiKL/+lpp6KMChOoii66AblswZ1Wi9AneY+pDBf37xOXaElgeTmH/45\n" +
                "L9nwZC//hDvY5XTyD1P33QpP13cdWRsgV/m1ycjx6hgJnr8slTDNvabXk3oUya5lGDMXq2jvyagf\n" +
                "CwbxQkdFDgvFDpOdoU95UQJBAORY9FaeE7wiZmkKMnjRJ1cGQlMVV/mngFqnWCdI+RxL+1gNnPlW\n" +
                "poHdguRDaZb4SiPBe/qIBoB13Fs4hvgVj4kCQQDG0gCqeyVPcumXyTrrOG0dUCauRKgi0wGKn+22\n" +
                "Zv1VlWNceCrtzN2vNhAru8t7sIhVEge87mRDb7yT14ugw+J3AkEAhyvtP9UFo6nNa8KgjAprukU3\n" +
                "P81zrZKtFXzYXzEAXfLQj5hHYwYbPObuk8cgG0FRSgdAJ9cb2jFqxwkYRIUm2QJBAIPcOXbXr3oO\n" +
                "w1/YpQvl43AifVTs0fouqYIP6W8kxPWi20Azrn0CdzBCEd1/ckf5X6HUEG+8R6D2FUKUHNWltKcC\n" +
                "QH2Cqt2mOkPoqamCkhbpvitOcK4w/IqnxQLKCB7J93tCuklu6nslwlURrdGvyTb9jkNbrtMJHcsr\n" +
                "R2PF0PTvj28=";


        Rsa rsa = Rsa.create().setPublicKey(publicKey).setPrivateKey(privateKey).loadKey();

        System.out.println("私人钥加密——公钥解密");
        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        source = "uydh@75YG3.,";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = rsa.encryptByPublicKey(data);
        System.out.println("加密后文字16：\r\n" + new BigInteger(1, encodedData).toString(16));
        System.out.println("加密后文字64：\r\n" + StringUtils.base64Encode(encodedData));
        byte[] decodedData = rsa.decryptByPrivateKey(encodedData);
        decodedData = rsa.decryptByPrivateKey(StringUtils.base64DecodeToBytes(StringUtils.base64Encode(encodedData)));
        System.out.println("解密后文字: \r\n" + new String(decodedData));

        decodedData = rsa.decryptByPrivateKey(StringUtils.base64DecodeToBytes("XkfQwsVZhFIKcOXhZCH8sVISeGdYDW5mXGcnHOFCcmgo9+PInvI48a2Ufn3xXG/QDEdJ9cCS4wKDbRitR40flRcLj6ho/sKu14bb6GLAXycJyPGeJc4ygP/8zL0FcnMzip1cudaMHOcVvkscJtmG0ZS4heFPk7KvOY10lN3oiG8="));
        System.out.println("解密后文字: \r\n" + new String(decodedData));
    }
    @Test
    public void sign() throws Exception {

        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALWFBFfZBv5dBXvWY4dq0RsYyw2yqEqn1HKU+q0iFzv8\n" +
                "POParUS2gi2kWBgvW6gOnPuHY3Qt528BBRm/zYkkS78CAwEAAQ==";
        String privateKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAtYUEV9kG/l0Fe9Zjh2rRGxjLDbKo\n" +
                "SqfUcpT6rSIXO/w849qtRLaCLaRYGC9bqA6c+4djdC3nbwEFGb/NiSRLvwIDAQABAkBOIOKtrQwF\n" +
                "j6Q8mRpdvuwh7Zn4W110RKh8aQM8Yd/+QOhVkOooDEH4+4WO7G1ELh/Ue3cH2Dl1v0dBfjJzHkSB\n" +
                "AiEA8qkci+SWyeNlXU1q0LzRbKmBnU88e+z73wSkntsGpYsCIQC/f3yk/zZtK6FMTbn6JFjjC+4l\n" +
                "Xr4BPBiiOjrNO/oBHQIgORGmogvnnqF5NGFBrsfJZQnUbt+0tNx+O+wGn0mZnZMCIFx1Fx8qwYJw\n" +
                "IE6Q8IwfZHMq3W5Zke9SrqoU5zMUFB7ZAiEA6ivL8pimOdMKKyc97xJvAAHKTEErBRyQW71gxKVp\n" +
                "7Ts=";

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
