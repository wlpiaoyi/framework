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
    public void test() throws IOException {
        Response response = this.index();
        System.out.println();
    }


    public Response<String> index() throws IOException {

        Response<String> response =  HttpClient.instance(
                Request.initJson("https://appapi.qukoudai.cn/index.php")
                        .setParams("mo", "v2")
                        .setParams("getPurchaseList", "getPurchaseList" )
                        .setParams("uuid", "B3AFBA99-1DD9-4C15-ACBC-2989B744E000")
                        .setParams("class_id", "320")
                        .setParams("pageSiz", "20")
                        .setParams("page", "1")
                        .setParams("token", "lDZq1/4ge6dGollt+OIob9Ku9f1/bzAbwCH1pZV0tE+nGEJ3ROV27bEDX2e9/v5EZ49UzpJRrC6X9xukgAjjvIzZWYEDAeEr/ymLZ1IV5iSvvy8jmmaAmBJ2o0Qf4D0cGWXAIOXHgB6w4exdHLihHGgqwOUSUE/AQ4u5c+1NWqbAaMHcD/B6f7dt5Tmg2eDy")
                        .setProxy("127.0.0.1", 8888)
                        .setHeader("device","7")
        )
                .setRpClazz(String.class)
                .response();

        return response;
    }


    @After
    public void tearDown() throws Exception {

    }
}
