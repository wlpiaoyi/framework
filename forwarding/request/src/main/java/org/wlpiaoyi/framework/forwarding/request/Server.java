package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingConsoleDashboard;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardingPortStats;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.socket.Builder;
import org.wlpiaoyi.framework.utils.socket.server.SocketServer;
import org.wlpiaoyi.framework.utils.thread.Runnable;

import java.io.IOException;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-16 14:50:55</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class Server {

    static {
        try {
            ForwardUtils.loadMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws InterruptedException {
        if (!ForwardUtils.isLogEnabled()) {
            ForwardingPortStats.initFromDict();
            ForwardingConsoleDashboard.start();
        }
        log.info("[fw-req] bootstrap hub={} listenEntries={} buffer={}B timeout={}s",
                ForwardUtils.getResponseServerAddress(), ForwardUtils.getDict().size(),
                ForwardUtils.BUFF_CACHE_SIZE, MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut", 60));
        ForwardUtils.getDict().forEach((k, v) -> {
            Builder.getThreadPool().submit((Runnable<Object, Object>) (taskId, param) -> {
                int port = Integer.parseInt(k.toString());
                int timeOut = MapUtils.getInteger(ForwardUtils.getCONFIG_MAP(), "timeOut",60);
                String target = ForwardUtils.getRequestServerAddress(port);
                log.info("[fw-req] listen port={} -> target {}", port, target);
                var server = SocketServer.build(port, ForwardUtils.BUFF_CACHE_SIZE, timeOut)
                        .setOnBound(() -> ForwardingPortStats.markListenRunning(port))
                        .setLoadReader(clientId -> new ServerReader(port));
                server.start();
                ForwardingPortStats.markListenStopped(port);
                return 0;
            });
        });
    }


}
