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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class GsonBuilderTest {

    @Data
    static class TestObj{

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
        String json = GsonBuilder.gsonDefault().toJson(to);
        TestObj to2 = GsonBuilder.gsonDefault().fromJson(json, TestObj.class);
        System.out.println(json);
    }

    @After
    public void tearDown() throws Exception {

    }
}
