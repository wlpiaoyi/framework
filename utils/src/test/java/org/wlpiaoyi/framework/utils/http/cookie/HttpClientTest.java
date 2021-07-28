package org.wlpiaoyi.framework.utils.http.cookie;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.IOException;

public class HttpClientTest {

    @Before
    public void setUp() throws Exception {}

    @Test
    public void test() throws IOException {
        Response<String > response =  HttpClient.instance(
                Request.initForm("https://www.baidu.com/qq").setProxy("127.0.0.1", 8888)
                        .addCookie(new BasicClientCookie("a1","v1")))
                .setRpClazz(String.class)
                .response();
        System.out.println("");
    }

    @After
    public void tearDown() throws Exception {

    }
}
