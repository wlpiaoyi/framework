package org.wlpiaoyi.framework.forwarding.response;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.forwarding.utils.socket.ForwardUtils;
import org.wlpiaoyi.framework.forwarding.utils.socket.Security;
import org.wlpiaoyi.framework.forwarding.utils.socket.model.ResponseMessage;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.socket.IReader;
import org.wlpiaoyi.framework.utils.socket.IWriter;
import org.wlpiaoyi.framework.utils.socket.client.SocketClient;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2026-02-17 13:04:47</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class ClientReader implements IReader {

    private final IWriter serverWriter;
    private int messageId = 0;
    private final byte[] bufferCaches = new byte[ForwardUtils.MAX_CACHE_SIZE];

    public ClientReader(IWriter serverWriter) {
        this.serverWriter = serverWriter;
    }

    @Override
    public int begin(int clientId, String host, int port) {
        log.debug("ClientReader.begin. ClientId: {}, Host: {}, Port: {}", clientId, host, port);
        return 1;
    }

    @Override
    public synchronized int read(IWriter writer, int clientId, byte[] readBytes, int readLen) {
        log.debug("ClientReader.read. ClientId: {}, ReadLen: {}", clientId, readLen);
        this.messageId++;
        if (this.messageId < 0) this.messageId = 1;
        ResponseMessage message = new ResponseMessage(this.messageId);
        if (this.messageId < 0) this.messageId = 1;
        byte[] data = new byte[readLen];
        System.arraycopy(readBytes, 0, data, 0, readLen);
        // use server forwarding data
        System.out.println("S=================================================>:dMessage:");
        System.out.println(new String(data));
        System.out.println("S=================================================<:dMessage");
        message.setData(SecurityUtils.getSecurity().encrypt(data, 0, data.length));
        System.out.println("S=================================================>:eMessage:");
        System.out.println(new String(message.getData()));
        System.out.println("S=================================================<:eMessage");
        if (!message.check()) throw new RuntimeException("message check error！clientId:" + clientId);
        message.toBytes(this.bufferCaches, 0);
        this.serverWriter.write(clientId, this.bufferCaches, message.getLen());
//        log.debug("ClientReader.read. write len:{} message: {}", message.getLen(), DataUtils.base64Encode(this.bufferCaches));
        return 0;
    }

    @Override
    public void error(int clientId, Exception e) {
        log.error("ClientReader.error. ClientId: {}", clientId, e);
    }

    @Override
    public void end(int clientId) {
        log.debug("ClientReader.end. ClientId: {}", clientId);
    }
}
