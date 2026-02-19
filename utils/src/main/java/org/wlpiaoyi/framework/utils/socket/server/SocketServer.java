package org.wlpiaoyi.framework.utils.socket.server;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.socket.Builder;
import org.wlpiaoyi.framework.utils.socket.IReader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    private final int bufferSize;

    // Port on which the server listens for incoming connections
    @Getter
    private final int port;

    @Getter
    private final int timeOut;

    // Map to store active client connections by their ID
    private final Map<Integer, ClientRunner> clientMaps = new ConcurrentHashMap<>();

    // Flag to control whether the server is running
    private volatile boolean running = false;

    private LoadReader loadReader;

    private final Lock lock = new ReentrantLock();

    private final Object synTagObj = new Object();


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
    SocketServer(int port, int bufferSize, int timeOut){
        this.port = port;
        this.bufferSize = bufferSize;
        this.timeOut = timeOut;
    }

    public static SocketServer build(int port, int bufferSize, int timeOut){
        return new SocketServer(port, bufferSize, timeOut);
    }

    public SocketServer setLoadReader(LoadReader loadReader){
        this.loadReader = loadReader;
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
    public boolean start() {
        log.debug("SocketServer.start. Starting server...");
        this.lock.lock();
        if(this.running){
            log.warn("SocketServer.start. Server is already running.");
            this.lock.unlock();
            return false;
        }
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            this.lock.unlock();
            log.debug("SocketServer.start. Server started successfully on port: {}", this.port);
            this.listener(serverSocket);
            return true;
        } catch (Exception e) {
            log.error("SocketServer.start. Failed to start server: {}", e.getMessage(), e);
            return false;
        } finally {
            this.lock.unlock();
            log.debug("SocketServer.listener. Server shutdown.");
            stop(); // Ensure proper cleanup even if an error occurs
            this.synTagObj.notifyAll();
        }
    }

    private void listener(ServerSocket serverSocket) {
        log.debug("SocketServer.listener. Server is running.");
        this.running = true;
        // Continuously accept new client connections while the server is running
        final AtomicInteger clientIndex = new AtomicInteger(0);
        int clientId = 0;
        while (running) {
            try {
                clientId = 0;
                if(clientIndex.get() < 0) clientIndex.set(1);
                while (clientIndex.get() == 0 || this.clientMaps.containsKey(clientIndex.get())){
                    clientIndex.incrementAndGet();
                }
                clientId = clientIndex.get();
//                log.debug("SocketServer.listener. Waiting for client connections...");
                // Accept a new client connection
                var clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(this.timeOut * 1000);
//                log.debug("SocketServer.listener. Accepted client connection from: {}", clientSocket.getInetAddress().getHostAddress());
                IReader reader = this.loadReader.loadReader(clientId);
                if(reader == null){
                    log.warn("SocketServer.listener. No reader for client: {}", clientId);
                    clientSocket.close();
                    continue;
                }
                // Create a new SocketClient instance for the connected client
                var client = new ClientRunner(clientSocket, clientId, reader, this.bufferSize);
                var onFinish = new ClientOnFinish(client, this, reader);
                // Add the client to the map of active clients
                this.clientMaps.put(client.getClientId(), client);
                if(reader.begin(clientId, clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort()) == -1){
                    log.warn("SocketServer.listener. Reader begin failed for client: {}", clientId);
                    this.close(clientId);
                    continue;
                }
                // Submit the client task to the thread pool
                var future = Builder.getThreadPool().submit(client, onFinish);
//                    var future = Builder.getThreadPool().submit();
                onFinish.setFuture(future);
//                log.debug("SocketServer.listener. Submitted client task for Client ID: {}", client.getClientId());
            } catch (Exception e) {
                log.warn("SocketServer.listener. Error accepting connection for Client ID: {}", clientId, e);
                this.close(clientId);
            }
        }
    }


    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Waits for the server to stop.
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 21:53</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <p><b>{@code @return:}</b>{@link boolean}
     * <p><b>{@code @throws:}</b>{@link InterruptedException}</p>
     * <hr/>
     */
    public void await() throws InterruptedException {
        if(!this.running) throw new IllegalStateException("Server is not running.");
        this.synTagObj.wait();
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
            log.debug("SocketServer.stop. Shutting down the server...");
            // 先关闭所有客户端连接，使任务尽快结束
            clientMaps.forEach((id, client) -> {
                client.close();
                log.debug("SocketServer.stop. Closed connection for Client ID: {}", id);
            });
            clientMaps.clear();
        } catch (Exception e) {
            log.error("SocketServer.stop. Error occurred during server shutdown: {}", e.getMessage(), e);
        } finally {
           this.lock.unlock();
        }
    }

    public void close(int clientId){
        try {
            log.debug("SocketServer.close. Closing client for Client ID: {}", clientId);
            this.lock.lock();
            if(!this.clientMaps.containsKey(clientId)){
                log.warn("SocketServer.close. No client for clientId: {}", clientId);
                return;
            }
            var client = this.clientMaps.get(clientId);
            client.close();
            this.clientMaps.remove(clientId);
        } catch (Exception e) {
            log.error("SocketServer.close. Error occurred during client close: {}", e.getMessage(), e);
        } finally {
            this.lock.unlock();
        }
    }

    static class ClientOnFinish implements Runnable {

        private final ClientRunner sClient;
        private final WeakReference<SocketServer> socketServer;
        private final IReader reader;

        @Setter
        private Future<Integer> future;

        ClientOnFinish(ClientRunner sClient, SocketServer socketServer, IReader reader) {
            this.sClient = sClient;
            this.socketServer = new WeakReference<>(socketServer);
            this.reader = reader;
            this.future = null;
        }


        @Override
        public void run() {
            try {
                int i = 20;
                while (this.future == null && i-- > 0){
                    Thread.sleep(100);
                }
                if (this.future == null) throw new RuntimeException("future is null");
                // Wait for the client task to complete (with a timeout of 60 minutes)
//                this.future.get(60, TimeUnit.MINUTES);
                this.reader.end(this.sClient.getClientId());
            } catch (Exception e) {
                this.reader.error(this.sClient.getClientId(), e);
                log.error("ClientOnFinish.run. Error waiting for client task to complete: {}", e.getMessage(), e);
            } finally {
                // Clean up resources after the client disconnects
                this.sClient.close();
                Objects.requireNonNull(this.socketServer.get()).close(sClient.getClientId());
                log.debug("ClientOnFinish.run. Client connection closed, Client ID: {}", sClient.getClientId());
            }
        }
    }

    public interface LoadReader {
        IReader loadReader(long clientId);
    }

}
