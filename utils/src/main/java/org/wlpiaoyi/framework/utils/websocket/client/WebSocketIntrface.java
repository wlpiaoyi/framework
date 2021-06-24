package org.wlpiaoyi.framework.utils.websocket.client;

import org.java_websocket.handshake.ServerHandshake;

public interface WebSocketIntrface {

    void onOpen(WebSocketClient webSocketClient, ServerHandshake serverHandshake);

    void onMessage(WebSocketClient webSocketClient, String message);

    void onClose(WebSocketClient webSocketClient, int code, String message, boolean b);

    void onError(WebSocketClient webSocketClient, Exception e);
}
