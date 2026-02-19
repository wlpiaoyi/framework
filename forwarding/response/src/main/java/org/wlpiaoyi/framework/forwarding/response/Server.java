package org.wlpiaoyi.framework.forwarding.response;

import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.socket.server.SocketServer;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-16 14:50:55</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class Server {

    private final SocketServer server;

    public Server(int port) {
        int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut",60);
        this.server = SocketServer.build(port, ForwardUtils.BUFF_CACHE_SIZE, timeOut).setLoadReader(clientId -> new ServerReader());
    }

    public void run() throws InterruptedException {
        this.server.start();
    }


}
