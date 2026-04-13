package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.socket.server.SocketServer;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-16 14:50:55</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class Server {

    private final SocketServer server;
    private final int listenPort;

    public Server(int port) {
        this.listenPort = port;
        int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut",60);
        this.server = SocketServer.build(port, ForwardUtils.BUFF_CACHE_SIZE, timeOut).setLoadReader(clientId -> new ServerReader());
    }

    public void run() throws InterruptedException {
        log.info("[fw-res] bootstrap listenPort={} buffer={}B timeout={}s",
                listenPort, ForwardUtils.BUFF_CACHE_SIZE, MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut", 60));
        this.server.start();
    }


}
