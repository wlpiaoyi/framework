package org.wlpiaoyi.framework.forwarding.utils.socket.model;

import lombok.Getter;
import lombok.Setter;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.Serial;
import java.nio.charset.StandardCharsets;

/**
 * 请求消息实体（request → response）。
 * <p>
 * 在 {@link Message} 固定头（10 字节）基础上扩展以下字段：
 * <pre>
 * | 字段    | 长度 | 说明                  |
 * |---------|------|-----------------------|
 * | hostLen | 2 B  | 目标主机字符串长度    |
 * | host    | N B  | 目标主机（UTF-8）     |
 * | port    | 4 B  | 目标端口（大端序）    |
 * | dataLen | 2 B  | 加密数据长度          |
 * | data    | N B  | RSA+AES 加密后的数据  |
 * </pre>
 * </p>
 */
public class RequestMessage extends Message implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 目标主机名或 IP */
    @Getter
    @Setter
    private String host;

    /** 目标端口号 */
    @Getter
    @Setter
    private int port;

    /** 加密后的业务数据 */
    @Getter
    @Setter
    private byte[] data;

    public RequestMessage(int id) {
        super((byte) 2);
        super.id = id;
        super.generateKey();
    }

    /**
     * 将 host 字段写入字节数组（先写 2 字节长度，再写 UTF-8 字节）。
     *
     * @param bytes  目标字节数组
     * @param offset 起始偏移量
     * @return 写入的总字节数（2 + 字符串长度）
     * @throws IllegalArgumentException 如果字符串长度超过 65535
     */
    private int writeHost(byte[] bytes, int offset) {
        byte[] hostBytes = this.host.getBytes(StandardCharsets.UTF_8);
        int len = hostBytes.length;
        if (len > 65535) {
            throw new IllegalArgumentException("Host length exceeds 65535 bytes");
        }
        ValueUtils.longToBytes(this.len, bytes, offset, 2);
        offset += 2;
        System.arraycopy(hostBytes, 0, bytes, offset, len);
        return 2 + len;
    }

    /**
     * 从字节数组读取 host 字段（先读 2 字节长度，再读相应字节的 UTF-8 字符串）。
     *
     * @param bytes  源字节数组
     * @param offset 起始偏移量
     * @return 读取的总字节数（2 + 字符串长度）
     */
    private int readHost(byte[] bytes, int offset) {
        int len = (int) ValueUtils.byteToLong(bytes, offset, 2);
        offset += 2;
        this.host = new String(bytes, offset, len, StandardCharsets.UTF_8);
        return 2 + len;
    }

    /**
     * 将 port 字段写入字节数组（4 字节，大端序）。
     *
     * @param bytes  目标字节数组
     * @param offset 起始偏移量
     * @return 写入的字节数（始终为 4）
     */
    private int writePort(byte[] bytes, int offset) {
        ValueUtils.longToBytes(this.port, bytes, offset, 4);
        return 4;
    }

    /**
     * 从字节数组读取 port 字段（4 字节，大端序）。
     *
     * @param bytes  源字节数组
     * @param offset 起始偏移量
     * @return 读取的字节数（始终为 4）
     */
    private int readPort(byte[] bytes, int offset) {
        this.port = (int) ValueUtils.byteToLong(bytes, offset, 4);
        return 4;
    }

    /**
     * 将当前消息对象序列化为字节数组。
     * <p>
     * 完整格式：{@code [Message头(10B)] + [hostLen(2B) + host] + [port(4B)] + [dataLen(2B) + data]}
     * </p>
     *
     * @param bytes  目标字节数组（需保证长度足够）
     * @param offset 起始写入偏移量（若为负数则视为 0）
     * @return 实际写入的字节数（即消息总长度）
     */
    /**
     * 将当前消息对象序列化为字节数组。
     * <p>
     * 完整格式：{@code [Message头(10B)] + [hostLen(2B) + host] + [port(4B)] + [dataLen(2B) + data]}
     * </p>
     *
     * @param bytes  目标字节数组（需保证长度足够）
     * @param offset 起始写入偏移量（若为负数则视为 0）
     * @return 实际写入的字节数（即消息总长度）
     */
    public int toBytes(byte[] bytes, int offset) {
        if (offset < 0) offset = 0;
        int start = offset;
        // 预先获取 host 字节以计算总长度
        byte[] hostBytes = this.host.getBytes(StandardCharsets.UTF_8);
        int hostLen = hostBytes.length;
        if (hostLen > 65535) {
            throw new IllegalArgumentException("Host length exceeds 65535 bytes");
        }
        int bodyLen = 0;
        if (this.data != null) bodyLen = data.length;
        if (bodyLen > 65535) {
            throw new IllegalArgumentException("Data length exceeds 65535 bytes");
        }

        // 计算消息体长度（不含 Message 固定头）
        int totalLen = 2 + hostLen  // host 长度 + host 数据
                + 4;       // port
        if (bodyLen > 0) totalLen += (2 + bodyLen);
        offset += super.toBytes(bytes, offset, totalLen);

        // 写入 host 长度和内容
        ValueUtils.longToBytes(hostLen, bytes, offset, 2);
        offset += 2;
        System.arraycopy(hostBytes, 0, bytes, offset, hostLen);
        offset += hostLen;

        offset += writePort(bytes, offset);     // port (4)

        if (bodyLen > 0) {
            // 写入 data 长度和内容
            ValueUtils.longToBytes(bodyLen, bytes, offset, 2);
            offset += 2;
            System.arraycopy(this.data, 0, bytes, offset, bodyLen);
            offset += bodyLen;
        }
        return offset - start;  // 返回总写入字节数
    }

    /**
     * 从字节数组中解析并填充当前消息对象。
     *
     * @param bytes 源字节数组
     * @param off   起始读取偏移量（若为负数则视为 0）
     * @return 实际读取的字节数（即消息总长度）
     */
    @Override
    public int formatBytes(byte[] bytes, int off) {
        if (off < 0) off = 0;
        int start = off;
        off += super.formatBytes(bytes, off);

        // 读取 host 长度和内容
        int hostLen = (int) ValueUtils.byteToLong(bytes, off, 2);
        off += 2;
        this.host = new String(bytes, off, hostLen, StandardCharsets.UTF_8);
        off += hostLen;

        off += readPort(bytes, off);        // port (4)

        // 读取 data 长度和内容
        this.setData(null);
        if (bytes.length <= off) return off - start;
        int dataLen = (int) ValueUtils.byteToLong(bytes, off, 2);
        if (dataLen == 0) return off - start;
        off += 2;
        this.data = new byte[dataLen];
        for (int i = 0; i < dataLen; i++) {
            this.data[i] = bytes[off++];
        }
        return off - start;  // 返回实际读取的总字节数
    }
}
