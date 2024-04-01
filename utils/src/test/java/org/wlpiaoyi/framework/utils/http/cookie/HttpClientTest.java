package org.wlpiaoyi.framework.utils.http.cookie;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.BufferedHttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HttpClientTest {

    private String think_language = "zh-cn";
    private String PHPSESSID = "u3l75up4pdr836rdpml3bre7c4";
    private String uuid = "B3AFBA99-1DD9-4C15-ACBC-2989B744E000";
    private String token = "lDZq1/4ge6fGACF2AgX3n0j+SiZ7R6Jj09j85wC2YpEH5Vcpy0vuAef1a0QzHgUeilWr8GE0eoCZhauMNSjjD7dUX8k99QXzhgPGKJ6akXKs2Lq8YnVFRuQELaI8mj6/hVftmsOm6EZqFUz93j9IWr6z8jpKVzNcj8fHrOY65hD9stwhl5CzKEvRNIcSqq7d";

    @Before
    public void setUp() throws Exception {}

    @Test
    public void test() throws IOException, InterruptedException {
//        Response response = this.emartet();
//        System.out.println();
//        String url = "http://www.100csc.com/shop/goods/1604545423286/100000352.html";
        String url = "http://www.100csc.com/";

        while (true){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Response<String> response = HttpClient.instance(
                                    Request.initJson(url).setMethod(Request.Method.Get).setProxy("127.0.0.1", 8888)
                            )
                            .setRpClazz(String.class)
                            .response();
                    System.out.println("<======" + response.getStatusCode());
                }
            }).start();
            Thread.sleep(1000);
        }

    }



    public Response<String> emartet() {
        String url = "https://api.smsbao.com/sms?u=yes_boy&p=9d7fb24aa17944029d87d506b044c9c2&m=" +
                18228088049L
                + "&c=" + "【短信宝】您的验证码是3572,十分钟内有效";

        Response<String> response =  HttpClient.instance(
                Request.initJson(url).setMethod(Request.Method.Get)
        )
                .setRpClazz(String.class)
                .response();

        return response;
    }


    public Response<String> index(String classId) throws IOException {

        Response<String> response =  HttpClient.instance(
                Request.initJson("https://appapi.qukoudai.cn/index.php")
                        .setCookie("PHPSESSID", this.PHPSESSID)
                        .setCookie("think_language", this.think_language)
                        .setHeader("x-version-release", "1.5.1")
                        .setHeader("user-agent","1.5.1 rv:51 (iPhone; iOS 13.7; zh_CN)")
                        .setParam("mo", "v2")
                        .setParam("a", "getPurchaseList" )
                        .setParam("uuid", this.uuid)
                        .setParam("class_id", classId)
                        .setParam("pageSiz", "20")
                        .setParam("page", "1")
                        .setParam("token", this.token)
                        .setProxy("127.0.0.1", 8888)
        )
                .setRpClazz(String.class)
                .response();

        return response;
    }

    @After
    public void tearDown() throws Exception {

    }
}
