package org.wlpiaoyi.framework.proxy.stream;

import lombok.Getter;
import org.wlpiaoyi.framework.proxy.stream.protocol.StreamCourse;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

//00 00 0D 0A 30 0D 0A 0D 0A
//61 63 68 65 0D 0A 0D 0A
public class StreamThread extends Thread{

    public enum StreamType {
        Input,
        Output
    }


    private InputStream inputStream;
    private OutputStream outputStream;
    @Getter
    private StreamType streamType;
    @Getter
    private long beginExecuteTime;
    @Getter
    private long recentExecuteTime;
    @Getter
    private String host;
    @Getter
    private int port;

    private CountDownLatch downLatch;

    private final WeakReference<StreamCourse> streamInterface;

    public StreamThread(InputStream inputStream,
                        OutputStream outputStream,
                        StreamType streamType,
                        String host,
                        int port,
                        StreamCourse streamCourse){
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.streamType = streamType;
        this.host = host;
        this.port = port;
        if(streamCourse != null)this.streamInterface = new WeakReference<>(streamCourse);
        else this.streamInterface = null;
    }

    public void startStrem(CountDownLatch downLatch){
        this.downLatch = downLatch;
        super.start();
    }

    public void stopStream(){
        try{this.inputStream.close();}catch (Exception e){e.printStackTrace();}
        try{this.outputStream.close();}catch (Exception e){e.printStackTrace();}
    }

    public void run() {
        try {
            this.beginExecuteTime = System.currentTimeMillis();
            if(this.streamInterface != null && !this.streamInterface.isEnqueued()) this.streamInterface.get().streamStart(this);
            byte[] buffer = new byte[64];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                if (len > 0) {
                    this.recentExecuteTime = System.currentTimeMillis();
                    if(this.streamInterface != null && !this.streamInterface.isEnqueued()){
                        byte[] rbuffer = this.streamInterface.get().streaming(this, buffer, len);
                        if(rbuffer != null) outputStream.write(rbuffer);
                        else outputStream.write(buffer, 0, len);
                    }else outputStream.write(buffer, 0, len);
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            if(this.streamInterface != null && !this.streamInterface.isEnqueued()) this.streamInterface.get().streamErro(this, e);
        } finally {
            if(this.downLatch != null){
                this.downLatch.countDown();
                this.downLatch = null;
            }
            if(this.streamInterface != null && !this.streamInterface.isEnqueued()) this.streamInterface.get().streamEnd(this);
        }
    }

}
