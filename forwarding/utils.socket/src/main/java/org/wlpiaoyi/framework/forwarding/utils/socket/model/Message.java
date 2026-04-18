package org.wlpiaoyi.framework.forwarding.utils.socket.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

/**
 * 转发框架自定义二进制消息协议的基类。
 * <p>
 * 消息头固定格式（共 10 字节）：
 * <pre>
 * | 字段 | 长度 | 说明                |
 * |------|------|---------------------|
 * | len  | 4 B  | 消息总长度（大端序）|
 * | id   | 4 B  | 消息唯一标识        |
 * | key  | 1 B  | 校验键（id % seed） |
 * | type | 1 B  | 消息类型            |
 * </pre>
 * 子类 {@link RequestMessage}（type=2）和 {@link ResponseMessage}（type=1）在此基础上扩展消息体。
 * </p>
 */
@EqualsAndHashCode
class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Random random = new Random();

    /** 用于生成 key 的种子数组 */
    private static final byte[] SEEDS = new byte[]{0x22, 0x55, 0x33, 0x66};

    /** 消息总长度（字节数），由 toBytes 方法自动设置，仅用于读取时校验 */
    @Getter
    protected int len;

    /** 消息唯一标识符 */
    @Getter
    protected int id;

    /** 校验键，由 id 和种子生成，用于简单验证消息有效性 */
    @Getter
    private byte key;

    /** 消息类型（业务自定义：1=ResponseMessage, 2=RequestMessage） */
    @Getter
    private byte type;

    public Message(byte type) {
        this.type = type;
        this.generateKey();
    }

    /**
     * 检查 key 是否正确（即 key 等于 id 对任一种子取模的结果）。
     *
     * @return 如果 key 匹配任一种子则返回 true，否则 false
     */
    public boolean check() {
        for (byte seed : SEEDS) {
            if (this.key == this.id % seed) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据 id 随机选择一个种子生成 key。
     */
    protected void generateKey() {
        this.key = (byte) (this.id % SEEDS[random.nextInt(SEEDS.length)]);
    }

    /**
     * 将 len 字段写入字节数组（4 字节，大端序）。
     *
     * @param bytes  目标字节数组
     * @param offset 起始偏移量
     * @return 写入的字节数（始终为 4）
     */
    private int writeLen(byte[] bytes, int offset) {
        ValueUtils.longToBytes(this.len, bytes, offset, 4);
        return 4;
    }

    /**
     * 从字节数组读取 len 字段（4 字节，大端序）。
     *
     * @param bytes  源字节数组
     * @param offset 起始偏移量
     * @return 读取的字节数（始终为 4）
     */
    private int readLen(byte[] bytes, int offset) {
        this.len = (short) ValueUtils.byteToLong(bytes, offset, 4);
        return 4;
    }

    /**
     * 将 id 字段写入字节数组（4 字节，大端序）。
     *
     * @param bytes  目标字节数组
     * @param offset 起始偏移量
     * @return 写入的字节数（始终为 4）
     */
    private int writeId(byte[] bytes, int offset) {
        ValueUtils.longToBytes(this.id, bytes, offset, 4);
        return 4;
    }

    /**
     * 从字节数组读取 id 字段（4 字节，大端序）。
     *
     * @param bytes  源字节数组
     * @param offset 起始偏移量
     * @return 读取的字节数（始终为 4）
     */
    private int readId(byte[] bytes, int offset) {
        this.id = (int) ValueUtils.byteToLong(bytes, offset, 4);
        return 4;
    }

    /**
     * 将当前消息对象序列化为字节数组。
     * <p>
     * 此方法会自动计算消息总长度并填充 len 字段，然后按固定格式写入所有字段。
     * 如果提供的字节数组容量不足，会抛出 ArrayIndexOutOfBoundsException。
     * </p>
     *
     * @param bytes   目标字节数组（需保证长度足够）
     * @param offset  起始写入偏移量（若为负数则视为 0）
     * @param bodyLen 消息体长度（不含固定头）
     * @return 实际写入的字节数（即消息总长度）
     */
    public int toBytes(byte[] bytes, int offset, int bodyLen) {
        if (offset < 0) offset = 0;
        int start = offset;
        // 计算消息总长度并设置 len
        int totalLen = 4   // len 自身
                + 4        // id
                + 1        // key
                + 1        // type
                + bodyLen;
        this.len = (short) totalLen;  // 如果 totalLen > 65535，会溢出，但实际场景通常不会

        // 写入各字段
        offset += writeLen(bytes, offset);      // len (4)
        offset += writeId(bytes, offset);       // id (4)
        bytes[offset++] = this.key;             // key (1)
        bytes[offset++] = this.type;            // type (1)
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

        offset += readLen(bytes, offset);        // len (4)
        offset += readId(bytes, offset);         // id (4)
        this.key = bytes[offset++];               // key (1)
        this.type = bytes[offset++];              // type (1)
        return offset - start;  // 返回实际读取的总字节数
    }
}
