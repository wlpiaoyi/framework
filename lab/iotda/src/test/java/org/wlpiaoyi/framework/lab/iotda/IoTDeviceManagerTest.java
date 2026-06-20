package org.wlpiaoyi.framework.lab.iotda;

import com.huaweicloud.sdk.iot.device.IoTDevice;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.data.DataUtils;

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

    private static final String TEST_CA_PATH = "C:\\Home\\Document\\Develop\\Java\\framework\\lab\\iotda\\src\\main\\resources\\mqtts_ca_cert.jks";

    @Before
    public void setUp() {
        IoTDeviceManager.setIotServerUri(null);
        IoTDeviceManager.setIotRootCAPath(null);
    }

    @After
    public void tearDown() {
        IoTDeviceManager.setIotServerUri(null);
        IoTDeviceManager.setIotRootCAPath(null);
    }

    @Test
    public void getAndOnlineDevice() {
        IoTDeviceManager.init(TEST_SERVER_URI, TEST_CA_PATH);
        assertNull(IoTDeviceManager.getAndOnlineDevice("6a0fb591a183716c0a330c71_sl02", TEST_DEVICE_SECRET));
        assertNull(IoTDeviceManager.getAndOnlineDevice("6a0fb585201db91d32326cb2_sl01", TEST_DEVICE_SECRET));
        assertEquals(2, IoTDeviceManager.onlineDeviceCount());
    }

}
