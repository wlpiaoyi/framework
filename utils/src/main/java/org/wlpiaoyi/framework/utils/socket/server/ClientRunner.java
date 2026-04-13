package org.wlpiaoyi.framework.utils.socket.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.socket.Builder;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.IWriter;
import org.wlpiaoyi.framework.utils.thread.Runnable;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * <p><b>{@code @author:}</b> wlpia</p>
 * <p><b>{@code @description:}</b> Represents a client connection managed by the socket server.</p>
 * <p><b>{@code @date:}</b> 2026-02-15 18:42:08</p>
 * <p><b>{@code @version:}:</b> 1.0</p>
 */
@Slf4j
class ClientRunner implements Runnable<java.lang.Runnable, Integer> {


    private final int bufferSize;

    // Unique identifier for this client
    @Getter
    private final int clientId;

    // The underlying socket connection for this client
    private final Socket sClient;

    // Interface for reading data from the client
    private final IReader reader;

    // Interface for writing data to the client
    private final IWriter writer;

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Constructs a new SocketClient instance with the given socket, client ID, and reader interface.
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>sClient</b>
     * {@link Socket} The socket representing the client connection.
     * </p>
     *
     * <p><b>{@code @param}</b> <b>clientId</b>
     * {@link int} The unique identifier assigned to this client.
     * </p>
     *
     * <p><b>{@code @param}</b> <b>iClientReader</b>
     * {@link IReader} The interface used to read data from the client.
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 21:48</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    ClientRunner(Socket sClient, int clientId, IReader iReader, int bufferSize) throws IOException {
        this.sClient = sClient;
        this.clientId = clientId;
        this.reader = iReader;
        this.writer = Builder.getWriter(this.sClient, this.sClient.getInetAddress().getHostAddress(), this.sClient.getPort());
        this.bufferSize = bufferSize;
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Reads data from the client and processes it using the provided reader interface.
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>taskId</b>
     * {@link String} The task identifier (not used in this implementation).
     * </p>
     *
     * <p><b>{@code @param}</b> <b>onFinishCallback</b>
     * {@link java.lang.Runnable} Additional parameters (not used in this implementation).
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 21:48</p>
     * <p><b>{@code @return:}</b>{@link Integer} Returns 0 upon completion.
     * </p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    @Override
    public Integer run(String taskId, java.lang.Runnable onFinishCallback) throws Exception {
        try {
            log.debug("ClientRunner.run. Starting data reception for Client ID: {}", this.clientId);
            InputStream in = this.sClient.getInputStream();
            byte[] readBytes = new byte[this.bufferSize];
            int readLen = 0;
            if(this.reader.read(this.writer, this.clientId, readBytes, readLen) == -1) return -1;
            while (true) {
//                if(!Thread.currentThread().isInterrupted()){
//                    log.warn("ClientRunner.run. Error occurred while reading from server {}:{} clientId:{}", this.sClient.getInetAddress(), this.sClient.getPort(), this.clientId);
////                    break;
//                }
                readLen = in.read(readBytes);
                if (readLen == -1) {
                    log.warn("ClientRunner.run. Server {}:{} clientId:{} disconnected", this.sClient.getInetAddress(), this.sClient.getPort(), this.clientId);
                    break;
                }
                if (log.isDebugEnabled()) {
                    log.debug("[fw-tcp] ingress-read localPort={} clientId={} peer={}:{} chunkLen={}",
                            this.sClient.getLocalPort(), this.clientId,
                            this.sClient.getInetAddress().getHostAddress(), this.sClient.getPort(), readLen);
                }
                if(this.reader.read(this.writer, this.clientId, readBytes, readLen) == -1) break;
            }
            log.debug("ClientRunner.run. Data reception completed for Client ID: {}", this.clientId);
        } catch (Exception e) {
            if (e.getMessage().contains("Socket closed")) {
                log.debug("ClientRunner.run. Socket closed for Client ID: {}", this.clientId);
            } else {
                log.error("ClientRunner.run. Error occurred while receiving data for Client ID: {}", this.clientId, e);
            }
            log.error("ClientRunner.run. Unexpected error for Client ID: {}", this.clientId, e);
        } finally {
            close();
            if (onFinishCallback != null) {
                onFinishCallback.run();
            }
        }
        return 0;
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * Closes the client connection and releases associated resources.
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/15 21:48</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    void close() {
        log.debug("ClientRunner.close. Closing connection for Client ID: {}", this.clientId);
        try {
            if(!this.sClient.isInputShutdown()){
                this.sClient.shutdownInput();
            }
        } catch (IOException e) {
            log.debug("ClientRunner.close. Error occurred while closing socket.in", e);
        }
        try {
            if(!this.sClient.isOutputShutdown()){
                this.sClient.getOutputStream().flush();
                this.sClient.shutdownOutput();
            }
        } catch (IOException e) {
            log.error("ClientRunner.close. Error occurred while closing socket.out", e);
        }
        try {
            if (!sClient.isClosed()) {
                sClient.close();
            }
        } catch (IOException e) {
            log.error("ClientRunner.close. Error occurred while closing connection for Client ID: {}", this.clientId, e);
        }
    }

}
