package org.wlpiaoyi.framework.utils.websocket.service;


import com.google.gson.Gson;
import lombok.Getter;
import lombok.NonNull;

import javax.websocket.*;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 异步WebSocket服务
 */
public class WsBootService {

    //超时时间
    public static int TIME_OUT_SECONDS = 5000;

    // 用来记录当前在线连接数。应该把它设计成线程安全的。
    //======================================>
    private static Object LOCK_ONLINE_COUNT = new Object();
    private static int ONLINE_COUNT = 0;
    //<======================================

    //concurrent包的线程安全Set,用来存放每个客户端对应的WebSocketServer对象
    private static CopyOnWriteArraySet<WsBootService> SERVICE_SET = new CopyOnWriteArraySet<WsBootService>();

    private Map<String, Map<String, Object>> resultMap = new HashMap<>();

    private Object lockResult = new Object();

    @Getter
    private Session session;

    /**
     * 实现服务器异步线程安全主动推送
     * @param message
     * @throws IOException
     */
    public void sendMessage(@NonNull String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
    /**
     * 实现服务器异步线程安全主动推送
     * @param data
     * @throws IOException
     */
    public void sendMessage(@NonNull Object data) throws IOException {
        this.sendMessage(new Gson().toJson(data));
    }

    public void close() throws IOException {
        this.session.close();
    }

    /**
     * 建立连接回调
     * @param session
     */
//    @OnOpen
    public final void onWsOpen(Session session){
        this.session = session;
        WsBootService.SERVICE_SET.add(this);
        addOnlineCount();
    }

//    /**
//     * 收到客户端消息后调用的方法
//     * @param message
//     */
//    @OnMessage
//    public final void onWsMessage(String message) {
//        WsUtile.onMessage(message, this,this);
//    }

    /**
     * 关闭连接回调
     */
//    @OnClose
    public final void onWsClose() {
        WsBootService.SERVICE_SET.remove(this);  //从set中删除
        subOnlineCount();//在线数减1
    }

    /**
     * 异常连接回调
     * @param session
     * @param error
     */
//    @OnError
    public final void onWsError(Session session, Throwable error) {
        if(session.isOpen()){
            try{session.close();} catch (Exception e){e.printStackTrace();}
        }

    }


//    @Contract(pure = true)
    public static int getOnlineCount() {
        synchronized (LOCK_ONLINE_COUNT){
            return WsBootService.ONLINE_COUNT;
        }
    }

    public static void addOnlineCount() {
        synchronized (LOCK_ONLINE_COUNT){
            WsBootService.ONLINE_COUNT++;
        }
    }

    public static void subOnlineCount() {
        synchronized (LOCK_ONLINE_COUNT){
            WsBootService.ONLINE_COUNT--;
        }
    }
}
