package org.wlpiaoyi.framework.utils.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class DataBaseXUtilsTest {

    private final char[] CHAR10_ARRAY = "0123456789".toCharArray();
    private final char[] CHAR16_ARRAY = "0123456789abcdef".toCharArray();
    private final char[] CHAR64_ARRAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Before
    public void setUp() throws Exception {
        log("开始执行DataBaseXUtils测试");
    }
    /**
     * 测试进制转换功能 - 10进制转16进制（大数据量）
     */
    @Test
    public void testConversionToBaseX_DecimalToHex_LargeData() {
        log("=== 测试10进制转16进制功能（大数据量） ===");

        // 测试10进制转换到16进制 - 使用超过100个字符的数据
        String largeDecimalString = "987654321012345678901234567890123456789012345678901234567890" +
                "987654321012345678901234567890123456789012345678901234567890" +
                "987654321012345678901234567890123456789012345678901234567890" +
                "987654321012345678901234567890123456789012345678901234567890" +
                "987654321012345678901234567890123456789012345678901234567890" +
                "987654321012345678901234567890123456789012345678901234567890" +
                "987654321012345678901234567890123456789012345678901234567890" +
                "987654321012345678901234567890123456789012345678901234567890" +
                "987654321012345678901234567890123456789012345678901234567890";
        byte[] decimalBytes = largeDecimalString.getBytes();
        log("原始10进制数据长度: " + decimalBytes.length + ", 数据: " + largeDecimalString);
        byte[] hexBytes = DataBaseXUtils.conversionToBaseX(decimalBytes, CHAR10_ARRAY, CHAR16_ARRAY);
        log("转换后16进制数据长度: " + hexBytes.length + ", 数据: " + new String(hexBytes));

        assertNotNull("10进制转16进制转换结果不应为null", hexBytes);
        assertTrue("10进制转16进制转换结果不应为空", hexBytes.length > 0);

        // 验证转换结果的合理性（通过反向转换验证）
        byte[] backToDecimal = DataBaseXUtils.conversionToBaseX(hexBytes, CHAR16_ARRAY, CHAR10_ARRAY);
        String backToDecimalString = new String(backToDecimal);
        log("反向转换回10进制数据长度: " + backToDecimal.length + ", 数据: " + backToDecimalString);
        assertArrayEquals("10进制与16进制双向转换应保持一致性", decimalBytes, backToDecimal);

        log("10进制转16进制功能测试通过");
    }

    /**
     * 测试进制转换功能 - 16进制转64进制
     */
    @Test
    public void testConversionToBaseX_HexToBase64() {
        log("=== 测试16进制转64进制功能 ===");

        // 测试16进制转换到64进制 - 使用超过100个字符的数据
        String largeHexString = "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789" +
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789" +
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789" +
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789" +
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789" +
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789" +
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789" +
                "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789";
        byte[] hexBytes = largeHexString.getBytes();
        log("原始16进制数据长度: " + hexBytes.length + ", 数据: " + largeHexString);
        byte[] base64Bytes = DataBaseXUtils.conversionToBaseX(hexBytes, CHAR16_ARRAY, CHAR64_ARRAY);
        log("转换后64进制数据长度: " + base64Bytes.length + ", 数据: " + new String(base64Bytes));

        assertNotNull("16进制转64进制转换结果不应为null", base64Bytes);
        assertTrue("16进制转64进制转换结果不应为空", base64Bytes.length > 0);

        // 验证转换结果的合理性（通过反向转换验证）
        byte[] backToHex = DataBaseXUtils.conversionToBaseX(base64Bytes, CHAR64_ARRAY, CHAR16_ARRAY);
        String backToHexString = new String(backToHex);
        log("反向转换回16进制数据长度: " + backToHex.length + ", 数据: " + backToHexString);
        assertArrayEquals("16进制与64进制双向转换应保持一致性", hexBytes, backToHex);

        log("16进制转64进制功能测试通过");
    }

    /**
     * 测试进制转换功能 - 256进制转64进制
     */
    @Test
    public void testConversionToBase256_HexToBase64() {
        log("=== 测试256进制转64进制功能 ===");

        // 测试16进制转换到64进制 - 使用超过100个字符的数据
        String largeHexString = "；安排维卡史蒂夫卡爱迪生的饭卡上；看傻傻的开发扣税的；爱看书的地方卡萨斯的看法；看" +
                "阿道夫那位加哦就diekemdjd的卡我很渴望哈开发商大家那时的爱上对方家里就安慰两句啊发生的放假咯了";
        byte[] dataBytes = largeHexString.getBytes();
        log("原始256进制数据长度: " + dataBytes.length + ", 数据: " + largeHexString);
        byte[] base64Bytes = DataBaseXUtils.conversionToBaseX(dataBytes, null, CHAR64_ARRAY);
        log("转换后64进制数据长度: " + base64Bytes.length + ", 数据: " + new String(base64Bytes));

        assertNotNull("256进制转64进制转换结果不应为null", base64Bytes);
        assertTrue("256进制转64进制转换结果不应为空", base64Bytes.length > 0);

        // 验证转换结果的合理性（通过反向转换验证）
        byte[] backToData = DataBaseXUtils.conversionToBaseX(base64Bytes, CHAR64_ARRAY, null);
        String backToHexString = new String(backToData);
        log("反向转换回16进制数据长度: " + backToData.length + ", 数据: " + backToHexString);
        assertArrayEquals("256进制与64进制双向转换应保持一致性", backToData, dataBytes);

        log("256进制转64进制功能测试通过");
    }

    @After
    public void tearDown() throws Exception {
        log("DataBaseXUtils测试执行完成");
    }

    /**
     * 自定义日志方法，输出带时间戳的日志信息
     * @param message 日志消息
     */
    private void log(String message) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [DataBaseXUtilsTest] " + message);
    }
}
