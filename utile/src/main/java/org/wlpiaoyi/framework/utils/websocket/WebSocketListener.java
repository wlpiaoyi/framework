package org.wlpiaoyi.framework.utils.websocket;

public interface WebSocketListener {

    /**
     * 建立连接回调
     * @param target
     */
    void onOpen(Object target);
    /**
     * 收到客户端消息后调用的方法
     * @param target
     * @param message
     */
    void onMessage(Object target,String message, String uuid);
    /**
     * 收到客户端消息后调用的方法
     * @param target
     * @param message
     */
    void onMessage(Object target,String message);

    /**
     * 关闭连接回调
     */
    void onClose(Object target);

    /**
     * 异常连接回调
     * @param target
     * @param error
     */
    void onError(Object target, Throwable error);

}
