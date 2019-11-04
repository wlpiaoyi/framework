package org.wlpiaoyi.framework.proxy.stream.protocol;


import org.wlpiaoyi.framework.proxy.stream.StreamThread;

public interface StreamCourse {
    void streamStart(StreamThread stream);
    byte[] streaming(StreamThread stream, byte[] buffer, int len);
    void streamEnd(StreamThread stream);
    void streamErro(StreamThread stream, Exception e);
}
