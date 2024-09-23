package org.wlpiaoyi.framework.utils.http;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicHeader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
            this.textHttp5Post();
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
        Request<Map> request = new Request<>(context, "http://127.0.0.1:8180/file_info/list", Request.Method.Post);
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
    public void textHttp5Post() throws IOException {
        BasicClassicHttpRequest request = new HttpPost("http://36.138.30.52:1888/admin-api/system/captcha/get");
        String bodyArg = "{\n" +
                "\t\"captchaType\": \"blockPuzzle\",\n" +
                "\t\"clientUid\": \"slider-2eec2994-ed58-4966-baaa-ed1d7289ed4c\",\n" +
                "\t\"ts\": 1711940612038\n" +
                "}";
        request.addHeader(new BasicHeader("cookie", "Hm_lvt_e8002ef3d9e0d8274b5b74cc4a027d08=1710397580; " +
                "SECKEY_ABVK=xFd9ju0MmGghzeLk3tWNTsZjIXDC6xZeCT0K50hFwwU%3D; " +
                "BMAP_SECKEY=_DaWencWHGPyGjALIKQWWzrX0liOFE-_yoCACxWMb0uarxFTp14bVxu8XIovrCzsTvQZfdKvIF9WRMooBiHpL9-iF-JM4Z6ZQTxOg0NVmIAKKjEqt-QCrKriCoZcUlCHAe89SMQXQe9RD04hkdDuCvlYNsZwKunhi8ww84jf4pTErWNZRuEp0iA6a3E5W6V1; " +
                "token=c0e3a5fb39644bb2b7665df8a6069e3a; " +
                "Hm_lvt_fadc1bd5db1a1d6f581df60a1807f8ab=1711531073,1711596270,1711679148,1711940612; " +
                "Hm_lpvt_fadc1bd5db1a1d6f581df60a1807f8ab=1711940612"));
        request.setHeader(new BasicHeader("Connection", "keep-alive"));
        InputStream bodyIo = new ByteArrayInputStream(bodyArg.getBytes(StandardCharsets.UTF_8));
        request.setEntity(new InputStreamEntity(bodyIo, ContentType.APPLICATION_JSON));
        try (CloseableHttpClient httpclient = HttpClients.custom()
//                .setProxy(new HttpHost("127.0.0.1", 8888))
                .build()) {
            httpclient.execute(request, (HttpClientResponseHandler<String>) response -> {
                // 获取状态码
                System.out.println(response.getVersion()); // HTTP/1.1
                System.out.println(response.getCode()); // 200
                System.out.println(response.getReasonPhrase()); // OK
                System.out.println(Arrays.toString(response.getHeaders())); // OK
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
