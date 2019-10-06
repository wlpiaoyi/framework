package org.wlpiaoyi.framework.utils.websocket.service;


import lombok.Getter;
import lombok.NonNull;
import org.wlpiaoyi.framework.utils.websocket.WebSocketListener;
import org.wlpiaoyi.framework.utils.websocket.WsUtile;

import javax.websocket.*;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 异步WebSocket服务
 */
public class WsBootService implements WsUtile.SendASyncMessageLitsener {

    //超时时间
    public static int TIME_OUT_SECONDS = 5000;

    // 用来记录当前在线连接数。应该把它设计成线程安全的。
    //======================================>
    private static Object LOCK_ONLINE_COUNT = new Object();
    private static int ONLINE_COUNT = 0;
    //<======================================

    //concurrent包的线程安全Set,用来存放每个客户端对应的WebSocketServer对象
    private static CopyOnWriteArraySet<WsBootService> SERVICE_SET = new CopyOnWriteArraySet<WsBootService>();

    private static WeakReference<WebSocketListener> wsListener;

    private Map<String, Map<String, Object>> resultMap = new HashMap<>();

    private Object lockResult = new Object();

    @Getter
    private Session session;

    /**
     * 实现服务器同步线程安全主动推送
     * @param message
     * @return index[]: 0,uuid 1,message
     */
    public final String[] sendSyncMessage(@NonNull String message){
        String uuid = WsUtile.getUUID64();
        return new String[]{uuid, this.sendSyncMessage(message, uuid)};
    }

    /**
     * 实现服务器同步线程安全主动推送
     * @param uuid
     * @param message
     * @return
     */
    public String sendSyncMessage(@NonNull String uuid, @NonNull String message){
        return WsUtile.ssmlSendSyncMessage(uuid, message, this);
    }


    /**
     * 实现服务器异步线程安全主动推送
     * @param uuid
     * @param message
     * @return
     * @throws IOException
     */
    public void sendASyncMessage(@NonNull String uuid, @NonNull String message) throws IOException {
        String sendArg = uuid + ":" + message;
        this.sendASyncMessage(sendArg);
    }

    /**
     * 实现服务器异步线程安全主动推送
     * @param message
     * @throws IOException
     */
    public void sendASyncMessage(@NonNull String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
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
        if(WsBootService.wsListener != null){
            try{
                WsBootService.wsListener.get().onOpen(this);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message
     */
//    @OnMessage
    public final void onWsMessage(String message) {
        WsUtile.onMessage(message, this,this);
    }

    /**
     * 关闭连接回调
     */
//    @OnClose
    public final void onWsClose() {
        WsBootService.SERVICE_SET.remove(this);  //从set中删除
        subOnlineCount();//在线数减1
        if(WsBootService.wsListener != null){
            try{
                WsBootService.wsListener.get().onClose(this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
        if(WsBootService.wsListener != null){
            try{
                WsBootService.wsListener.get().onError(this, error);
            }catch (Exception e){e.printStackTrace();}
        }

    }

    //=====================WebSocketListener========================>
    @Override
    public final void ssmlSendASyncMessage(@NonNull String message) {
        try {
            this.sendASyncMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final Map<String, Map<String, Object>> getSsmlResultMap() {
        return this.resultMap;
    }

    @Override
    public final WebSocketListener getSsmlwsAbstract() {
        return WsBootService.wsListener != null ? WsBootService.wsListener.get() : null ;
    }

    @Override
    public final Object getSsmlLcok() {
        return this.lockResult;
    }

    @Override
    public int getSsmlTimeoutSeconds() {
        return TIME_OUT_SECONDS;
    }
    //<=====================WebSocketListener========================


    public static void setWsListener(WebSocketListener wsListener){
        WsBootService.wsListener = new WeakReference<>(wsListener);
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
