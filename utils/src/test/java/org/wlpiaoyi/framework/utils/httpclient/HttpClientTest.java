package org.wlpiaoyi.framework.utils.httpclient;

import lombok.Data;
//import org.apache.http.HttpHost;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

//import org.apache.http.client.fluent.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
            Date date = new Date();
            String dateTimeArgs = DateUtils.formatLocalDateTime(DateUtils.parsetoLocalDateTime(date));
            String[] zoneIds = ZoneId.getAvailableZoneIds().toArray(new String[]{});
            LocalDateTime dt = DateUtils.parsetoLocalDateTime(dateTimeArgs, DateUtils.YYYYMMDDHHMMSS, ZoneId.of(zoneIds[0]));
            String text = HttpGetClient.String("http://sms.webchinese.cn/web_api/SMS", null, new HashMap(){{
                put("Action","UP");
                put("Uid","wlpiaoyi");
                put("Key","d41d8cd98f00b204e980");
                put("Prompt","0");
            }});
            System.out.println(text);
            HttpHost proxy = new HttpHost("192.168.1.85", 8888);
            text = HttpGetClient.String("http://utf8.api.smschinese.cn", null, new HashMap(){{
                put("Uid","wlpiaoyi");
                put("Key","d41d8cd98f00b204e980");
                put("smsMob","18382471299");
                put("smsText","【学府嘉苑业委会】邻居朋友你好:请问你同意小区绿化的改造吗? 同意回复1 不同意回复2");
            }}, proxy);
            System.out.println(text);
            Map params = new HashMap(){{put("intVar",1);}};
            text = HttpPostClient.StringJsonParams("https://127.0.0.1:8002/wlpiaoyi/postData-json", null, params, new HttpHost("127.0.0.1", 9000));
            System.out.println(text);

            text = HttpPostClient.StringJsonParams("https://127.0.0.1:8002/addShop", null, params, new HttpHost("127.0.0.1", 9000));
            System.out.println(text);


            text = HttpPostClient.StringFormParams("https://127.0.0.1:8002/wlpiaoyi/postData-form", null, params, new HttpHost("127.0.0.1", 9000));
            System.out.println(text);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {

    }

}
