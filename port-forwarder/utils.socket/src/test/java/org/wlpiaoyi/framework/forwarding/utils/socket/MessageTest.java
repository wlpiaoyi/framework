//package org.wlpiaoyi.framework.forwarding.utils.socket;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.wlpiaoyi.framework.forwarding.utils.socket.model.Message;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Random;
//
//import static org.junit.Assert.*;
//
///**
// * {@link Message} 的单元测试类。
// * <p>
// * 测试内容包括：
// * <ul>
// *     <li>序列化与反序列化的一致性</li>
// *     <li>密钥生成与校验逻辑</li>
// *     <li>主机名字段长度超限异常</li>
// *     <li>消息总长度（len）的正确计算</li>
// *     <li>多种属性组合下的正确性</li>
// * </ul>
// * </p>
// *
// * @author wlpiaoyi
// * @version 1.0
// * @date 2026-02-16 19:10:59
// */
//@Slf4j
//public class MessageTest {
//
//    @Before
//    public void setUp() {
//        log.info("开始测试 Message 类...");
//    }
//
//    @After
//    public void tearDown() {
//        log.info("测试结束。");
//    }
//
//    /**
//     * 测试序列化和反序列化的一致性。
//     * <p>
//     * 构造一个完整的 Message 对象，转换为字节数组，再重新解析，
//     * 验证所有字段（包括自动生成的 key 和 len）是否与原始对象一致。
//     * </p>
//     */
//    @Test
//    public void testSerialization() {
//        // 构造原始消息
//        Message original = new Message();
//        original.setId(123456);
//        original.setType((byte) 0x01);
//        original.setHost("localhost");
//        original.setPort(8080);
//        original.generateKey(); // 生成密钥
//
//        // 序列化
//        byte[] buffer = new byte[1024]; // 提供足够大的缓冲区
//        int written = original.toBytes(buffer, 0);
//        assertTrue("写入字节数应为正数", written > 0);
//        assertEquals("写入字节数应与消息总长度一致", original.getLen(), written);
//
//        // 反序列化到新对象
//        Message deserialized = new Message();
//        int read = deserialized.formatBytes(buffer, 0);
//        assertEquals("读取字节数应与写入字节数一致", written, read);
//
//        // 验证所有字段
//        assertEquals("ID 不一致", original.getId(), deserialized.getId());
//        assertEquals("密钥不一致", original.getKey(), deserialized.getKey());
//        assertEquals("类型不一致", original.getType(), deserialized.getType());
//        assertEquals("主机名不一致", original.getHost(), deserialized.getHost());
//        assertEquals("端口不一致", original.getPort(), deserialized.getPort());
//        assertEquals("消息总长度不一致", original.getLen(), deserialized.getLen());
//        assertTrue("反序列化后的密钥校验应通过", deserialized.check());
//    }
//
//    /**
//     * 测试密钥生成与校验方法。
//     * <p>
//     * 验证 {@link Message#generateKey()} 生成的密钥能通过 {@link Message#check()} 校验，
//     * 而手动篡改密钥或 ID 后校验失败。
//     * </p>
//     */
//    @Test
//    public void testKeyGenerationAndCheck() {
//        Message msg = new Message();
//        msg.setId(100);
//        msg.generateKey();
//
//        // 校验应通过
//        assertTrue("正确生成的密钥应通过校验", msg.check());
//
//        // 修改 ID，校验应失败
//        msg.setId(101);
//        assertFalse("ID 改变后密钥不应通过校验", msg.check());
//
//        // 恢复 ID 并修改密钥，校验应失败
//        msg.setId(100);
//        msg.key = (byte) 0x00;
//        assertFalse("密钥被篡改后不应通过校验", msg.check());
//
//        // 测试多个随机 ID 的密钥生成
//        Random rand = new Random();
//        for (int i = 0; i < 100; i++) {
//            msg.setId(rand.nextInt(10000));
//            msg.generateKey();
//            assertTrue("随机 ID 生成的密钥应通过校验", msg.check());
//        }
//    }
//
//    /**
//     * 测试主机名字段长度超过限制时抛出异常。
//     * <p>
//     * 构造一个超长的主机名字符串（UTF-8 字节数超过 65535），
//     * 验证 {@link Message#toBytes(byte[], int)} 抛出 {@link IllegalArgumentException}。
//     * </p>
//     */
//    @Test(expected = IllegalArgumentException.class)
//    public void testHostTooLong() {
//        Message msg = new Message();
//        msg.setId(1);
//        msg.setType((byte) 0);
//        msg.setPort(1234);
//
//        // 构造一个长度超过 65535 字节的字符串
//        // 每个中文字符在 UTF-8 中占 3 字节，构造 22000 个中文字符即可超过 65535
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < 22000; i++) {
//            sb.append("测");
//        }
//        String longHost = sb.toString();
//        assertTrue("构造的字符串 UTF-8 长度应超过 65535",
//                longHost.getBytes(StandardCharsets.UTF_8).length > 65535);
//
//        msg.setHost(longHost);
//        msg.generateKey();
//
//        // 应抛出 IllegalArgumentException
//        msg.toBytes(new byte[1024 * 1024], 0);
//    }
//
//    /**
//     * 测试消息总长度（len）字段的计算是否正确。
//     * <p>
//     * 对于不同的主机名字符串，验证 toBytes 后 len 是否等于固定部分长度加上主机名 UTF-8 字节长度。
//     * 固定部分长度 = 2(len) + 4(id) + 1(key) + 1(type) + 2(host长度) + 4(port) = 14 字节。
//     * </p>
//     */
//    @Test
//    public void testMessageLengthCalculation() {
//        Message msg = new Message();
//        msg.setId(0x12345678);
//        msg.setType((byte) 0x7F);
//        msg.setPort(65535);
//        msg.generateKey();
//
//        // 测试不同主机名字符串
//        String[] hosts = {"", "localhost", "192.168.1.1", "这是一个测试主机名", "🌟🔥"};
//        for (String host : hosts) {
//            msg.setHost(host);
//            msg.generateKey(); // 重新生成 key（可选）
//
//            byte[] buffer = new byte[1024];
//            int written = msg.toBytes(buffer, 0);
//
//            // 计算预期长度：固定 14 + host UTF-8 字节长度
//            int expectedLen = 14 + host.getBytes(StandardCharsets.UTF_8).length;
//            assertEquals("消息总长度计算错误", expectedLen, written);
//            assertEquals("消息总长度字段设置错误", expectedLen, msg.getLen());
//
//            // 读取缓冲区中的 len 字段（前两个字节），验证是否与预期一致
//            int lenFromBuffer = ((buffer[0] & 0xFF) << 8) | (buffer[1] & 0xFF);
//            assertEquals("缓冲区中的长度字段错误", expectedLen, lenFromBuffer);
//        }
//    }
//
//    /**
//     * 测试多种属性组合下的正确性。
//     * <p>
//     * 随机生成多组属性值，验证序列化/反序列化后所有字段保持一致。
//     * </p>
//     */
//    @Test
//    public void testDifferentValues() {
//        Random rand = new Random();
//        for (int i = 0; i < 50; i++) {
//            Message original = new Message();
//            original.setId(rand.nextInt());
//            original.setType((byte) rand.nextInt(256));
//            original.setHost(generateRandomHost(rand));
//            original.setPort(rand.nextInt(65536)); // 0~65535
//            original.generateKey();
//
//            byte[] buffer = new byte[1024];
//            int written = original.toBytes(buffer, 0);
//
//            Message deserialized = new Message();
//            int read = deserialized.formatBytes(buffer, 0);
//            assertEquals(written, read);
//
//            // 逐字段比较
//            assertEquals(original.getId(), deserialized.getId());
//            assertEquals(original.getKey(), deserialized.getKey());
//            assertEquals(original.getType(), deserialized.getType());
//            assertEquals(original.getHost(), deserialized.getHost());
//            assertEquals(original.getPort(), deserialized.getPort());
//            assertEquals(original.getLen(), deserialized.getLen());
//        }
//    }
//
//    /**
//     * 生成一个随机主机名字符串（由 ASCII 字母、数字、点和中文组成）。
//     *
//     * @param rand 随机数生成器
//     * @return 随机主机名
//     */
//    private String generateRandomHost(Random rand) {
//        int length = rand.nextInt(50) + 1; // 1~50 字符
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < length; i++) {
//            int type = rand.nextInt(3);
//            switch (type) {
//                case 0: // 小写字母
//                    sb.append((char) ('a' + rand.nextInt(26)));
//                    break;
//                case 1: // 数字
//                    sb.append((char) ('0' + rand.nextInt(10)));
//                    break;
//                case 2: // 中文（随机一个常用汉字范围）
//                    sb.append((char) (0x4e00 + rand.nextInt(0x9fff - 0x4e00 + 1)));
//                    break;
//            }
//        }
//        return sb.toString();
//    }
//}