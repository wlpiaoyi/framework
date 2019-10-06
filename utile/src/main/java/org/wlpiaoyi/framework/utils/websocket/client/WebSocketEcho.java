package org.wlpiaoyi.framework.utils.websocket.client;

import lombok.NonNull;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import org.wlpiaoyi.framework.utils.websocket.WebSocketListener;
import org.wlpiaoyi.framework.utils.websocket.WsUtile;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

final class WebSocketEcho extends okhttp3.WebSocketListener implements WsUtile.SendASyncMessageLitsener {

    private WebSocket webSocket;

    private Map<String,Map<String, Object>> resultMap = new HashMap<>();

    private Object lock = new Object();

    private int timeout;

    private WeakReference<WebSocketListener> listener;

    private WeakReference<WebSocketClient> client;

    private CountDownLatch downLatch;


    WebSocketEcho(int timeout, WebSocketClient client, WebSocketListener listener, CountDownLatch downLatch){
        this.timeout = timeout;
        this.client = new WeakReference<>(client);
        this.downLatch = downLatch;
        if(listener != null) this.listener = new WeakReference<>(listener);
    }

    @Override
    public void ssmlSendASyncMessage(@NonNull String message) {
        this.sendASyncMessage(message);
    }

    @Override
    public Map<String, Map<String, Object>> getSsmlResultMap() {
        return this.resultMap;
    }

    @Override
    public WebSocketListener getSsmlwsAbstract() {
        return this.listener != null ? this.listener.get() : null;
    }

    @Override
    public Object getSsmlLcok() {
        return this.lock;
    }

    @Override
    public int getSsmlTimeoutSeconds() {
        return WebSocketClient.TIME_OUT_SECONDS;
    }

    /**
     * 实现服务器异步线程安全主动推送
     * @param uuid
     * @param message
     * @return
     */
    public boolean sendASyncMessage(@NonNull String uuid, @NonNull String message){
        String sendArg = uuid + ":" + message;
        return this.sendASyncMessage(sendArg);
    }

    /**
     * 实现服务器异步线程安全主动推送
     * @param text
     * @return
     */
    public boolean sendASyncMessage(@NonNull String text){
        return this.webSocket.send(text);
    }


    /**
     * 实现客户端同步线程安全主动推送
     * @param message
     * @return index[]: 0,uuid 1,message
     */
    public String[] sendSyncMessage(@NonNull String message){
        String uuid = WsUtile.getUUID64();
        return new String[]{uuid, this.sendSyncMessage(uuid, message)};
    }
    /**
     * 实现客户端同步线程安全主动推送
     * @param message
     * @param uuid
     * @return
     */
    public String sendSyncMessage(@NonNull String uuid, @NonNull String message){
        return WsUtile.ssmlSendSyncMessage(uuid, message, this);
    }

    public void close(){
        if(this.webSocket == null) return;
        this.webSocket.cancel();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        this.webSocket = webSocket;
        this.downLatch.countDown();

        if(this.listener != null){
            try{
                this.listener.get().onOpen(this.client.get());
            }catch (Exception e){e.printStackTrace();}
        }

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        super.onMessage(webSocket, message);
        WsUtile.onMessage(message,this.client, this);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        if(this.listener != null){
            try{
                this.listener.get().onClose(this.client.get());
            }catch (Exception e){e.printStackTrace();}
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        if(this.listener != null){
            try{
                this.listener.get().onError(this.client, t.getCause());
            }catch (Exception e){e.printStackTrace();}
        }
    }
}
