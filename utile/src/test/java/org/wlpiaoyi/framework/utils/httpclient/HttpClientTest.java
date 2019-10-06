package org.wlpiaoyi.framework.utils.httpclient;

import lombok.Data;
//import org.apache.http.HttpHost;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//import org.apache.http.client.fluent.*;

import java.io.IOException;


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

//        System.out.println("To enable your free eval account and get "
//                +"CUSTOMER, YOURZONE and YOURPASS, please contact "
//                +"sales@luminati.io");
//        HttpHost proxy = new HttpHost("zproxy.lum-superproxy.io", 22225);
//        String res = Executor.newInstance()
//                .auth(proxy, "lum-customer-hl_f4b143d9-zone-static", "diprow4ep468")
//                .execute(Request.Get("http://lumtest.com/myip.json").viaProxy(proxy))
//                .returnContent().asString();
//        System.out.println(res);
//        boolean flag1 = DateTimeUtils.verifyMEID("35 311400 809636 6");
//        boolean flag2 = DateTimeUtils.verifyMEID("35 209107 674249 6");
//        DateTimeUtils.getRandomMEID();
//        Response response =  HttpClient.syncGETResponse(null, "http://127.0.0.1:8001/wlpiaoyi/test/getData?intVar=20&stringVar=get-success");
//        System.out.println(HttpClient.getResponseText(response));
//        response =  HttpClient.syncGETResponse(null, "http://127.0.0.1:8001/wlpiaoyi/test/getData?intVar=-20&stringVar=get-exception");
//        System.out.println(HttpClient.getResponseText(response));
////
//
//        response =  HttpClient.syncPOSTFormResponse(null, "http://127.0.0.1:8001/wlpiaoyi/test/postData-form", new HashMap<String, String>(){{
//            put("intVar", "20");
//            put("stringVar", "post-form-success");
//        }});
//        System.out.println(HttpClient.getResponseText(response));
//
//        JsonBodyData jsonData = new JsonBodyData();
//        jsonData.setIntVar(20);
//        jsonData.setStringVar("post-json-success");
//        response =  HttpClient.syncPOSTJsonResponse(null, "http://127.0.0.1:8001/wlpiaoyi/test/postData-json", jsonData);
//        System.out.println(HttpClient.getResponseText(response));
    }

    @After
    public void tearDown() throws Exception {

    }

}
