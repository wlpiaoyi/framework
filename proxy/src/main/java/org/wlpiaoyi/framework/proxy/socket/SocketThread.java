package org.wlpiaoyi.framework.proxy.socket;


import lombok.Getter;
import lombok.Setter;
import org.wlpiaoyi.framework.proxy.rule.SocketRuleUtils;
import org.wlpiaoyi.framework.proxy.rule.SocketRule;
import org.wlpiaoyi.framework.proxy.socket.protocol.SocketCourse;
import org.wlpiaoyi.framework.proxy.stream.StreamThread;
import org.wlpiaoyi.framework.proxy.stream.protocol.StreamCourse;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.wlpiaoyi.framework.proxy.rule.SocketRule.BUFFER_LEN;


public class SocketThread extends Thread{


    @Getter
    private String requestDomain;
    @Getter
    private int requestPort;
    @Getter
    private String responseDomain;
    @Getter
    private int responsePort;
    @Getter
    private byte[][] encryptionDatas;
    @Getter @Setter
    private Object userInfo;

    private Socket socketIn;
    private Socket socketOut;
    private StreamThread outStream;
    private StreamThread inStream;
    private Proxy proxy;
    private SocketRuleUtils.ProxyType proxyType;

    private SocketRule socketRule;
    private WeakReference<SocketCourse> socketOperation;
    private WeakReference<StreamCourse> streamOperation;

    public SocketThread(Socket socket) {
        this.SocketThreadInit(socket, null, null);
    }

    public SocketThread(Socket socket, Proxy proxy) {
        this.SocketThreadInit(socket, proxy, null);
    }

    public SocketThread(Socket socket, byte[][] encryptionDatas) {
        this.SocketThreadInit(socket, null, encryptionDatas);
    }

    public SocketThread(Socket socket, Proxy proxy, byte[][] encryptionDatas) {
        this.SocketThreadInit(socket, proxy, encryptionDatas);
    }

    public void run() {
        Map<Object, Object> userMap = new HashMap<>();
        try {
            boolean enableNext = true;
            if(this.socketOperation != null && !this.socketOperation.isEnqueued()){
                try{
                    enableNext = this.socketOperation.get().socketOpen(this,userMap);
                }catch (Exception e){e.printStackTrace();}
            }
            if (!enableNext) return;

            byte[] buffer = new byte[BUFFER_LEN];
            /**
             * handle protocol
             */
            //===============================================>
            InputStream isIn = socketIn.getInputStream();
            OutputStream osIn = socketIn.getOutputStream();
            if(!this.doVerifyProxyType(buffer, isIn, osIn)) return;
            if(!this.doVerifyEncryption(buffer, isIn, osIn)) return;
            //<===============================================

            /**
             * read request host and port
             */
            //===============================================>
            this.doAddress(buffer, isIn, osIn, userMap);
            //<===============================================

            /**
             * connection data
             */
            //===============================================>
            this.doConnectData(buffer, isIn, osIn, userMap);
            //<===============================================

        } catch (Exception e) {
            if(this.socketOperation != null && !this.socketOperation.isEnqueued()){
                try{
                    this.socketOperation.get().socketException(this, e, userMap);
                }catch (Exception ex){e.printStackTrace();}
            }
        } finally {
            this.close();
            if(this.socketOperation != null && !this.socketOperation.isEnqueued()){
                try{
                    this.socketOperation.get().socketClose(this, userMap);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }

    public void close(){
        if(this.outStream != null) this.outStream.close();
        if(this.inStream != null) this.inStream.close();
    }

    public void setSocketOperation(SocketCourse socketCourse){
        if(socketCourse != null) this.socketOperation = new WeakReference<>(socketCourse);
        else  this.socketOperation = null;
    }
    public void setStreamOperation(StreamCourse streamCourse){
        if(streamCourse != null) this.streamOperation = new WeakReference<>(streamCourse);
        else this.streamOperation = null;
    }

    public final long getRecentExecuteTime(){
        return Math.max(this.inStream.getRecentExecuteTime(), this.outStream.getRecentExecuteTime());
    }


    private final void SocketThreadInit(Socket socket, Proxy proxy, byte[][] encryptionDatas) {
        this.socketIn = socket;
        this.proxy = proxy;
        this.proxyType = SocketRuleUtils.ProxyType.Unknown;
        this.encryptionDatas = encryptionDatas;
        this.requestDomain = socketIn.getInetAddress().getHostAddress();
        this.requestPort = socketIn.getPort();
        this.responseDomain = null;
        this.responsePort = 0;
    }

    private boolean doVerifyProxyType(byte[] buffer, InputStream isIn, OutputStream osIn) throws IOException {
        isIn.read(buffer);
        this.proxyType = SocketRuleUtils.ProxyType.Unknown;
        this.socketRule = SocketRuleUtils.getSocketRule(SocketRuleUtils.socketProxyVersion(buffer));
        assert this.socketRule != null;
        this.proxyType = this.socketRule.getProxyType(buffer);
        this.socketRule.proxyTypeHandle(proxyType, osIn, this.encryptionDatas);
        osIn.flush();

        return true;
    }

    private boolean doVerifyEncryption(byte[] buffer, InputStream isIn, OutputStream osIn) throws IOException {
        return this.socketRule.doVerifyEncryption(buffer, this.encryptionDatas, isIn, osIn);
    }

    private void doAddress(byte[] buffer, InputStream isIn, OutputStream osIn, Map<Object, Object> userMap) throws IOException {
        this.socketRule.doAddress(buffer, this.proxyType, isIn, osIn, userMap);
        this.responseDomain = MapUtils.getString(userMap, "responseDomain");
        this.responsePort = MapUtils.getInteger(userMap, "responsePort");
        userMap.remove("responseAddress");
        userMap.remove("responseDomain");
        userMap.remove("responsePort");
        userMap.put("responsePort", this.responsePort);
        if(ValueUtils.isNotBlank(this.responseDomain)){
            userMap.put("responseDomain", this.responseDomain);
        }
    }

    private boolean doConnectData(byte[] buffer, InputStream isIn, OutputStream osIn, Map<Object, Object> userMap) throws IOException, InterruptedException {

        boolean enableNext = true;
        if(this.socketOperation != null && !this.socketOperation.isEnqueued()){
            try{
                enableNext = this.socketOperation.get().socketConnect(this, userMap);
            }catch (Exception e){e.printStackTrace();}
        }
        if (!enableNext) return false;

        if(this.proxy != null){
            //connected from proxy
            socketOut = new Socket(proxy);
            socketOut.connect(new InetSocketAddress(this.responseDomain, this.responsePort));//服务器的ip及地址
        }else{
            //connected direction
            socketOut = new Socket(this.responseDomain, this.responsePort);
        }
        InputStream isOut = socketOut.getInputStream();
        OutputStream osOut = socketOut.getOutputStream();

        this.socketRule.doConnectData(osIn, userMap);

        CountDownLatch downLatch = new CountDownLatch(1);
        StreamCourse streamCourse = (this.streamOperation != null && !this.streamOperation.isEnqueued()) ? this.streamOperation.get() : null;
        this.outStream = new StreamThread(isIn, osOut, StreamThread.StreamType.Output, this.requestDomain, this.requestPort, downLatch, streamCourse, userMap);
        outStream.start();
        this.inStream = new StreamThread(isOut, osIn, StreamThread.StreamType.Input, this.requestDomain, this.requestPort, downLatch,  streamCourse, userMap);
        inStream.start();

        return downLatch.await(4, TimeUnit.HOURS);
    }

}