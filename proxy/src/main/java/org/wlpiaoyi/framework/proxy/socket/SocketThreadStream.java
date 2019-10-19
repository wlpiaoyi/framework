package org.wlpiaoyi.framework.proxy.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

//00 00 0D 0A 30 0D 0A 0D 0A
//61 63 68 65 0D 0A 0D 0A
public class SocketThreadStream extends Thread{

    public enum StreamType {
        Input,
        Output
    }

    public interface SocketThreadStreamInterface{
        void streamStart(SocketThreadStream stream);
        void streaming(SocketThreadStream stream, byte[] buffer, int len);
        void streamEnd(SocketThreadStream stream);
        void streamErro(SocketThreadStream stream, Exception e);
    }

    private InputStream inputStream;
    private OutputStream outputStream;
    private StreamType streamType;
    private long recentExecuteTime;

    private final WeakReference<SocketThreadStreamInterface> streamInterface;

    public SocketThreadStream(InputStream inputStream, OutputStream outputStream, StreamType streamType, SocketThreadStreamInterface streamInterface){
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.streamType = streamType;
        if(streamInterface != null)this.streamInterface = new WeakReference<>(streamInterface);
        else this.streamInterface = null;
    }

    public StreamType getStreamType(){
        return this.streamType;
    }

    public long getRecentExecuteTime(){
        return this.recentExecuteTime;
    }

    public void run() {
        try {
            if(this.streamInterface != null) this.streamInterface.get().streamStart(this);
            byte[] buffer = new byte[64];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                if (len > 0) {
                    this.recentExecuteTime = System.currentTimeMillis();
                    outputStream.write(buffer, 0, len);
                    outputStream.flush();
                    if(this.streamInterface != null) this.streamInterface.get().streaming(this, buffer, len);
                }
            }
        } catch (Exception e) {
            if(this.streamInterface != null) this.streamInterface.get().streamErro(this, e);
        } finally {
            if(this.streamInterface != null) this.streamInterface.get().streamEnd(this);
        }
    }

}
