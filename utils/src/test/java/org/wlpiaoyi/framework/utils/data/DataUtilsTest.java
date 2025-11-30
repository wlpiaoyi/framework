package org.wlpiaoyi.framework.utils.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DataUtilsTest {

    @Before
    public void setUp() throws Exception {}


    @Test
    public void MD() throws IOException {
        System.out.println(new String(DataUtils.base64Encode(DataUtils.sha256("admin".getBytes(StandardCharsets.UTF_8)))));
    }

    @Test
    public void write1() throws IOException {

        int a = 1, b = 2, c = 3;

        String path = DataUtils.USER_DIR + "/test1/test1-1/data.txt";
        String text = "123456我";
        WriterUtils.overwrite(new File(path), text.getBytes(StandardCharsets.UTF_8));
        String readArg = ReaderUtils.loadString(path, StandardCharsets.UTF_8);
        System.out.println(readArg);
    }

    @Test
    public void write2() throws IOException {
        String path = DataUtils.USER_DIR + "/test1/test1-1/data.txt";
        WriterUtils.append(new File(path), "789阿达".getBytes(StandardCharsets.UTF_8));
    }
    @Test
    public void write3() throws IOException {
        String path = DataUtils.USER_DIR + "/test1/test1-1/data.txt";
        WriterUtils.overwrite(new File(path), "123456", StandardCharsets.UTF_8);
    }

    @Test
    public void write4() throws IOException {
        String path = DataUtils.USER_DIR + "/test1/test1-1/data.txt";
        WriterUtils.append(new File(path), "我的", StandardCharsets.UTF_8);
    }



    @Test
    public void write5() throws IOException {
        String readInText = DataUtils.readFile("C:\\Users\\wlpia\\Desktop\\1.txt");
        assert readInText != null;
        InputStream readIo = new ByteArrayInputStream(readInText.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream writOut = new ByteArrayOutputStream();
        DataUtils.zipData(readIo, writOut);
        byte[] outBytes = writOut.toByteArray();
        DataUtils.writeFile(new ByteArrayInputStream(outBytes), "C:\\Users\\wlpia\\Desktop\\1.zip");

        readIo = Files.newInputStream(new File("C:\\Users\\wlpia\\Desktop\\1.zip").toPath());
        writOut = new ByteArrayOutputStream();
        DataUtils.unZipData(readIo, writOut);
        System.out.println(readInText.equals(writOut.toString()));
    }


    /**
     * 测试Base64编解码功能
     */
    @Test
    public void testBase92EncodeDecode() {
        String testData = "Hello World! This is a test string for Base92 encoding.";
        byte[] originalBytes = testData.getBytes(StandardCharsets.UTF_8);

        // 测试byte[]编解码
        byte[] encodedBytes = DataUtils.base92Encode(originalBytes);
        System.out.println("byte[]编码解码结果：\n" + new String(encodedBytes, StandardCharsets.UTF_8));
        byte[] decodedBytes = DataUtils.base92Decode(encodedBytes);
        System.out.println("byte[]编解码结果：\n" + new String(decodedBytes, StandardCharsets.UTF_8));
        for (int i = 0; i < encodedBytes.length; i++) {
            encodedBytes[i] = (byte) 0xFF;
        }
        decodedBytes = DataUtils.base92Encode(encodedBytes);
        System.out.println("byte[]test结果：\n" + new String(decodedBytes, StandardCharsets.UTF_8));
        for (int i = 0; i < encodedBytes.length; i++) {
            encodedBytes[i] = (byte) 0x0;
        }
        encodedBytes[0] = (byte) 0x1;
        decodedBytes = DataUtils.base92Encode(encodedBytes);
        System.out.println("byte[]test结果：\n" + new String(decodedBytes, StandardCharsets.UTF_8));

    }


    @After
    public void tearDown() throws Exception {

    }
}
