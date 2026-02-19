package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppLoader {
    public static void main(String[] args) {
        Server server = new Server(8080);
        try {
            server.run();
        } catch (InterruptedException e) {
            log.error("AppLoader.main. Error occurred while running server", e);
        }

    }
}