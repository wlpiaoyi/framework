package org.wlpiaoyi.framework.utils.websocket.client;


import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebSecketClientTest implements Runnable {

    private int count = 20;

    @Before
    public void setUp() throws Exception {}

    @Test
    public void test() throws IOException {

//        int count = this.count;
//        while (count -- > 0){
//            new Thread(this).start();
//        }
//        while (this.count > 0){
//            try{
//                Thread.sleep(10);
//            }catch (Exception e){}
//        }

    }

    @Override
    public void run() {

        WebSocketClient wsClient = new WebSocketClient("ws://127.0.0.1:8001/wlpiaoyi/test/sid01", null);
        if(!wsClient.syncOpen()){
            System.out.println("ws open faild");
            return;
        }
        int count = 0;
        String uuid = "null";
        try{
            Gson gson = new Gson();
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("type", "send-receive");
            dataMap.put("count", count ++);
            String message =  gson.toJson(dataMap);
            String result[] =  wsClient.sendSyncMessage(message);
            uuid = result[0];
            dataMap = gson.fromJson(result[1], Map.class);
            dataMap.put("type", "send-receive");
            dataMap.put("count", count ++);
            dataMap.put("uuid", result[0]);
            while (true){
                message =  gson.toJson(dataMap);
                message =  wsClient.sendSyncMessage(((String)dataMap.get("uuid")), message);
                dataMap = gson.fromJson(message, Map.class);
                dataMap.put("type", "send-receive");
                dataMap.put("count", count ++);
                Assert.assertFalse("org-uuid:" + uuid + " ret-uuid:" + ((String)dataMap.get("uuid")),
                        !((String)dataMap.get("uuid")).equals(uuid));
                try{
                    Thread.sleep(10);
                }catch (Exception e){}
                if(count > 4) break;

            }
            dataMap.put("type", "send-close");
            dataMap.put("count", count ++);
            message =  gson.toJson(dataMap);
            wsClient.sendAsyncMessage(uuid, message);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(this.count + "<============uuid:"+ uuid + " count:" + count);
        synchronized (WebSecketClientTest.class){
            this.count--;
        }
    }

    @After
    public void tearDown() throws Exception {

    }
}
