package org.wlpiaoyi.framework.forwarding.utils.socket.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='border-radius: 12px; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * TODO
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2026/2/17 14:50</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
public class ResponseMessage extends Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /** 数据 */
    @Getter @Setter
    private byte[] data;

    public ResponseMessage(int id) {
        super((byte) 1);
        super.id = id;
        super.generateKey();
    }

    /**
     * 将当前消息对象序列化为字节数组。
     * <p>
     * 此方法会自动计算消息总长度并填充 len 字段，然后按固定格式写入所有字段。
     * 如果提供的字节数组容量不足，会抛出 ArrayIndexOutOfBoundsException。
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
     * <p>
     * 此方法按照固定格式依次读取各字段，不依赖 len 字段进行数据校验（len 会被读取，但仅用于记录）。
     * 如果提供的字节数组数据不足，会抛出 ArrayIndexOutOfBoundsException。
     * </p>
     *
     * @param bytes  源字节数组
     * @param offset 起始读取偏移量（若为负数则视为 0）
     * @return 实际读取的字节数（即消息总长度）
     */
    public int formatBytes(byte[] bytes, int offset) {
        if (offset < 0) offset = 0;
        int start = offset;
        offset += super.formatBytes(bytes, offset);
        // 读取 data 长度和内容
        this.setData(null);
        if(offset >= bytes.length) return offset - start;
        int dataLen = (int) ValueUtils.byteToLong(bytes, offset, 2);
        if (dataLen == 0) return offset - start;
        offset += 2;
        this.data = new byte[dataLen];
        for (int i = 0; i < dataLen; i++){
            this.data[i] = bytes[offset++];
        }
        offset += dataLen;
        return offset - start;  // 返回实际读取的总字节数
    }
}