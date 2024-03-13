package org.wlpiaoyi.framework.proxy.stream.protocol;


import org.wlpiaoyi.framework.proxy.stream.StreamThread;

import java.util.Map;

public interface StreamCourse {
    void streamStart(StreamThread stream, Map<Object, Object> userMap);
    byte[] streaming(StreamThread stream, byte[] buffer, int len, Map<Object, Object> userMap);
    void streamEnd(StreamThread stream, Map<Object, Object> userMap);
    void streamErro(StreamThread stream, Exception e, Map<Object, Object> userMap);
}
