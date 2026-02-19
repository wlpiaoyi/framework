package org.wlpiaoyi.framework.forwarding.request;

import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.utils.socket.Builder;
import org.wlpiaoyi.framework.utils.socket.server.SocketServer;
import org.wlpiaoyi.framework.utils.thread.Runnable;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-16 14:50:55</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class Server {

    public void run() throws InterruptedException {

        Builder.getThreadPool().submit((Runnable<Object, Object>) (taskId, param) -> {
            int port = 1122;
            var server = SocketServer.build(port, ForwardUtils.BUFF_CACHE_SIZE).setLoadReader(clientId -> new ServerReader(port));
            server.start();
            return 0;
        });

        Builder.getThreadPool().submit((Runnable<Object, Object>) (taskId, param) -> {
            int port = 1180;
            var server = SocketServer.build(port, ForwardUtils.BUFF_CACHE_SIZE).setLoadReader(clientId -> new ServerReader(port));
            server.start();
            return 0;
        });
    }


}
