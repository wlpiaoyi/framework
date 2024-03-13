package org.wlpiaoyi.framework.proxy.stream;

import lombok.Getter;
import org.wlpiaoyi.framework.proxy.stream.protocol.StreamCourse;
import org.wlpiaoyi.framework.proxy.utils.Utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Map;
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

    private final Map<Object, Object> userMap;
    private final WeakReference<StreamCourse> streamInterface;

    public StreamThread(InputStream inputStream,
                        OutputStream outputStream,
                        StreamType streamType,
                        String host,
                        int port,
                        StreamCourse streamCourse,
                        Map<Object, Object> userMap){
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.streamType = streamType;
        this.host = host;
        this.port = port;
        this.downLatch = null;
        if(streamCourse != null)this.streamInterface = new WeakReference<>(streamCourse);
        else this.streamInterface = null;
        this.userMap = userMap;
    }

    public StreamThread(InputStream inputStream,
                        OutputStream outputStream,
                        StreamType streamType,
                        String host,
                        int port,
                        CountDownLatch downLatch,
                        StreamCourse streamCourse,
                        Map<Object, Object> userMap){
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.streamType = streamType;
        this.host = host;
        this.port = port;
        this.downLatch = downLatch;
        if(streamCourse != null)this.streamInterface = new WeakReference<>(streamCourse);
        else this.streamInterface = null;
        this.userMap = userMap;
    }

    public void close(){
        if(this.inputStream != null)
            try{this.inputStream.close(); this.inputStream = null;}catch (Exception e){e.printStackTrace();}
        if(this.outputStream != null)
            try{this.outputStream.close(); this.outputStream = null;}catch (Exception e){e.printStackTrace();}
        if(this.downLatch != null){
            try{
                this.downLatch.countDown();
                synchronized (this.downLatch){
                    if(this.downLatch.getCount() > 0){
                    }
                }
            }catch (Exception e){e.printStackTrace();}
        }
    }

    public void run() {
        try {
            this.beginExecuteTime = System.currentTimeMillis();
            if(this.streamInterface != null && !this.streamInterface.isEnqueued()) this.streamInterface.get().streamStart(this, userMap);
            byte[] buffer = new byte[Utils.BUFFER_LEN];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                if (len > 0) {
                    this.recentExecuteTime = System.currentTimeMillis();
                    if(this.streamInterface != null && !this.streamInterface.isEnqueued()){
                        byte[] rbuffer = this.streamInterface.get().streaming(this, buffer, len, userMap);
                        if(rbuffer != null) outputStream.write(rbuffer);
                        else outputStream.write(buffer, 0, len);
                    }else outputStream.write(buffer, 0, len);
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            if(this.streamInterface != null && !this.streamInterface.isEnqueued()) this.streamInterface.get().streamErro(this, e, userMap);
        } finally {
            this.close();
            if(this.streamInterface != null && !this.streamInterface.isEnqueued()) this.streamInterface.get().streamEnd(this, userMap);
        }
    }

}
