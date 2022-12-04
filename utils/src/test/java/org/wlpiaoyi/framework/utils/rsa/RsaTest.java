package org.wlpiaoyi.framework.utils.rsa;


import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Rsa;

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
    public void test() throws Exception {
        String a = "我的名字是 {{name}}，我永远是 {{age}}。";

        //String reg = "[1-9]\\d{4,14}";

        String reg = "\\{\\{([a-zA-Z0-9\\-])+\\}\\}";//\b表示的是单词的边界 找三个字母的单词
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(a);
        List<Integer[]> indexs = new ArrayList<>();
        while (m.find()){
            String name = m.group();
            System.out.println(m.group()); //用于获取匹配过的结果
            System.out.println(m.start()+"..."+m.end());//返回字串的索引位置
        }

        String b = a.replace("{{name}}", "11").replace("{{age}}", "121");
        Rsa rsa = Rsa.create().loadKey();
        String publicKey = rsa.publicKey;
        String privateKey = rsa.privateKey;
        String aesKey = StringUtils.getUUID32();
        String aesIV = StringUtils.getUUID32().substring(0, 16);
        System.out.println("publicKey:" + publicKey);
        System.out.println("privateKey:" + privateKey);
        System.out.println("aesKey:" + aesKey);
        System.out.println("aesIV:" + aesIV);


//        System.out.println("公钥加密——私钥解密");
//        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？";
//        System.out.println("\r加密前文字：\r\n" + source);
//        byte[] data = source.getBytes();
//        byte[] encodedData = rsa.encryptByPublicKey(data);
//        System.out.println("加密后文字：\r\n" + new String(encodedData));
//        byte[] decodedData = rsa.decryptByPrivateKey(encodedData);
//        String target = new String(decodedData);
//        System.out.println("解密后文字: \r\n" + target);

    }

    @Test
    public void test2() throws Exception {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzr0UqyIwWvFTD5WwP2WJC1bPWcCpZSRE4fPma\n" +
                "W9/kbfoBtGGlKe0GWliFndl+Z5/vjAMZ8z+cb/rjh0+S7pefd771vIrz8KtnPGvVvKORu2bT2Upr\n" +
                "/+GPDyG8HxKqYMe5qRxWX994oQclYyIWQPlj8C5FNTzccPjaPXR0JcBweQIDAQAB";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALOvRSrIjBa8VMPlbA/ZYkLVs9Zw\n" +
                "KllJETh8+Zpb3+Rt+gG0YaUp7QZaWIWd2X5nn++MAxnzP5xv+uOHT5Lul593vvW8ivPwq2c8a9W8\n" +
                "o5G7ZtPZSmv/4Y8PIbwfEqpgx7mpHFZf33ihByVjIhZA+WPwLkU1PNxw+No9dHQlwHB5AgMBAAEC\n" +
                "gYAy7iscxVtv1lHwdMb8dxFzAD/JOGHIjP1klYfqSMBdbw6+DPLgbdHRSypsNSHAwn6C15zJbjlJ\n" +
                "jjP+6guUCizPfNxE6/LfG03LhrQ+L21LKy/USBFSEB9sueNgwojSxevoM+pdVFhtRdMxaKzup9kA\n" +
                "nEq2cR6rT8fs7M6M+UMoAQJBAOBgZBsdK2ULV7M6AWQKcwzz79xypH5l8R+hnNs7PFMeQWgbHPto\n" +
                "GAHP6hcsArSUApDgF8RgXjEzp2J8keu/F+kCQQDNAmAgG9NgAk8ywxJBggq4OD65g2irzFz69wuP\n" +
                "HsxxyXfQvDDPjL5bvIi/vxIo2F1UkLsPHAvUyI0x6FBeYsoRAkEAyfVZBaK6xYdxF+xVBiP1rKoA\n" +
                "sy8pam/9mhgQpK/ru3DXNIp7CruGKFNphBPkF3/F03sxSVvoTGcO+bHgcg6dyQJAFizomvCHl74I\n" +
                "NRR2uBFJ+Y1T85ssSlELybXJUUzijnhddn20xe6SdLfbDuqrGzH0Pn59TXAaM4USCND5SIxlEQJB\n" +
                "AL6bAL0IMU48aE9RxX7EbHq+rsvkkbB9JV6f19fbLG/+nwrE+NwegpI+fN0P4LldsQqd37+QFCtt\n" +
                "p62rKrIvwGw=";
        System.out.println(StringUtils.base64Decode(publicKey));

        Rsa rsa = Rsa.create().setPublicKey(publicKey).setPrivateKey(privateKey).loadKey();
        long timer = 1;//System.currentTimeMillis();
        String source = timer + "";;
        byte[] data = source.getBytes();
        byte[] encodedData = rsa.encryptByPublicKey(data);
        String base64Str = StringUtils.base64Encode(encodedData);
        System.out.println("加密后文字：\r\n" + base64Str);



        byte[] bytes = StringUtils.base64DecodeToBytes(base64Str);
        byte[] decodedData = rsa.decryptByPrivateKey(bytes);
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);


        System.out.println("私人钥加密——公钥解密");
        source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        System.out.println("\r加密前文字：\r\n" + source);
        data = source.getBytes();
        encodedData = rsa.encryptByPrivateKey(data);
        String pstr = new String(encodedData);
        System.out.println("加密后文字：\r\n" + pstr);
        decodedData = rsa.decryptByPublicKey(pstr.getBytes());
        target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);

        System.out.println("私钥签名——公钥验证签名");
        String sign = rsa.sign(encodedData);
        System.err.println("签名:\r" + sign);
        boolean status = rsa.verify(encodedData, sign);
        System.err.println("验证结果:\r" + status);


    }


    @After
    public void tearDown() throws Exception {

    }

}
