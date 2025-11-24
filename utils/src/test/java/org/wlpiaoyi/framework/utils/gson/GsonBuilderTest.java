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
import java.util.Map;

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

    @Test
    public void test2() throws IOException {
        String json = """
                {"requestId":"reqId-JH-DCSCREEN-BDS-ZLP-1-1","topic":"/device/JH-DCSCREEN/BDS-ZLP-1-1/message/event/dCScreen.faultEventReport","payload":{"headers":{"parentGatewayId":"test","productId":"JH-DCSCREEN","ignoreLog":true,"keepOnlineTimeoutSeconds":300,"initTopic":"/CDS-SD-YHZHSD-PQCS/test/dCScreen/event","creatorId":"55dbb88566e5916262b18c3dcd60f9ec","superDeviceType":"normalDevice","deviceCreateType":"fromProduct","_uid":"aZqQ1iGmFgxC9avyzaJVe-SzI5SQ98F7","deviceName":"JH-直流屏","productName":"JH-直流屏"},"messageId":"1990329658482581506_66330","deviceId":"BDS-ZLP-1-1","timestamp":1763366543776,"abilities":[{"data":{"FaultCode-0x0126":121212232,"FaultCode-0x0127":1232323232.3},"event":"faultEventReport","uri":{"nodes":[{"instanceName":"","nodeCode":"005","deviceId":"BDS-ZLP-1-1"}],"abilityCode":"dCScreen"}}],"event":"dCScreen.faultEventReport","data":{"FaultCode-0x0126":121212232,"FaultCode-0x0127":1232323232.3},"messageType":"EVENT"},"type":"result"}
                """;
        Map map = GsonBuilder.gsonDefault().fromJson(json, Map.class);
        log.info(json);
    }

    @After
    public void tearDown() throws Exception {

    }
}
