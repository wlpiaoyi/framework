package org.wlpiaoyi.framework.utils.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.MarkdownUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * <p><b>{@code @description:}</b>  </p>
 * <p><b>{@code @date:}</b>         2024-04-17 10:17:33</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
@Slf4j
public class MarkdownUtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("Demo.md");
        assert stream != null;
        System.out.println(MarkdownUtils.parseToHtml(stream,"测试", StandardCharsets.UTF_8));
    }

    @After
    public void tearDown() throws Exception {

    }
}
