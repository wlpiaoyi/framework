package org.wlpiaoyi.framework.utils.security;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/21 14:19
 * {@code @version:}:       1.0
 */
public class AesCipherTest {
    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void test() throws Exception {

        AesCipher aes = AesCipher.build().setKey(StringUtils.getUUID32()).loadConfig();
        System.out.println("AEA Key:\n" + aes.getKey().toString());
        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = aes.encrypt(data);
        String pstr = new String(StringUtils.base64Encode(encodedData));
        System.out.println("加密后文字：\r\n" + pstr);
        byte[] decodedData = aes.decrypt(StringUtils.base64DecodeToBytes(pstr));
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);

        String keyStr = StringUtils.getUUID32() + StringUtils.getUUID32();
        aes = AesCipher.build().setKey(keyStr, 128)
                .setIV(StringUtils.getUUID32().substring(0, 16)).loadConfig();
        System.out.println("AEA keyStr:\n" + keyStr);
        System.out.println("AEA Key:\n" + ValueUtils.bytesToHex(aes.getKey().getEncoded()));
        System.out.println("AEA IV:\n" + aes.getIV().toString());
        source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        System.out.println("\r加密前文字：\r\n" + source);
        data = source.getBytes();
        encodedData = aes.encrypt(data);
        pstr = new String(StringUtils.base64Encode(encodedData));
        System.out.println("加密后文字：\r\n" + pstr);
        decodedData = aes.decrypt(StringUtils.base64DecodeToBytes(pstr));
        target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);
    }

    @Test
    public void test2() throws Exception {
        String path = DataUtils.USER_DIR + "/target/test/aes_simple.mp4";
//        String path = DataUtils.USER_DIR + "/target/test/1.txt";
        File file = new File(path);
        AesCipher aes = AesCipher.build().setKey(StringUtils.getUUID32())
                .setIV(StringUtils.getUUID32().substring(0, 16)).loadConfig();
        FileInputStream in = new FileInputStream(file);
        File dFile = new File(DataUtils.USER_DIR + "/target/test/d_data.mp4");
//        File dFile = new File(DataUtils.USER_DIR + "/target/test/d_data.txt");
        if(dFile.exists()){
            dFile.delete();
        }
        FileOutputStream out = new FileOutputStream(dFile);
        try{
            aes.encryptSection(in, out);
        }finally {
            in.close();
            out.flush();
            out.close();
        }

        in = new FileInputStream(dFile);
        File eFile = new File(DataUtils.USER_DIR + "/target/test/e_data.mp4");
//        File eFile = new File(DataUtils.USER_DIR + "/target/test/e_data.txt");
        if(eFile.exists()){
            eFile.delete();
        }
        out = new FileOutputStream(eFile);
        try{
            aes.decryptSection(in, out);
        }finally {
            in.close();
            out.flush();
            out.close();
        }
        SignVerify signatureCipher = SignVerify.build().loadConfig();

        byte[] sign = signatureCipher.sign(new FileInputStream(file));
        System.out.println("file sign:" + new String(DataUtils.base64Encode(sign)));
        System.out.println("file verify:" + signatureCipher.verify(new FileInputStream(file), sign));
        System.out.println("eFile verify:" + signatureCipher.verify(new FileInputStream(eFile), sign));


    }


    @After
    public void tearDown() throws Exception {

    }
}
