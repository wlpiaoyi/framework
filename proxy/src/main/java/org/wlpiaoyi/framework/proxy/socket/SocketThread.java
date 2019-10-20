package org.wlpiaoyi.framework.proxy.socket;


import lombok.Getter;
import org.wlpiaoyi.framework.proxy.stream.StreamThread;
import org.wlpiaoyi.framework.proxy.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;



public class SocketThread extends Thread  {

    public interface SocketThreadInterface{

        boolean socketOpen(SocketThread socketThread);
        boolean socketConnect(SocketThread socketThread);

        void socketClose(SocketThread socketThread);
        void socketException(SocketThread socketThread, Exception e);

    }

    @Getter
    private String requestDomain;
    @Getter
    private int requestPort;
    @Getter
    private String responseDomain = "";
    @Getter
    private int responsePort = -1;

    private Socket socketIn;
    private Socket socketOut;
    private StreamThread outStream;
    private StreamThread inStream;
    private Proxy proxy;
    private byte[] ver;
    private Utils.SocketProxyType proxyType;

    private WeakReference<SocketThreadInterface> socketInterface;
    private WeakReference<StreamThread.StreamThreadInterface> streamInterface;

    public SocketThread(Socket socket) {
        this.SocketThreadInit(socket, null, false);
    }

    public SocketThread(Socket socket, Proxy proxy) {
        this.SocketThreadInit(socket, proxy, false);
    }
    public SocketThread(Socket socket, boolean isEncryption) {
        this.SocketThreadInit(socket, null, isEncryption);
    }

    public SocketThread(Socket socket, Proxy proxy, boolean isEncryption) {
        this.SocketThreadInit(socket, proxy, isEncryption);
    }

    private final void SocketThreadInit(Socket socket, Proxy proxy, boolean isEncryption) {
        this.socketIn = socket;
        this.requestDomain = socketIn.getInetAddress().getHostAddress();
        this.requestPort = socketIn.getPort();
        this.proxy = proxy;
        this.proxyType = Utils.SocketProxyType.Unkown;
        this.ver = isEncryption ? Utils.REQUEST_ENCRYPTION : Utils.RESPONSE_ANONYMITY;
    }

    public void run() {
        try {

            boolean enableNext = true;
            if(socketInterface != null){
                try{
                    enableNext = this.socketInterface.get().socketOpen(this);
                }catch (Exception e){e.printStackTrace();}
            }
            if (!enableNext) return;

            InputStream isIn = socketIn.getInputStream();
            OutputStream osIn = socketIn.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = isIn.read(buffer);

            if(Utils.IS_EQUES_BYTES(Utils.REQUEST_ANONYMITY, buffer)){
                this.proxyType = Utils.SocketProxyType.Anonymity;
            }else if(Utils.IS_EQUES_BYTES(Utils.REQUEST_ENCRYPTION, buffer)){
                this.proxyType = Utils.SocketProxyType.Encryption;
            }else if(Utils.IS_EQUES_BYTES(Utils.REQUEST_CUSTOM, buffer)){
                this.proxyType = Utils.SocketProxyType.Custom;
            }else {
                this.proxyType = Utils.SocketProxyType.Unkown;
            }
            osIn.write(this.ver);
            osIn.flush();

            len = isIn.read(buffer);
            this.responseDomain = Utils.getDomain(buffer, len, this.proxyType);
            this.responsePort = Utils.getPort(buffer, len);
            enableNext = true;
            if(socketInterface != null){
                try{
                    enableNext = this.socketInterface.get().socketConnect(this);
                }catch (Exception e){e.printStackTrace();}
            }
            if (!enableNext) return;


            if(this.proxy != null){
                socketOut = new Socket(proxy);
                socketOut.connect(new InetSocketAddress(this.responseDomain, this.responsePort));//服务器的ip及地址
            }else{
                socketOut = new Socket(this.responseDomain, this.responsePort);
            }
            InputStream isOut = socketOut.getInputStream();
            OutputStream osOut = socketOut.getOutputStream();

//            for (int i = 4; i <= 9; i++) {
//                Utils.CONNECT_OK[i] = buffer[i];
//            }
            osIn.write(Utils.CONNECT_OK);
//            osIn.write(Utils.getConnectBytes(buffer,len,proxyType));
            osIn.flush();
            this.outStream = new StreamThread(isIn, osOut, StreamThread.StreamType.Output,
                    this.streamInterface != null ? this.streamInterface.get() : null);
            outStream.start();
            this.inStream = new StreamThread(isOut, osIn, StreamThread.StreamType.Input,
                    this.streamInterface != null ? this.streamInterface.get() : null);
            inStream.start();
            outStream.join();
            inStream.join();
        } catch (Exception e) {
            if(socketInterface != null){
                try{
                    this.socketInterface.get().socketException(this, e);
                }catch (Exception ex){e.printStackTrace();}
            }
        } finally {
            this.close();
            if(socketInterface != null){
                try{
                    this.socketInterface.get().socketClose(this);
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


    public void setSocketInterface(SocketThreadInterface threadInterface){
        if(threadInterface != null) this.socketInterface = new WeakReference<>(threadInterface);
    }
    public void setStreamInterface(StreamThread.StreamThreadInterface streamInterface){
        if(streamInterface != null) this.streamInterface = new WeakReference<>(streamInterface);
    }

    public  long getRecentExecuteTime(){
        return Math.max(this.inStream.getRecentExecuteTime(), this.outStream.getRecentExecuteTime());
    }

//    @Override
//    public void streamStart(SocketThreadStream stream) {
//        if(socketInterface != null){
//            try{
//                this.socketInterface.get().streamStart(this, stream);
//            }catch (Exception e){e.printStackTrace();}
//        }
//    }
//
//    @Override
//    public void inStream(SocketThreadStream stream, byte[] buffer, int len) {
//        if(socketInterface != null){
//            try{
//                this.socketInterface.get().streaming(this, stream, buffer, len);
//            }catch (Exception e){e.printStackTrace();}
//        }
//    }
//
//    @Override
//    public void endStream(SocketThreadStream stream) {
//        if(socketInterface != null){
//            try{
//                this.socketInterface.get().streamEnd(this, stream);
//            }catch (Exception e){e.printStackTrace();}
//        }
//    }
//
//    @Override
//    public void erroStream(SocketThreadStream stream, Exception e) {
//        if(socketInterface != null){
//            try{
//                this.socketInterface.get().streamErro(this, stream, e);
//            }catch (Exception ex){e.printStackTrace();}
//        }
//
//    }

}
