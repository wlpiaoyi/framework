package org.wlpiaoyi.framework.lab.iotda;

import com.huaweicloud.sdk.iot.device.IoTDevice;
import com.huaweicloud.sdk.iot.device.client.requests.ServiceProperty;
import com.huaweicloud.sdk.iot.device.transport.ActionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link IoTDeviceManager} 单元测试与可选集成测试。
 */
public class IoTDeviceManagerTest {

    private static final String TEST_SERVER_URI = "ssl://124.71.200.230:8883";
    private static final String TEST_DEVICE_SECRET = "00000000";
    private static final String TEST_CA_PATH =
            "C:\\Home\\Document\\Develop\\Java\\framework\\lab\\iotda\\src\\main\\resources\\mqtts_ca_cert.230.jks";
//    private static final String TEST_SERVER_URI = "ssl://10.17.99.247:8883";
//    private static final String TEST_DEVICE_SECRET = "icss-xa-iot-123";
//    private static final String TEST_CA_PATH =
//            "C:\\Home\\Document\\Develop\\Java\\framework\\lab\\iotda\\src\\main\\resources\\mqtts_ca_cert.247.jks";

    private IoTDeviceManager deviceManager;

    @Before
    public void setUp() {
        deviceManager = IoTDeviceManager.getInstance(TEST_SERVER_URI, TEST_CA_PATH);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getAndOnlineDevice230() {
        var device = this.deviceManager.getAndOnlineDevice("CS9570", TEST_DEVICE_SECRET);

        for (int i = 5; i < 1000; i++){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.deviceManager.report().service("data",
                    Map.of(
                            "p_int01", i,
                            "p_str01", DateUtils.formatLocalDateTime(LocalDateTime.now())
                    )
            ).report(device);
        }
        System.out.println();
//        assertNull(IoTDeviceManager.getAndOnlineDevice("6a0fb585201db91d32326cb2_sl01", TEST_DEVICE_SECRET));
//        assertEquals(2, IoTDeviceManager.onlineDeviceCount());
    }
    @Test
    public void getAndOnlineDevice() {
        var device = this.deviceManager.getAndOnlineDevice("test_001_001", TEST_DEVICE_SECRET);

        for (int i = 5; i < 1000; i++){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.deviceManager.report().service("data",
                    Map.of(
                            "p_int01", i,
                            "p_str01", DateUtils.formatLocalDateTime(LocalDateTime.now())
                    )
            ).report(device);
        }
        System.out.println();
//        assertNull(IoTDeviceManager.getAndOnlineDevice("6a0fb585201db91d32326cb2_sl01", TEST_DEVICE_SECRET));
//        assertEquals(2, IoTDeviceManager.onlineDeviceCount());
    }

}
