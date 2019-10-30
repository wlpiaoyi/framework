package org.wlpiaoyi.framework.utils.websocket.client;

import com.google.gson.Gson;
import org.apache.http.client.utils.URIBuilder;
import org.java_websocket.handshake.ServerHandshake;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private WeakReference<WebSocketIntrface> wsInterface;

    private CountDownLatch downLatch;
    private Exception connectException;

    public WebSocketClient(URI serverUri, WebSocketIntrface wsInterface) {
        super(serverUri);
        this.wsInterface = wsInterface == null ? null : new WeakReference<WebSocketIntrface>(wsInterface);
    }
    public WebSocketClient(String url, WebSocketIntrface wsInterface) throws URISyntaxException {
        super(new URIBuilder(url).build());
        this.wsInterface = wsInterface == null ? null : new WeakReference<WebSocketIntrface>(wsInterface);
    }

    public boolean synConnect(long timeout, TimeUnit timeUnit) throws Exception {
        if(downLatch != null){
            try{ downLatch.countDown();} catch (Exception e){e.printStackTrace();}
        }
        downLatch = new CountDownLatch(1);
        super.connectBlocking(timeout, timeUnit);
        if(!downLatch.await(timeout, timeUnit)){
            downLatch = null;
            if(connectException == null) connectException = new TimeoutException("connect time out");
            throw connectException;
        }
        downLatch = null;
        return true;
    }

    public void send(Object data) throws NotYetConnectedException{
        this.send(new Gson().toJson(data));
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        downLatch.countDown();
        if(this.wsInterface == null || this.wsInterface.isEnqueued()) return;
        this.wsInterface.get().onOpen(this, serverHandshake);
    }

    @Override
    public void onMessage(String message) {
        if(this.wsInterface == null || this.wsInterface.isEnqueued()) return;
        this.wsInterface.get().onMessage(this, message);

    }

    @Override
    public void onClose(int code, String message, boolean b) {
        if(downLatch != null){
            connectException = new BusinessException(code, message);
            downLatch.countDown();
            downLatch = null;
        }
        if(this.wsInterface == null || this.wsInterface.isEnqueued()) return;
        this.wsInterface.get().onClose(this, code, message, b);

    }

    @Override
    public void onError(Exception e) {
        if(downLatch != null){
            connectException = e;
            downLatch.countDown();
            downLatch = null;
        }
        if(this.wsInterface == null || this.wsInterface.isEnqueued()) return;
        this.wsInterface.get().onError(this, e);
        this.close();

    }
}
