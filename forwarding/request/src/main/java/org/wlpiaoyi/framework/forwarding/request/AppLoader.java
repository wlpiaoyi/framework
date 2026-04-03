package org.wlpiaoyi.framework.forwarding.request;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppLoader {
    public static void main(String[] args) {
        Server server = new Server();
        RequestConfigConsole.printForwardingTable();
        RequestConnectionRegistry.initFromDict();
        RequestTrafficRegistry.initFromDict();
        RequestPortStatusConsole.printInitialStatusSection();
        RequestPortStatusConsole.startPeriodicRefresh();
        try {
            server.run();
            while (true){
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            log.error("AppLoader.main. Error occurred while running server", e);
        }

    }
}