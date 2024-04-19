package org.wlpiaoyi.framework.utils.web.websocket.service;


import com.google.gson.Gson;
import jakarta.websocket.Session;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.exception.CatchException;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 异步WebSocket服务
 */
//@ServerEndpoint("/myWs")
//@Component
public class WsBootService {

    // 用来记录当前在线连接数。应该把它设计成线程安全的。
    //======================================>
    private static Object LOCK_ONLINE_COUNT = new Object();
    private static int ONLINE_COUNT = 0;
    //<======================================

    //concurrent包的线程安全Set,用来存放每个客户端对应的WebSocketServer对象
    private static CopyOnWriteArraySet<WsBootService> SERVICE_SET = new CopyOnWriteArraySet<>();

    protected final Gson gson = GsonBuilder.gsonDefault();;


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
    public void sendMessage(@Nullable String headSuffix, @NonNull Object data) throws IOException {
        if(ValueUtils.isBlank(headSuffix)) {
            this.sendMessage(this.gson.toJson(data));
        } else {
            this.sendMessage(headSuffix + this.gson.toJson(data));
        }
    }

    /**
     * 实现服务器异步线程安全主动推送
     * @param data
     * @throws IOException
     */
    public void sendMessage(@NonNull Object data) throws IOException {
        this.sendMessage(this.gson.toJson(data));
    }

    public void close() throws IOException {
        this.session.close();
    }

    /**
     * 建立连接回调
     * @param session
     */
//    @OnOpen
    protected final void onWsOpen(Session session){
        this.session = session;
        WsBootService.SERVICE_SET.add(this);
        addOnlineCount();
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message
     */
//    @OnMessage
    protected final <T> T onWsMessage(String headSuffix, String message, Class<T> clazz) throws CatchException {
        if(!ValueUtils.isBlank(headSuffix)){
            if(!message.startsWith(headSuffix)) {
                throw new CatchException("message had not start with \"" + headSuffix + "\"");
            }
            message = message.substring(headSuffix.length());
        }
        T obj = gson.fromJson(message, clazz);
        return obj;
    }

    protected final <T> T onWsMessage(String message, Class<T> clazz){
        T obj = null;
        try {
            obj = this.onWsMessage(null, message, clazz);
        } catch (CatchException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 关闭连接回调
     */
//    @OnClose
    protected void onWsClose() {
        WsBootService.SERVICE_SET.remove(this);  //从set中删除
        subOnlineCount();//在线数减1
    }

    /**
     * 异常连接回调
     * @param session
     * @param error
     */
//    @OnError
    protected void onWsError(Session session, Throwable error) {
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
