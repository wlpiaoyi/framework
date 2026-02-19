package org.wlpiaoyi.framework.forwarding.request;

import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.socket.Builder;
import org.wlpiaoyi.framework.utils.socket.server.SocketServer;
import org.wlpiaoyi.framework.utils.thread.Runnable;

import java.io.IOException;
import java.util.Map;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-16 14:50:55</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class Server {

    static {
        try {
            ForwardUtils.loadMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() throws InterruptedException {
        ForwardUtils.getDict().forEach((k, v) -> {
            Builder.getThreadPool().submit((Runnable<Object, Object>) (taskId, param) -> {
                int port = Integer.parseInt(k.toString());
                var server = SocketServer.build(port, ForwardUtils.BUFF_CACHE_SIZE).setLoadReader(clientId -> new ServerReader(port));
                server.start();
                return 0;
            });
        });
    }


}
