package org.wlpiaoyi.framework.utils.gson;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Slf4j
public class GsonBuilderTest {

    @Data
    static class TestObj{

        private Long id = 22364643896987648L;
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
        String json =  GsonBuilder.gsonDefault().toJson(to);
        log.info(json);
    }

    @After
    public void tearDown() throws Exception {

    }
}
