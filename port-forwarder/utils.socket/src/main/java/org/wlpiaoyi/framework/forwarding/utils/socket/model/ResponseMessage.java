package org.wlpiaoyi.framework.forwarding.utils.socket.model;

import lombok.Getter;
import lombok.Setter;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应消息实体（response → request）。
 * <p>
 * 在 {@link Message} 固定头（10 字节）基础上仅扩展 data 字段，结构更简洁：
 * <pre>
 * | 字段    | 长度 | 说明                  |
 * |---------|------|-----------------------|
 * | dataLen | 2 B  | 加密数据长度          |
 * | data    | N B  | RSA+AES 加密后的数据  |
 * </pre>
 * 回传时无需再次携带目标地址，因为 request 侧通过 clientId 即可识别所属连接。
 * </p>
 */
public class ResponseMessage extends Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 加密后的业务数据 */
    @Getter
    @Setter
    private byte[] data;

    public ResponseMessage(int id) {
        super((byte) 1);
        super.id = id;
        super.generateKey();
    }

    /**
     * 将当前消息对象序列化为字节数组。
     * <p>
     * 完整格式：{@code [Message头(10B)] + [dataLen(2B) + data]}
     * </p>
     *
     * @param bytes  目标字节数组（需保证长度足够）
     * @param offset 起始写入偏移量（若为负数则视为 0）
     * @return 实际写入的字节数（即消息总长度）
     */
    /**
     * 将当前消息对象序列化为字节数组。
     * <p>
     * 完整格式：{@code [Message头(10B)] + [dataLen(2B) + data]}
     * </p>
     *
     * @param bytes  目标字节数组（需保证长度足够）
     * @param offset 起始写入偏移量（若为负数则视为 0）
     * @return 实际写入的字节数（即消息总长度）
     */
    public int toBytes(byte[] bytes, int offset) {
        if (offset < 0) offset = 0;
        int start = offset;

        int bodyLen = data.length;
        if (bodyLen > 65535) {
            throw new IllegalArgumentException("Data length exceeds 65535 bytes");
        }
        int totalLen = bodyLen + 2;
        offset += super.toBytes(bytes, offset, totalLen);
        ValueUtils.longToBytes(bodyLen, bytes, offset, 2);
        offset += 2;
        System.arraycopy(this.data, 0, bytes, offset, bodyLen);
        offset += bodyLen;
        return offset - start;  // 返回总写入字节数
    }

    /**
     * 从字节数组中解析并填充当前消息对象。
     *
     * @param bytes  源字节数组
     * @param offset 起始读取偏移量（若为负数则视为 0）
     * @return 实际读取的字节数（即消息总长度）
     */
    @Override
    public int formatBytes(byte[] bytes, int offset) {
        if (offset < 0) offset = 0;
        int start = offset;
        offset += super.formatBytes(bytes, offset);
        // 读取 data 长度和内容
        this.setData(null);
        if (offset >= bytes.length) return offset - start;
        int dataLen = (int) ValueUtils.byteToLong(bytes, offset, 2);
        if (dataLen == 0) return offset - start;
        offset += 2;
        this.data = new byte[dataLen];
        for (int i = 0; i < dataLen; i++) {
            this.data[i] = bytes[offset++];
        }
        return offset - start;  // 返回实际读取的总字节数
    }
}
