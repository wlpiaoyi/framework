package org.wlpiaoyi.framework.utils.aes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AesTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws Exception {

        Aes aes = Aes.create().setKey("abcd567890ABCDEF1234567890ABCDEF").setIV("abcd567890123456").load();
        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = aes.encrypt(data);
        String pstr = new String(encodedData);
        System.out.println("加密后文字：\r\n" + pstr);
        byte[] decodedData = aes.decrypt(encodedData);
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);
        String a = new String(aes.decrypt("R0trXMrMa+M5fvfbpRuiQB9jtTF58Yl66D9ormTT/y9ZJmFZqVx7+8/lc7P7fr5e5Tfl6PwC1pWpuGEqg0jE4w==".getBytes()));
        System.out.println("---" + a);

    }


    @After
    public void tearDown() throws Exception {

    }
}
