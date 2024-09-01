package org.wlpiaoyi.framework.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import lombok.NonNull;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p><b>{@code @description:}</b>  </p>
 * <p><b>{@code @date:}</b>         2024-04-17 11:05:30</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class MarkdownUtils {

    /**
     * <p><b>{@code @description:}</b> 
     * TODO
     * </p>
     * 
     * <p><b>@param</b> <b>mdInputStream</b>
     * {@link InputStream}
     * </p>
     * 
     * <p><b>@param</b> <b>htmlTile</b>
     * {@link String}
     * </p>
     * 
     * <p><b>@param</b> <b>encoding</b>
     * {@link Charset}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/4/17 11:33</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static String parseToHtml(@NonNull InputStream mdInputStream, String htmlTile, Charset encoding) throws IOException {
        if(encoding == null){
            encoding = StandardCharsets.UTF_8;
        }
        InputStream htmlIo = MarkdownUtils.class.getClassLoader().getResourceAsStream("markdown.base.html");
        assert htmlIo != null;
        String htmlContent = ReaderUtils.loadString(htmlIo, encoding);
        String htmlBody = parseToHtmlBody(mdInputStream, encoding);
        htmlContent = htmlContent.replace("${charSet}", encoding.name())
                .replace("${title}", htmlTile)
                .replace("${body}", htmlBody);
        return htmlContent;
    }


    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>mdInputStream</b>
     * {@link InputStream}
     * </p>
     *
     * <p><b>@param</b> <b>encoding</b>
     * {@link Charset}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/4/17 11:14</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static String parseToHtmlBody(InputStream mdInputStream, Charset encoding) throws IOException {
        if(encoding == null){
            encoding = StandardCharsets.UTF_8;
        }
        DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(true,
                Extensions.ALL
        );
        String mdContent = ReaderUtils.loadString(mdInputStream, encoding);
        Parser parser = Parser.builder(OPTIONS).build();
        HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();
        Node document = parser.parse(mdContent);
        return renderer.render(document);
    }
}
