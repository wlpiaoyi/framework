package org.wlpiaoyi.framework.utils.httpclient;

import lombok.Data;
//import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//import org.apache.http.client.fluent.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;


public class HttpClientTest {

    @Data
    public static class JsonBodyData{
        private int intVar;
        private String stringVar;
    }

    @Before
    public void setUp() throws Exception {}

    @Test
    public void test() throws IOException {

        try {
            HttpResponse response = HttpClient.GETResponse("https://www.baidu.com", null, null);
            String text = HttpClient.getResponseText(response);
            text = HttpClient.GETData("https://127.0.0.1:8002/wlpiaoyi/getData", null, new HashMap(){{put("var",1);}}, String.class);
            System.out.println(text);
            text = HttpClient.POSTData("https://127.0.0.1:8002/wlpiaoyi/postData-form", null, new HashMap(){{put("intVar",1);}}, String.class);
            System.out.println(text);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {

    }

}
