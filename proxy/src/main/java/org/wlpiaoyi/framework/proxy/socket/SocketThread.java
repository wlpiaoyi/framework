package org.wlpiaoyi.framework.proxy.socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;



public class SocketThread extends Thread  {

    public interface SocketThreadInterface{

        void socketStart(SocketThread socketThread);
        void socketHandle(SocketThread socketThread, byte[] buffer, int len);
        void socketData(SocketThread socketThread, byte[] buffer, int len);
        void socketConnected(SocketThread socketThread, String requestDomain, int requestPort);
        void socketEnd(SocketThread socketThread);
        void socketErro(SocketThread socketThread, Exception e);

    }

    private Socket socketIn;
    private Socket socketOut;
    private SocketThreadStream outStream;
    private SocketThreadStream inStream;
    private Proxy proxy;
    private final byte[] ver;
    private SocketProxyTools.SocketProxyType proxyType;

    private WeakReference<SocketThreadInterface> socketInterface;
    private WeakReference<SocketThreadStream.SocketThreadStreamInterface> streamInterface;

    public SocketThread(Socket socket, boolean isEncryption) {
        this.socketIn = socket;
        this.proxy = null;
        this.proxyType = SocketProxyTools.SocketProxyType.Unkown;
        this.ver = isEncryption ? SocketProxyTools.HANDLE_RESPONSE2 : SocketProxyTools.HANDLE_RESPONSE1;
    }

    public SocketThread(Socket socket, Proxy proxy, boolean isEncryption) {
        this.socketIn = socket;
        this.proxy = proxy;
        this.proxyType = SocketProxyTools.SocketProxyType.Unkown;
        this.ver = isEncryption ? SocketProxyTools.HANDLE_RESPONSE2 : SocketProxyTools.HANDLE_RESPONSE1;
    }

    public void run() {
        String host = "";
        int port = 0;
        try {
            if(socketInterface != null){
                try{
                    this.socketInterface.get().socketStart(this);
                }catch (Exception e){e.printStackTrace();}
            }
            InputStream isIn = socketIn.getInputStream();
            OutputStream osIn = socketIn.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = isIn.read(buffer);

            if(SocketProxyTools.IS_EQUES_BYTES(SocketProxyTools.HANDLE_REQUEEST1, buffer)){
                this.proxyType = SocketProxyTools.SocketProxyType.Anonymity;
            }else if(SocketProxyTools.IS_EQUES_BYTES(SocketProxyTools.HANDLE_REQUEEST2, buffer)){
                this.proxyType = SocketProxyTools.SocketProxyType.Encryption;
            }else if(SocketProxyTools.IS_EQUES_BYTES(SocketProxyTools.HANDLE_REQUEEST3, buffer)){
                this.proxyType = SocketProxyTools.SocketProxyType.ALL;
            }else {
                this.proxyType = SocketProxyTools.SocketProxyType.Unkown;
            }
            if(socketInterface != null){
                try{
                    this.socketInterface.get().socketHandle(this, buffer, len);
                }catch (Exception e){e.printStackTrace();}
            }
            osIn.write(this.ver);
            osIn.flush();
            host = SocketProxyTools.getDomain(buffer, len, this.proxyType);
            port = SocketProxyTools.getPort(buffer, len);
            if(socketInterface != null){
                try{
                    this.socketInterface.get().socketData(this, buffer, len);
                }catch (Exception e){e.printStackTrace();}
            }
            if(this.proxy != null){
                socketOut = new Socket(proxy);
                socketOut.connect(new InetSocketAddress(host, port));//服务器的ip及地址
            }else{
                socketOut = new Socket(host, port);
            }
            if(socketInterface != null){
                try{
                    this.socketInterface.get().socketConnected(this, host, port);
                }catch (Exception e){e.printStackTrace();}
            }
            InputStream isOut = socketOut.getInputStream();
            OutputStream osOut = socketOut.getOutputStream();
            for (int i = 4; i <= 9; i++) {
                SocketProxyTools.CONNECT_OK[i] = buffer[i];
            }
            osIn.write(SocketProxyTools.CONNECT_OK);
            osIn.flush();
            this.outStream = new SocketThreadStream(isIn, osOut, SocketThreadStream.StreamType.Output,
                    this.streamInterface != null ? this.streamInterface.get() : null);
            outStream.start();
            this.inStream = new SocketThreadStream(isOut, osIn, SocketThreadStream.StreamType.Input,
                    this.streamInterface != null ? this.streamInterface.get() : null);
            inStream.start();
            outStream.join();
            inStream.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(host + ":" + port);
            if(socketInterface != null){
                try{
                    this.socketInterface.get().socketErro(this, e);
                }catch (Exception ex){e.printStackTrace();}
            }
        } finally {
            this.close();
            if(socketInterface != null){
                try{
                    this.socketInterface.get().socketEnd(this);
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
    public void setStreamInterface(SocketThreadStream.SocketThreadStreamInterface streamInterface){
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
