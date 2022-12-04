package org.wlpiaoyi.framework.mybatis.proxy.socket;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.mybatis.utils.data.DataUtils;

import java.util.List;
import java.util.Map;

public class SocketProxyTest {

    private SocketProxy socketProxy;
    private List<Map> configurations;

    @Before
    public void setUp() throws Exception {
        String PATH = System.getProperty("user.dir") + "/proxy.json";
        String jsonStr = DataUtils.readFile(PATH);
        this.configurations = new Gson().fromJson(jsonStr, List.class);
    }

    @Test
    public void test() throws Exception {
        byte[][] encryptionDatas = new byte[2][];
        for(Map configuration : configurations){
            int port = ((Double)configuration.get("port")).intValue();
            boolean verify = configuration.containsKey("verify") ? (boolean)configuration.get("verify") : false;
            SocketProxy socketProxy;
            if(verify){
                String name = (String) configuration.get("name");
                String password = (String) configuration.get("password");
                encryptionDatas[0] = name.getBytes("UTF-8");
                encryptionDatas[1] = password.getBytes("UTF-8");
                socketProxy = new SocketProxy(port, encryptionDatas);
            }else {
                socketProxy = new SocketProxy(port);
            }
            socketProxy.asynStart();
        }
    }


    @After
    public void tearDown() throws Exception {
        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
