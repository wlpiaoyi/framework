package org.wlpiaoyi.framework.utils.web.websocket.client;


import lombok.Data;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class WebSecketClientTest implements Runnable, WebSocketIntrface {

    private int count = 20;

    @Before
    public void setUp() throws Exception {
        new Thread(this).start();
    }

    @Test
    public void test() throws Exception {
        while (true){
            Thread.sleep(1000);
        }
    }


    @After
    public void tearDown() throws Exception {

    }

    @Override
    public void run() {
        try {
//            WebSocketClient wsClient = new WebSocketClient("ws://121.40.165.18:8800", this);

            WebSocketClient wsClient = new WebSocketClient("ws://192.168.1.86:9001/friendship/ws/message.do", this);
//            wsClient.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("172.20.170.61", 8010)));
//            WebSocketClient wsClient = new WebSocketClient("ws://127.0.0.1:8001/wlpiaoyi/test/111", this);
            wsClient.synConnect(5000, TimeUnit.MILLISECONDS);
//            wsClient.send(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocketClient webSocketClient, ServerHandshake serverHandshake) {
        System.out.println("onOpen");
    }
    @Data
    private static class WSData{
        private int code = 0;
        private String message;
    }

    private WSData wsData = new WSData();

    @Override
    public void onMessage(WebSocketClient webSocketClient, String message) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            webSocketClient.send("test:", wsData);
        }).start();
    }

    @Override
    public void onClose(WebSocketClient webSocketClient, int code, String message, boolean b) {
        System.out.println("onClose");
    }

    @Override
    public void onError(WebSocketClient webSocketClient, Exception e) {
        System.out.println("onClose");
    }
}
