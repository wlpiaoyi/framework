package org.wlpiaoyi.framework.utils.http.cookie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.IOException;

public class HttpClientTest {

    private String think_language = "zh-cn";
    private String PHPSESSID = "u3l75up4pdr836rdpml3bre7c4";
    private String uuid = "B3AFBA99-1DD9-4C15-ACBC-2989B744E000";
    private String token = "lDZq1/4ge6fGACF2AgX3n0j+SiZ7R6Jj09j85wC2YpEH5Vcpy0vuAef1a0QzHgUeilWr8GE0eoCZhauMNSjjD7dUX8k99QXzhgPGKJ6akXKs2Lq8YnVFRuQELaI8mj6/hVftmsOm6EZqFUz93j9IWr6z8jpKVzNcj8fHrOY65hD9stwhl5CzKEvRNIcSqq7d";

    @Before
    public void setUp() throws Exception {}

    @Test
    public void test() throws IOException {
        Response response = this.index("322");
        System.out.println();
    }




    public Response<String> emartet() throws IOException {

        Response<String> response =  HttpClient.instance(
                Request.initJson("http://127.0.0.1:9081/e-market/sys/device/register")
                        .setHeader("platform", "1")
                        .setHeader("deviceNo","yue98278sjey28")
                        .setHeader("version","1.0.1")
                        .setProxy("127.0.0.1", 8888)
                        .setMethod(Request.Method.Get)
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
