package org.wlpiaoyi.framework.utils.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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



//    @Test
//    public void write5() throws IOException {
//        String path = DataUtils.USER_DIR + "/test1/test1-1/data_plus.txt";
//        System.out.println(DataUtils.MD5PLUS(new File(path)));;
//    }

    @After
    public void tearDown() throws Exception {

    }
}
