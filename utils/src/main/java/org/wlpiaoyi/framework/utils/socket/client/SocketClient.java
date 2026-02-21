package org.wlpiaoyi.framework.utils.socket.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.socket.Builder;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.IWriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.Future;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * TODO
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2026/2/15 22:58</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
@Slf4j
public class SocketClient{

    private final int bufferSize;

    @Getter
    private final String host;

    @Getter
    private final int port;

    @Getter
    private final int timeOut;

    // Unique identifier for this client
    @Getter
    private final int clientId;

    private Socket socket;

    // Interface for reading data from the client
    private final IReader reader;

    @Getter
    private IWriter writer;

    public SocketClient(String host, int port, int timeOut, int clientId, int bufferSize, IReader iReader){
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.reader = iReader;
        this.bufferSize = bufferSize;
        this.timeOut = timeOut;
    }

    public void connect() throws IOException {
//        log.debug("SocketClient.connect. Connecting to server {}:{} clientId:{}", this.host, this.port, this.clientId);
        if(this.socket != null && !this.socket.isClosed()){
            log.warn("SocketClient.connect. The socket is already connected. Disconnecting and reconnecting...");
            return;
        }
        socket = new Socket(this.host, this.port);
//        socket.setSoTimeout(this.timeOut * 1000);
        // Interface for writing data to the client
        this.writer = Builder.getWriter(this.socket.getOutputStream(), this.socket.getInetAddress().getHostAddress(), this.getPort());
        this.reader.begin(this.clientId, socket.getInetAddress().getHostAddress(), socket.getPort());
//        log.debug("SocketClient.connect. Connected to server {}:{} clientId:{}", socket.getInetAddress(), socket.getPort(), this.clientId);
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     * 
     * <p><b>{@code @param:}</b> <b>onFinishCallback</b>
     * {@link Runnable}
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/17 12:55</p
     * <p><b>{@code @return:}</b>{@link Future<Integer>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Future<Integer> asyncRun(java.lang.Runnable onFinishCallback){
        return Builder.getThreadPool().submit((taskId, param) -> syncRun(param), onFinishCallback);
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     * 
     * <p><b>{@code @param:}</b> <b>onFinishCallback</b>
     * {@link Runnable}
     * </p>
     *
     * <p><b>{@code @date:}</b>2026/2/17 12:55</p>
     * <p><b>{@code @return:}</b>{@link Integer}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Integer syncRun(java.lang.Runnable onFinishCallback){
        // 使用 try-with-resources 确保 Socket 和相关流自动关闭
        try {
//            log.debug("SocketClient.run. Connected to server {}:{} clientId:{}", socket.getInetAddress(), socket.getPort(), this.clientId);
            int readLen;
            byte[] readBytes = new byte[this.bufferSize];
            final InputStream in = socket.getInputStream();
            if(reader.begin(clientId, socket.getInetAddress().getHostAddress(), socket.getPort()) == -1){
                log.warn("SocketServer.start. Reader begin failed for client: {}", clientId);
                this.disConnect();
                return 0;
            }
            if(this.reader.read(this.writer, this.clientId, readBytes, 0) == -1) return 0;
            while (true){
//                if(!Thread.currentThread().isInterrupted()){
//                    log.warn("SocketClient.run. Error occurred while reading from server {}:{} clientId:{}", socket.getInetAddress(), socket.getPort(), this.clientId);
//                }
                readLen = in.read(readBytes, 0, readBytes.length);
                if (readLen == -1) {
                    log.warn("SocketClient.run. Server {}:{} clientId:{} disconnected", socket.getInetAddress(), socket.getPort(), this.clientId);
                    break;
                }
                if(this.reader.read(this.writer, this.clientId, readBytes, readLen) == -1) break;
            }
//            log.debug("SocketClient.run. Disconnected from server {}:{} clientId:{}", socket.getInetAddress(), socket.getPort(), this.clientId);
        } catch (Exception e) {
            this.reader.error(this.clientId, e);
            log.error("SocketClient.run. Error occurred while connecting to server {}:{} clientId:{}", this.host, this.port, this.clientId, e);
        }finally {
            reader.end(this.clientId);
            this.disConnect();
            this.reader.end(this.clientId);
            if(onFinishCallback != null){
                onFinishCallback.run();
            }
        }
        return 0;
    }
    public void disConnect(){
        log.debug("SocketClient.disConnect. Disconnecting client with ID: {}", this.clientId);
        try {
            if(!this.socket.isInputShutdown()){
                this.socket.shutdownInput();
            }
        } catch (IOException e) {
            log.error("ClientRunner.close. Error occurred while closing socket.in", e);
        }
        try {
            if(!this.socket.isOutputShutdown()){
                this.socket.getOutputStream().flush();
                this.socket.shutdownOutput();
            }
        } catch (IOException e) {
            log.error("ClientRunner.close. Error occurred while closing socket.out", e);
        }
        try {
//            log.debug("ClientRunner.close. Attempting to close connection for Client ID: {}", this.clientId);
            if (!socket.isClosed()) {
                socket.close();
//                log.debug("ClientRunner.close. Successfully closed connection for Client ID: {}", this.clientId);
            }
        } catch (IOException e) {
            log.error("ClientRunner.close. Error occurred while closing connection for Client ID: {}", this.clientId, e);
        }
        this.socket = null;

    }
}
