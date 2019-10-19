package org.wlpiaoyi.framework.utils.websocket.client;

import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.wlpiaoyi.framework.utils.websocket.WebSocketListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * websocket 客户端
 */
public class WebSocketClient {

    public static int TIME_OUT_SECONDS = 5000;

    private WebSocketEcho socketEcho;

    private CountDownLatch downLatch = new CountDownLatch(1);

    @Getter
    private String url;

    public WebSocketClient(String url, WebSocketListener listener){
        this.url = url;
        this.socketEcho = new WebSocketEcho(TIME_OUT_SECONDS, this, listener, downLatch);
    }


    /**
     * 同步开启ws
     */
    public boolean syncOpen() {
        this.asyncOpen();
        try{
            if(!downLatch.await(TIME_OUT_SECONDS, TimeUnit.MILLISECONDS)){
                this.close();
                return false;
            }
        }catch (Exception e){
            this.close();
            return false;
        }
        return true;
    }
    /**
     * 异步开启ws
     */
    public void asyncOpen() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT_SECONDS, TimeUnit.MILLISECONDS)//设置读取超时时间
                .writeTimeout(TIME_OUT_SECONDS, TimeUnit.MILLISECONDS)//设置写的超时时间
                .connectTimeout(TIME_OUT_SECONDS, TimeUnit.MILLISECONDS)//设置连接超时时间
                .build();
        Request request = new Request.Builder().url(this.url).build();
        client.newWebSocket(request, this.socketEcho);
    }


    /**
     * 实现客户端同步线程安全主动推送
     * @param message
     * @return index[]: 0,uuid 1,message
     */
    public String[] sendSyncMessage(String message){
        return this.socketEcho.sendSyncMessage(message);
    }

    /**
     * 实现客户端同步线程安全主动推送
     * @param message
     * @param uuid
     * @return
     */
    public String sendSyncMessage(String uuid, String message){
        return this.socketEcho.sendSyncMessage(uuid, message);
    }

    /**
     * 实现服务器异步线程安全主动推送
     * @param message
     * @return
     */
    public boolean sendAsyncMessage(String message){
        return this.socketEcho.sendAsyncMessage(message);
    }

    /**
     * 实现服务器异步线程安全主动推送
     * @param uuid
     * @param message
     * @return
     */
    public String sendAsyncMessage(String uuid, String message){
        return this.socketEcho.sendAsyncMessage(uuid, message);
    }


    public void close(){
        this.socketEcho.close();
    }

}
