package org.wlpiaoyi.framework.proxy.socket;


import lombok.Getter;
import lombok.Setter;
import org.wlpiaoyi.framework.proxy.socket.protocol.SocketControl;
import org.wlpiaoyi.framework.proxy.socket.protocol.SocketCourse;
import org.wlpiaoyi.framework.proxy.stream.StreamThread;
import org.wlpiaoyi.framework.proxy.stream.protocol.StreamCourse;
import org.wlpiaoyi.framework.proxy.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;



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
    private Utils.SocketProxyType proxyType;

    private WeakReference<SocketControl.VerifyProxyType> verifyProxyType;
    private WeakReference<SocketControl.VerifyEncryption> verifyEncryption;
    private WeakReference<SocketControl.ResponseAddress> responseAddress;
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
        try {

            boolean enableNext = true;
            if(this.socketOperation != null && !this.socketOperation.isEnqueued()){
                try{
                    enableNext = this.socketOperation.get().socketOpen(this);
                }catch (Exception e){e.printStackTrace();}
            }
            if (!enableNext) return;

            byte[] buffer = new byte[1024];
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
            this.doAddress(buffer, isIn, osIn);
            //<===============================================

            /**
             * connection data
             */
            //===============================================>
            this.doConnectData(buffer, isIn, osIn);
            //<===============================================

        } catch (Exception e) {
            if(this.socketOperation != null && !this.socketOperation.isEnqueued()){
                try{
                    this.socketOperation.get().socketException(this, e);
                }catch (Exception ex){e.printStackTrace();}
            }
        } finally {
            this.close();
            if(this.socketOperation != null && !this.socketOperation.isEnqueued()){
                try{
                    this.socketOperation.get().socketClose(this);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }

    public void close(){
        if(this.socketIn.isClosed() == false){
            try {
                InputStream isIn = socketIn.getInputStream();
                OutputStream osIn = socketIn.getOutputStream();
                isIn.close();
                osIn.close();
            } catch (IOException e) {}
            try {
                this.socketIn.close();
            } catch (IOException e) {}
        }
        if(this.socketOut != null && this.socketOut.isClosed() == false){
            try {
                InputStream isOut = socketOut.getInputStream();
                OutputStream osOut = socketOut.getOutputStream();
                isOut.close();
                osOut.close();
            } catch (IOException e) {}
            try {
                this.socketOut.close();
            } catch (IOException e) {}
        }

    }


    public void setVerifyProxyType(SocketControl.VerifyProxyType verifyProxyType){
        if(verifyProxyType != null) this.verifyProxyType = new WeakReference<>(verifyProxyType);
        else this.verifyProxyType = null;
    }

    public void setVerifyEncryption(SocketControl.VerifyEncryption verifyEncryption){
        if(verifyEncryption != null) this.verifyEncryption = new WeakReference<>(verifyEncryption);
        else this.verifyEncryption = null;
    }
    public void setResponseAddress(SocketControl.ResponseAddress responseAddress){
        if(responseAddress != null) this.responseAddress = new WeakReference<>(responseAddress);
        else this.responseAddress = null;
    }

    public void setSocketOperation(SocketCourse socketCourse){
        if(socketCourse != null) this.socketOperation = new WeakReference<>(socketCourse);
        else  this.socketOperation = null;
    }
    public void setStreamOperation(StreamCourse streamCourse){
        if(streamCourse != null) this.streamOperation = new WeakReference<>(streamCourse);
        else this.streamOperation = null;
    }

    public  long getRecentExecuteTime(){
        return Math.max(this.inStream.getRecentExecuteTime(), this.outStream.getRecentExecuteTime());
    }


    private final void SocketThreadInit(Socket socket, Proxy proxy, byte[][] encryptionDatas) {
        this.socketIn = socket;
        this.proxy = proxy;
        this.proxyType = Utils.SocketProxyType.Unkown;
        this.encryptionDatas = encryptionDatas;
        this.requestDomain = socketIn.getInetAddress().getHostAddress();
        this.requestPort = socketIn.getPort();
        this.responseDomain = null;
        this.responsePort = 0;
    }

    private boolean doVerifyProxyType(byte[] buffer, InputStream isIn, OutputStream osIn) throws IOException {

        //read handle request
        isIn.read(buffer);

        this.proxyType = Utils.SocketProxyType.Unkown;
        //judge handle type
        if(this.verifyProxyType != null && !this.verifyProxyType.isEnqueued()){
            this.verifyProxyType.get().controlBefore(this, (byte) 0x00, isIn, osIn);
            this.proxyType = this.verifyProxyType.get().verifyProxyType(this, buffer);
        }else{
            this.proxyType = Utils.verifyProxyType(buffer);
        }
        if(proxyType == Utils.SocketProxyType.Unkown){
            osIn.write(Utils.RESPONSE_UNKOWN);
            osIn.flush();
            return false;
        }

        //response handle
        if(this.encryptionDatas != null && this.encryptionDatas.length == 2){
            osIn.write(Utils.RESPONSE_ENCRYPTION);
        }else {
            osIn.write(Utils.RESPONSE_ANONYMITY);
        }
        osIn.flush();

        return true;
    }

    private boolean doVerifyEncryption(byte[] buffer, InputStream isIn, OutputStream osIn) throws IOException {


        if(this.encryptionDatas != null && this.encryptionDatas.length == 2){
            //read username and password
            isIn.read(buffer);
            //verify username and password
            byte nameL = Utils.getEncryptionNameLength(buffer);
            byte[] name = Utils.getEncryptionName(buffer, nameL);
            if(nameL < 1 || name == null || name.length != nameL) return false;

            byte pwdL = Utils.getEncryptionPwdLenght(buffer, nameL);
            byte[] pwd = Utils.getEncryptionPwd(buffer, nameL, pwdL);
            if(pwdL < 1 || pwd == null || pwd.length != pwdL) return false;

            boolean flag;
            if(this.verifyEncryption != null && !this.verifyEncryption.isEnqueued()){
                this.verifyEncryption.get().controlBefore(this, (byte) 0x10, isIn, osIn);
                flag = this.verifyEncryption.get().verifyEncryption(this, name, nameL, pwd, pwdL);
            }else{
                flag = !Utils.IS_EQUES_BYTES(this.encryptionDatas[0], name) || !Utils.IS_EQUES_BYTES(this.encryptionDatas[1], pwd);
            }
            if(!flag){
                //verify missed
                byte[] response = Utils.ENCRYPTION_OK;
                response[1] = 0x01;
                osIn.write(response);
                osIn.flush();
                return false;
            }else {
                //verify passed
                osIn.write(Utils.ENCRYPTION_OK);
                osIn.flush();
            }
        }

        return true;
    }

    private void doAddress(byte[] buffer, InputStream isIn, OutputStream osIn) throws IOException {
        int len = isIn.read(buffer);
        if(this.responseAddress != null && !this.responseAddress.isEnqueued()){
            this.responseAddress.get().controlBefore(this, (byte) 0x20, isIn, osIn);
            this.responseDomain = this.responseAddress.get().responseDomain(this, buffer, len, this.proxyType);
            this.responsePort = this.responseAddress.get().responsePort(this, buffer, len);
        }else{
            this.responseDomain = Utils.getDomain(buffer, len, this.proxyType);
            this.responsePort = Utils.getPort(buffer, len);
        }
    }

    private boolean doConnectData(byte[] buffer, InputStream isIn, OutputStream osIn) throws IOException, InterruptedException {

        boolean enableNext = true;
        if(this.socketOperation != null && !this.socketOperation.isEnqueued()){
            try{
                enableNext = this.socketOperation.get().socketConnect(this);
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

        osIn.write(Utils.CONNECT_OK);
        osIn.flush();
        StreamCourse streamCourse = (this.streamOperation != null && this.streamOperation.isEnqueued()) ? this.streamOperation.get() : null;
        this.outStream = new StreamThread(isIn, osOut, StreamThread.StreamType.Output, this.requestDomain, this.requestPort, streamCourse);
        outStream.start();
        this.inStream = new StreamThread(isOut, osIn, StreamThread.StreamType.Input, this.requestDomain, this.requestPort, streamCourse);
        inStream.start();
        outStream.join();
        inStream.join();

        return true;
    }

}