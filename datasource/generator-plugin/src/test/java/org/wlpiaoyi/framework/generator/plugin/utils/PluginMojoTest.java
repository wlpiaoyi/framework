package org.wlpiaoyi.framework.generator.plugin.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.generator.plugin.PluginMojo;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class PluginMojoTest {

    @Before
    public void setUp() throws Exception {}

    @Test
    public void test() throws MojoExecutionException, MojoFailureException {
        PluginMojo mogo = new PluginMojo(
                "\\src\\main\\resources\\generator.config.properties",
                "\\src\\main\\resources\\generator.plug.json",
                "\\src\\main\\resources\\template",
                DataUtils.USER_DIR);

        mogo.execute();
        log("测试结束");
    }

    @After
    public void tearDown() throws Exception {}

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private void log(String message) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [CommentEnumParseTest] " + message);
    }
}
