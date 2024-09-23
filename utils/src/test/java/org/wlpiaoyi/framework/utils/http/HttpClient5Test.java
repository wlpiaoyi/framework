package org.wlpiaoyi.framework.utils.http;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.http.factory.CookieFactory;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>{@code @description:}</b>  </p>
 * <p><b>{@code @date:}</b>         2024-04-01 11:16:19</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class HttpClient5Test {

    @Before
    public void setUp() throws Exception {}


    @Test
    public void textHttpWhel() throws IOException, InterruptedException {
        int count = 100;
        while (count -- > 0){
            this.textHttp5Get();
            this.textHttp5GetProxy();
//            this.textHttp5Post();
            Thread.sleep(100);
        }
    }
    @Test
    public void textHttp5Get() throws IOException {
        HttpClientContext context = HttpClientContext.create();
        Request<String> request = new Request<String>(context, "https://www.baidu.com", null, Request.Method.Get);
        Response<String> response = request.execute(String.class);
        System.out.println();
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        BasicClientCookie cookie = new BasicClientCookie("custom_cookie", "test_value");
////        cookie.setDomain("www.baidu.com");
//        cookie.setAttribute("domain", "true");
//        cookie.setPath("/");
//        cookieStore.addCookie(cookie);
//        context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
//        BasicClassicHttpRequest request = new HttpGet("https://www.baidu.com");
//        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
//            httpclient.execute(request, context, (HttpClientResponseHandler<String>) response -> {
//
//                // 获取状态码
//                System.out.println(response.getVersion()); // HTTP/1.1
//                System.out.println(response.getCode()); // 200
//                System.out.println(response.getReasonPhrase()); // OK
//                HttpEntity entity = response.getEntity();
//                System.out.println(ReaderUtils.loadString(entity.getContent(), StandardCharsets.UTF_8));
//                return null;
//            });
//        }
    }

    @Test
    public void textHttp5GetProxy() throws IOException {
        HttpClientContext context = HttpClientContext.create();
        CookieStore cookieStore = CookieFactory.loadCookieStore(context);
        Request<Map> request = new Request<>(context, "http://127.0.0.1:8180/file_info/list?p1=v1&p2=v2", Request.Method.Post);
        CookieFactory.setCookie(cookieStore, "n1", "v1", request.getDomain(), "/");
        CookieFactory.setCookie(cookieStore, "n2", "v2", 30, request.getDomain(), "/");
        request.setBody(new HashMap(){{
            put("current", "0");
            put("size", "3");
        }}).setHttpProxy("127.0.0.1", 8888);
        Response<Map> response = request.execute(Map.class);
        System.out.println();
//        BasicClassicHttpRequest request = new HttpGet("https://www.baidu.com");
//        try (CloseableHttpClient httpclient = HttpClients.custom()
//                .setProxy(new HttpHost("127.0.0.1", 8888))
//                .build()) {
//            httpclient.execute(request, (HttpClientResponseHandler<String>) response -> {
//                // 获取状态码
//                System.out.println(response.getVersion()); // HTTP/1.1
//                System.out.println(response.getCode()); // 200
//                System.out.println(response.getReasonPhrase()); // OK
//                HttpEntity entity = response.getEntity();
//                System.out.println(ReaderUtils.loadString(entity.getContent(), StandardCharsets.UTF_8));
//                return null;
//            });
//        }
    }


    @Test
    public void textHttp5Upload() throws IOException {
        HttpPost request = new HttpPost("http://127.0.0.1:8180/file/upload");
        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setProxy(new HttpHost("127.0.0.1", 8888))
                .build()) {
            File file = new File("C:\\Users\\wlpiaoyi\\OneDrive\\桌面\\dist.zip");
            MultipartEntityBuilder builder  = MultipartEntityBuilder.create();
            builder.addTextBody("k1", "v1");
            // 文件名
            builder.addBinaryBody("filename", file.getName().getBytes(StandardCharsets.UTF_8));
            // 文件
            builder.addPart("file", new FileBody(file));
            request.setEntity(builder.build());
            //https://cloud.tencent.com/developer/ask/sof/103466581 进度条
            httpclient.execute(request, (HttpClientResponseHandler<String>) response -> {
                // 获取状态码
                System.out.println(response.getVersion()); // HTTP/1.1
                System.out.println(response.getCode()); // 200
                System.out.println(response.getReasonPhrase()); // OK
                HttpEntity entity = response.getEntity();
                System.out.println(ReaderUtils.loadString(entity.getContent(), StandardCharsets.UTF_8));
                return null;
            });
        }
    }

    @After
    public void tearDown() throws Exception {

    }
}
