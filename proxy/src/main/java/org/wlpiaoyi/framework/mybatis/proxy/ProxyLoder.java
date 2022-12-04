package org.wlpiaoyi.framework.mybatis.proxy;

import com.google.gson.Gson;
import org.wlpiaoyi.framework.mybatis.proxy.socket.SocketProxy;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.util.List;
import java.util.Map;

public class ProxyLoder {

    private static List<Map> configurations;

    public static void main(String[] args) throws Exception {

        String PATH = System.getProperty("user.dir") + "/proxy.json";
        String jsonStr = DataUtils.readFile(PATH);
        ProxyLoder.configurations = new Gson().fromJson(jsonStr, List.class);

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

            String proxy = configuration.containsKey("proxy") ? (String)configuration.get("proxy") : null;
            if(!StringUtils.isBlank(proxy)){
                String[] proxyArr = proxy.split(":");
                socketProxy.setProxy(proxyArr[0], new Integer(proxyArr[1]).intValue());
            }
            socketProxy.asynStart();
        }

        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
