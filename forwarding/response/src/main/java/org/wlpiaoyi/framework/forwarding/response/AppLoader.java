package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;

import java.io.IOException;

@Slf4j
public class AppLoader {

    static {
        try {
            ForwardUtils.loadMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(Integer.parseInt(ForwardUtils.getResponseServerAddress().split(":")[1]));
        try {
            server.run();
        } catch (InterruptedException e) {
            log.error("AppLoader.main. Error occurred while running server", e);
        }

    }
}