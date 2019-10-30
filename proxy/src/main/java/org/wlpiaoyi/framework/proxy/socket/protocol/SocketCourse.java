package org.wlpiaoyi.framework.proxy.socket.protocol;


import org.wlpiaoyi.framework.proxy.socket.SocketThread;

public interface SocketCourse {

    /**
     * Stop if it returns false, otherwise continue open
     * @param socketThread
     * @return
     */
    boolean socketOpen(SocketThread socketThread);

    /**
     * Stop if it returns false, otherwise continue connect
     * @param socketThread
     * @return
     */
    boolean socketConnect(SocketThread socketThread);

    /**
     *
     * @param socketThread
     */
    void socketClose(SocketThread socketThread);

    /**
     *
     * @param socketThread
     * @param e
     */
    void socketException(SocketThread socketThread, Exception e);

}
