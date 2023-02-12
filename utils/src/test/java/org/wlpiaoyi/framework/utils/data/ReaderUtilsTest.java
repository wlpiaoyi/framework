package org.wlpiaoyi.framework.utils.data;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/11 20:34
 * {@code @version:}:       1.0
 */
@Slf4j
public class ReaderUtilsTest {

    @Before
    public void setUp() throws Exception {}


    @Test
    public void loadString() throws IOException {
        String path = DataUtils.USER_DIR + "/target/test/reader.txt";
        File file = new File(path);
        @Cleanup FileInputStream fileIO = new FileInputStream(file);
        System.out.println(ReaderUtils.loadString(fileIO, null));
    }

    @After
    public void tearDown() throws Exception {}
}
