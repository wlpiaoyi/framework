package org.wlpiaoyi.framework.utils.socket.client;

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

    // The size of the buffer used for reading data from the client
    private static final int BUFFER_SIZE = 8192;

    @Getter
    private final String host;

    @Getter
    private final int port;

    // Unique identifier for this client
    @Getter
    private final int clientId;

    private Socket socket;

    // Interface for reading data from the client
    private final IReader reader;

    // Interface for writing data to the client
    private IWriter writer;

    public SocketClient(String host, int port, int clientId, IReader iReader){
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.reader = iReader;
    }

    public void connect() throws IOException {
        log.info("SocketClient.connect. Connecting to server {}:{} clientId:{}", this.host, this.port, this.clientId);
        if(this.socket != null && !this.socket.isClosed()){
            log.warn("SocketClient.connect. The socket is already connected. Disconnecting and reconnecting...");
            return;
        }
        socket = new Socket(this.host, this.port);
        log.info("SocketClient.connect. Connected to server {}:{} clientId:{}", socket.getInetAddress(), socket.getPort(), this.clientId);
    }

    public Integer run(java.lang.Runnable onFinishCallback){
        // 使用 try-with-resources 确保 Socket 和相关流自动关闭
        try {
            this.writer = Builder.getWriter(this.socket.getOutputStream());
            InputStream in = socket.getInputStream();
            log.info("SocketClient.run. Connected to server {}:{} clientId:{}", socket.getInetAddress(), socket.getPort(), this.clientId);
            byte[] readBytes = new byte[BUFFER_SIZE];
            int readLen;
            while (!Thread.currentThread().isInterrupted() && (readLen = in.read(readBytes)) != -1){
                this.reader.read(this.writer, this.clientId, readBytes, readLen);
            }
            log.info("SocketClient.run. Disconnected from server {}:{} clientId:{}", socket.getInetAddress(), socket.getPort(), this.clientId);
        } catch (IOException e) {
            log.error("SocketClient.run. Error occurred while connecting to server {}:{} clientId:{}", this.host, this.port, this.clientId, e);
        }finally {
            this.disConnect();
            if(onFinishCallback != null){
                onFinishCallback.run();
            }
        }
        return 0;
    }
    public void disConnect(){
        if (this.socket == null) return;
        if (this.socket.isClosed()) return;
        try {
            socket.getInputStream().close();
        } catch (IOException e) {
            log.info("SocketClient.disConnect. Error occurred while closing socket.in", e);
        }
        try {
            socket.getOutputStream().flush();
            socket.getOutputStream().close();
        } catch (IOException e) {
            log.error("SocketClient.disConnect. Error occurred while closing socket.out", e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            log.error("SocketClient.disConnect. Error occurred while closing socket", e);
        }
        this.socket = null;

    }
}
