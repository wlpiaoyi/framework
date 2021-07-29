package org.wlpiaoyi.framework.utils.http.cookie;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.IOException;
import java.util.Map;

public class HttpClientTest {

    @Before
    public void setUp() throws Exception {}

    @Test
    public void register() throws IOException {
        Response<String > response =  HttpClient.instance(
                Request.initForm("http://127.0.0.1:9081/xuefujy/sys/device/register")
                        .setProxy("127.0.0.1", 8888)
                        .setHeader("device","7")
                )
                        .setRpClazz(Map.class)
                        .response();
        System.out.println("");
    }


    @After
    public void tearDown() throws Exception {

    }
}
