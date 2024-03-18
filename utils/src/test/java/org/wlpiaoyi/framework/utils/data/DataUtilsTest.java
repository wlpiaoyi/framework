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

    @After
    public void tearDown() throws Exception {

    }
}
