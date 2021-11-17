package org.wlpiaoyi.framework.utils.gson;

import com.google.gson.Gson;
import lombok.Data;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;
import org.wlpiaoyi.framework.utils.rsa.Rsa;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GsonBuilderTest {

    @Data
    static class TestObj{

        private String a1;
        private Date date;

        private LocalDateTime lDateTime;

        private LocalDate lDate;

        private LocalTime lTime;

    }

    @Before
    public void setUp() throws Exception {}

    @Test
    public void test() throws IOException {
        TestObj to = new TestObj();
        to.date = new Date();
        to.lDateTime = LocalDateTime.of(2021,01,01,12,01);
        to.lDate = LocalDate.of(2021,01,02);
        to.lTime = LocalTime.of(12,02);
        String json = "{\"a1\":\"[0xe4][0xba][0x91][0xe6][0xb5][0xae][0xe5][0xb8][0x82][0xe5][0xb7][0xa5][0xe5]" +
                "[0x95][0x86][0xe8][0xa1][0x8c][0xe6][0x94][0xbf][0xe7][0xae][0xa1][0xe7][0x90]" +
                "[0x86][0xe5][0xb1][0x80][0xe4][0xba][0x91][0xe5][0x9f][0x8e][0xe5][0x88][0x86][0xe5][0xb1][0x80]\"}";
        //GsonBuilder.gsonDefault().toJson(to);
        byte[] bs = new byte[4];
        bs[0] = (byte) 0xe4;
        bs[1] = (byte) 0xba;
        bs[3] = (byte) 0x91;
        bs[3] = (byte) 0xe6;
        new String(bs, 0, 4, "Unicode");
//        g(byte bytes[], int offset, int length, String charsetName)
        TestObj to2 = GsonBuilder.gsonDefault().fromJson(json, TestObj.class);
        Map map = GsonBuilder.gsonDefault().fromJson(json, Map.class);
        map.put("map", new HashMap(){{
            put("a1","[0xe4][0xba][0x91][0xe6][0xb5][0xae][0xe5][0xb8][0x82][0xe5][0xb7][0xa5][0xe5]" +
                    "[0x95][0x86][0xe8][0xa1][0x8c][0xe6][0x94][0xbf][0xe7][0xae][0xa1][0xe7][0x90]" +
                    "[0x86][0xe5][0xb1][0x80][0xe4][0xba][0x91][0xe5][0x9f][0x8e][0xe5][0x88][0x86][0xe5][0xb1][0x80]");
        }});
        json = GsonBuilder.gsonDefault().toJson(map);
        System.out.println(json);
    }

    @After
    public void tearDown() throws Exception {

    }
}
