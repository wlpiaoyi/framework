package org.wlpiaoyi.framework.utils.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataUtilsTest {

    @Before
    public void setUp() throws Exception {}

    /**
     * 测试Base64编解码功能
     */
    @Test
    public void testBase92EncodeDecode() {
        {
            String testData = "；安排维卡史蒂夫卡爱迪生的饭卡上；看傻傻的开发扣税的；爱看书的地方卡萨斯的看法；看阿道夫那位加哦就diekemdjd的卡我很渴望哈开发商大家那时的爱上对方家里就安慰两句啊发生的放假咯了";
            byte[] originalBytes = testData.getBytes(StandardCharsets.UTF_8);
            // 测试byte[]编解码
            log("byte[]编码开始");
            byte[] encodedBytes = DataUtils.base92Encode(originalBytes);
            log("byte[]编码结果：\n" + new String(encodedBytes, StandardCharsets.UTF_8));
            log("byte[]解码开始");
            byte[] decodedBytes = DataUtils.base92Decode(encodedBytes);
            log("byte[]解码结果：\n" + new String(decodedBytes, StandardCharsets.UTF_8));
        }
        {
            String testData = "按钮去咯i如热体u微软i偶五日u人陪我嚄98349389wklalKJSJK;JALSKJA;SDLFJ艾丽卡士大夫按你的卡上的看法艾丽卡的咖啡店啊收到了科技发达解放军" +
                    "爱空间发起脾气无法，没法肯定累u让飞机空投开过会了太热了您可通过人哟与快乐进口轮胎压力就解开了好了好了狂热特痛苦和热让客人挺好看韩国" +
                    "阿萨尔你看看你骄傲的房间啊飒飒的解放军而侮辱特颇有他【u【披露可能陪陪你看看你，；uil，；ui，；uio，u；i，；uiyt；与看人品投入typo苦痛内衣裤" +
                    "士大夫立刻就公司的法拉利科技公司附件二微软推荐哦温热推荐温热推荐哦忒我就未提及未提及；理解为过热推荐；尼科特就为了软键盘网球IERJIUT" +
                    "QWERQJQ解决日俄u头俄日就如同接口撇我如今推荐哦i；立陶宛具有覅u哦同日偶然听见他奇偶位u";
            byte[] originalBytes = testData.getBytes(StandardCharsets.UTF_8);
            // 测试byte[]编解码
            log("byte[]编码开始");
            byte[] encodedBytes = DataUtils.base92Encode(originalBytes);
            log("byte[]编码结果：\n" + new String(encodedBytes, StandardCharsets.UTF_8));
            log("byte[]解码开始");
            byte[] decodedBytes = DataUtils.base92Decode(encodedBytes);
            log("byte[]解码结果：\n" + new String(decodedBytes, StandardCharsets.UTF_8));
        }
    }



    @Test
    public void testBase92EncodeMaxMin() {
        // 测试byte[]编解码
        log("byte[]编码开始");
        byte[] encodedBytes = new byte[100];
        for (int i = 0; i < encodedBytes.length; i++) {
            encodedBytes[i] = (byte) 0xFF;
        }
        log("byte[]编码开始");
        byte[] decodedBytes = DataUtils.base92Encode(encodedBytes);
        log("byte[] length:" + new String(decodedBytes, StandardCharsets.UTF_8).length() + " test结果：\n" + new String(decodedBytes, StandardCharsets.UTF_8));
        for (int i = 0; i < encodedBytes.length; i++) {
            encodedBytes[i] = (byte) 0x0;
        }
        encodedBytes[0] = (byte) 0x1;
        log("byte[]编码开始");
        decodedBytes = DataUtils.base92Encode(encodedBytes);
        log("byte[] length:" + new String(decodedBytes, StandardCharsets.UTF_8).length() + " test结果：\n" + new String(decodedBytes, StandardCharsets.UTF_8));
    }

    @Test
    public void testBase92EncodeDecodeStream() throws IOException {
        // 测试数据
        String testData = "alkadl这是一个Base92编解码流测试，包含各种字符：ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-={}[]|:;\"'<>?,./~`";
        for (int i = 0; i < 20; i++){
            testData +=
                    "爱空间发起脾气无法，没法肯定累u让飞机空投开过会了太热了您可通过人哟与快乐进口轮胎压力就解开了好了好了狂热特痛苦和热让客人挺好看韩国" +
                    "阿萨尔你看看你骄傲的房间啊飒飒的解放军而侮辱特颇有他【u【披露可能陪陪你看看你，；uil，；ui，；uio，u；i，；uiyt；与看人品投入typo苦痛内衣裤" +
                    "士大夫立刻就公司的法拉利科技公司附件二微软推荐哦温热推荐温热推荐哦忒我就未提及未提及；理解为过热推荐；尼科特就为了软键盘网球IERJIUT" +
                    "QWERQJQ解决日俄u头俄日就如同接口撇我如今推荐哦i；立陶宛具有覅u哦同日偶然听见他奇偶位uasdakdk欧日为日无色融入问了人家哦i二年热哦i二呢日哦尔特" +
                    "阿德我问哦i微微染发剂温柔i问了句哦i为u认为人家哦i微软九年解耦i我却啊是u如附件为就为日u哦微弱为u哦u欧文u荣威u荣威u偶认为哦";
        }

        // 创建输入流和输出流
        ByteArrayInputStream input = new ByteArrayInputStream(testData.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream encodedOutput = new ByteArrayOutputStream();
        // 执行Base92编码
        long startTime = System.currentTimeMillis();
        log("编码数据长度:" + testData.getBytes(StandardCharsets.UTF_8).length);
        DataUtils.base92Encode(input, encodedOutput);
        log("编码耗时: " + (System.currentTimeMillis() - startTime) + "ms");
        log("原始数据: " + testData);
        byte[] encodedData = encodedOutput.toByteArray();
        String encodedString = new String(encodedData, StandardCharsets.UTF_8);
        log("编码结果: \n" + encodedString);

        // 创建解码输入流和输出流
        ByteArrayInputStream encodedInput = new ByteArrayInputStream(encodedData);
        ByteArrayOutputStream decodedOutput = new ByteArrayOutputStream();

        // 执行Base92解码
        startTime = System.currentTimeMillis();
        log("解码数据长度:" + encodedData.length);
        DataUtils.base92Decode(encodedInput, decodedOutput);
        log("解码耗时: " + (System.currentTimeMillis() - startTime) + "ms");
        byte[] decodedData = decodedOutput.toByteArray();
        String decodedString = new String(decodedData, StandardCharsets.UTF_8);
        log("解码结果: " + decodedString);

        encodedString = encodedString.replaceAll("\n", "");
        log("去掉换行符编码结果: " + encodedString);
        // 创建解码输入流和输出流
        encodedInput = new ByteArrayInputStream(encodedString.getBytes(StandardCharsets.UTF_8));
        decodedOutput = new ByteArrayOutputStream();

        // 执行Base92解码
        startTime = System.currentTimeMillis();
        log("去掉换行符解码数据长度:" + encodedString.getBytes(StandardCharsets.UTF_8).length);
        DataUtils.base92Decode(encodedInput, decodedOutput);
        log("去掉换行符解码耗时: " + (System.currentTimeMillis() - startTime) + "ms");
        decodedData = decodedOutput.toByteArray();
        decodedString = new String(decodedData, StandardCharsets.UTF_8);
        log("去掉换行符解码结果: " + decodedString);

        // 验证编解码正确性
        assertEquals(testData, decodedString);
        assertTrue(Arrays.equals(testData.getBytes(StandardCharsets.UTF_8), decodedData));

        log("Base92流编解码测试通过");
    }


    @After
    public void tearDown() throws Exception {

    }


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /**
     * 自定义日志方法，输出带时间戳的日志信息
     * @param message 日志消息
     */
    private void log(String message) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [DataBaseXUtilsTest] " + message);
    }
}
