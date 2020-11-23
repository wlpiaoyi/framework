package org.wlpiaoyi.framework.proxy.socket.protocol;


import org.wlpiaoyi.framework.proxy.socket.SocketThread;

import java.util.Map;

public interface SocketCourse {

    /**
     * Stop if it returns false, otherwise continue open
     * @param socketThread
     * @return
     */
    boolean socketOpen(SocketThread socketThread, Map<Object, Object> userMap);

    /**
     * Stop if it returns false, otherwise continue connect
     * @param socketThread
     * @return
     */
    boolean socketConnect(SocketThread socketThread, Map<Object, Object> userMap);

    /**
     *
     * @param socketThread
     */
    void socketClose(SocketThread socketThread, Map<Object, Object> userMap);

    /**
     *
     * @param socketThread
     * @param e
     */
    void socketException(SocketThread socketThread, Exception e, Map<Object, Object> userMap);

}
