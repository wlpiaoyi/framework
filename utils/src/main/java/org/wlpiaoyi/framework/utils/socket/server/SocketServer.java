package org.wlpiaoyi.framework.utils.socket.server;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.socket.Builder;
import org.wlpiaoyi.framework.utils.socket.IReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * A socket server that handles multiple client connections using a thread pool.
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2026/2/15 21:55</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
@Slf4j
public class SocketServer {

    // Port on which the server listens for incoming connections
    @Getter
    private final int port;

    // Map to store active client connections by their ID
    private final Map<Integer, ClientRunner> clientMaps = new ConcurrentHashMap<>();

    // Interface for reading data from clients
    private IReader iReader;

    // Flag to control whether the server is running
    private volatile boolean running = false;

    // Counter to assign unique IDs to connected clients
    private volatile int clientIndex = 0;

    // Server socket used to accept incoming client connections
    private ServerSocket serverSocket;

    private Lock lock = new ReentrantLock();


    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Constructor to initialize the socket server with a specific port.
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>port</b>
     * {@link int}
     * The port number on which the server will listen for connections.
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 21:51</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public SocketServer(int port){
        this.port = port;
    }


    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Sets the client reader interface to handle incoming data from clients.
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>iClientReader</b>
     * {@link IReader}
     * The implementation of IClientReader to process client data.
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 21:51</p>
     * <p><b>{@code @return:}</b>{@link SocketServer}
     * This instance of SocketServer for method chaining.
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public SocketServer setClientReader(IReader iReader) {
        this.iReader = iReader;
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Starts the socket server and begins listening for incoming client connections.
     * <br/> Each accepted connection is handled in a separate thread from the thread pool.
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 21:52</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public void start() {
        log.info("SocketServer.start. Starting server...");
        this.lock.lock();
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            this.serverSocket = serverSocket;
            log.info("SocketServer.start. Server started successfully on port: {}", this.port);
            log.info("SocketServer.start. Waiting for client connections...");
            this.running = true;
            this.lock.unlock();
            // Continuously accept new client connections while the server is running
            while (running) {
                try {
                    this.clientIndex++;
                    log.info("SocketServer.start. Accepting client connection from: {}, Client ID: {}",
                            serverSocket.getInetAddress().getHostAddress(), this.clientIndex);

                    // Accept a new client connection
                    var clientSocket = serverSocket.accept();

                    // Create a new SocketClient instance for the connected client
                    var client = new ClientRunner(clientSocket, this.clientIndex, this.iReader);
                    while (this.clientMaps.containsKey(this.clientIndex)){
                        this.clientIndex++;
                    }
                    // Add the client to the map of active clients
                    this.clientMaps.put(client.getClientId(), client);

                    // Submit the client task to the thread pool
                    var onFinish = new ClientOnFinish(client, this.clientMaps);
                    var future = Builder.getThreadPool().submit(client,onFinish );
                    onFinish.setFuture(future);
                    log.info("SocketServer.start. Submitted client task for Client ID: {}", client.getClientId());
                } catch (IOException e) {
                    log.warn("SocketServer.start. Error accepting connection for Client ID: {}", this.clientIndex, e);
                }
            }
        } catch (IOException e) {
            log.error("SocketServer.start. Failed to start server: {}", e.getMessage(), e);
        } finally {
            log.info("SocketServer.start. Server shutdown.");
            stop(); // Ensure proper cleanup even if an error occurs
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Shuts down the socket server gracefully by closing all active connections,
     * <br/> stopping the thread pool, and releasing resources.
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 21:53</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public void stop() {
        try {
            this.lock.lock();
            this.running = false;
            log.info("SocketServer.stop. Shutting down the server...");

            // 关闭 server socket 以中断 accept
            if (this.serverSocket != null && !this.serverSocket.isClosed()) {
                this.serverSocket.close();
                log.info("SocketServer.stop. Server socket closed.");
            }

            // 先关闭所有客户端连接，使任务尽快结束
            clientMaps.forEach((id, client) -> {
                client.close();
                log.info("SocketServer.stop. Closed connection for Client ID: {}", id);
            });
            clientMaps.clear();
        } catch (IOException e) {
            log.error("SocketServer.stop. Error occurred during server shutdown: {}", e.getMessage(), e);
        } finally {
           this.lock.unlock();
        }
    }

    static class ClientOnFinish implements java.lang.Runnable {

        private final ClientRunner sClient;
        private final Map<Integer, ClientRunner> clientMaps;

        @Setter
        private Future<Integer> future;

        ClientOnFinish(ClientRunner sClient, Map<Integer, ClientRunner> clientMaps) {
            this.sClient = sClient;
            this.clientMaps = clientMaps;
            this.future = null;
        }

        @Override
        public void run() {
            try {
                int i = 10;
                while (this.future == null && i > 0){
                    Thread.sleep(1000);
                }
                if (this.future == null) throw new RuntimeException("future is null");
                // Wait for the client task to complete (with a timeout of 60 minutes)
                this.future.get(60, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("ClientOnFinish.run. Error waiting for client task to complete: {}", e.getMessage(), e);
            } finally {
                // Clean up resources after the client disconnects
                this.sClient.close();
                this.clientMaps.remove(sClient.getClientId());
                log.info("ClientOnFinish.run. Client connection closed, Client ID: {}", sClient.getClientId());
            }
        }
    }

}
