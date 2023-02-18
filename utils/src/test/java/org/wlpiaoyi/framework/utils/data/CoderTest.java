package org.wlpiaoyi.framework.utils.data;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Coder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/11 20:51
 * {@code @version:}:       1.0
 */
public class CoderTest {

    @Before
    public void setUp() throws Exception {}


    @Test
    public void encryptMD5() throws Exception {
        String path = DataUtils.USER_DIR + "/target/test/reader.txt";
        File file = new File(path);
        FileInputStream fileIO = new FileInputStream(file);
        String md5Value = new BigInteger(1, Coder.encryptMD5(fileIO)).toString(16);
        System.out.println(md5Value);
        fileIO.close();
        fileIO = new FileInputStream(file);
        String readerStr = ReaderUtils.loadString(fileIO, StandardCharsets.UTF_8);
        fileIO.close();
        md5Value = new BigInteger(1, Coder.encryptMD5(readerStr.getBytes(StandardCharsets.UTF_8))).toString(16);
        System.out.println(md5Value);
    }

    @Test
    public void encryptSHA() throws Exception {
        String path = DataUtils.USER_DIR + "/target/test/reader.txt";
        File file = new File(path);
        FileInputStream fileIO = new FileInputStream(file);
        String md5Value = new BigInteger(1, Coder.encryptSHA(fileIO)).toString(16);
        System.out.println(md5Value);
        fileIO.close();
        fileIO = new FileInputStream(file);
        String readerStr = ReaderUtils.loadString(fileIO, StandardCharsets.UTF_8);
        fileIO.close();
        md5Value = new BigInteger(1, Coder.encryptSHA(readerStr.getBytes(StandardCharsets.UTF_8))).toString(16);
        System.out.println(md5Value);
    }

    @After
    public void tearDown() throws Exception {}
}
