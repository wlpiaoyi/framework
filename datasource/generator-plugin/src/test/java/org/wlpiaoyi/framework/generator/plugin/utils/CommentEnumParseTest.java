package org.wlpiaoyi.framework.generator.plugin.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

public class CommentEnumParseTest {

    @Before
    public void setUp() throws Exception {}

    /**
     * 测试CommentEnumParse.validate方法的基础功能
     */
    @Test
    public void testValidateBasicFunction() {
        log("开始测试validate方法的基础功能");

        // 正确的测试用例
        String[] validCases = {
                "对外数据权限/二进制,ABC_123:Data_Index(0b1:查看, 0b10:下载, 0b100:修改/删除)",
                "数据权限类型:FileRole1(0:默认, 1:向下继承, 2:被动向下继承)",
                "状态:Status(false:无效, true:有效)",
                "用户角色:UserRole(0b001:游客, 0b010:普通用户, 0b100:管理员)",
                "权限等级:AuthLevel(0x1:一级, 0x2:二级, 0x4:三级)"
        };

        log("测试正确格式的字符串");
        for (int i = 0; i < validCases.length; i++) {
            String testCase = validCases[i];
            log("测试用例 " + (i + 1) + ": " + testCase);
            boolean result = CommentEnumParse.validate(testCase);
            log("验证结果: " + (result ? "✓ 通过" : "✗ 失败"));
        }

        // 错误的测试用例
        String[] invalidCases = {
                "名称Code(0:值)",  // 缺少冒号
                "名称:Code0:值)",  // 括号不匹配
                "名称:Code(:空键)",  // 空键
                "名称:Code(0:)",  // 空值
                "名称:Code(0)",  // 缺少冒号
                "名称:Code(0123:值)",  // 八进制格式不支持
                "名称@特殊字符:Code(0:值)",  // 名称包含特殊字符
        };

        log("测试错误格式的字符串");
        for (int i = 0; i < invalidCases.length; i++) {
            String testCase = invalidCases[i];
            log("测试用例 " + (i + 1) + ": " + testCase);
            boolean result = CommentEnumParse.validate(testCase);
            log("验证结果: " + (!result ? "✓ 正确拒绝" : "✗ 错误接受"));
        }

        log("基础功能测试完成");
    }

    /**
     * 测试validate与parse方法的兼容性
     */
    @Test
    public void testValidateAndParseCompatibility() {
        log("开始测试validate与parse方法的兼容性");

        String[] testCases = {
                "对外数据权限/二进制,ABC_123:Data_Index(0b1:查看, 0b10:下载, 0b100:修改/删除)",
                "数据权限类型:FileRole1(0:默认, 1:向下继承, 2:被动向下继承)",
                "状态:Status(false:无效, true:有效)",
                "用户角色:UserRole(0b001:游客, 0b010:普通用户, 0b100:管理员)",
                "权限等级:AuthLevel(0x1:一级, 0x2:二级, 0x4:三级)",
        };

        int passed = 0;
        int total = testCases.length;

        for (String testCase : testCases) {
            log("测试字符串: " + testCase);
            boolean validateResult = CommentEnumParse.validate(testCase);
            log("validate方法结果: " + validateResult);

            try {
                CommentEnumParse.ParseResult parseResult = CommentEnumParse.parse(testCase);
                boolean parseSuccess = (parseResult != null);
                log("parse方法结果: " + (parseSuccess ? "成功" : "失败"));

                // 验证兼容性
                if (validateResult && parseSuccess) {
                    log("✓ 兼容性验证通过: validate和parse都成功");
                    passed++;
                } else if (!validateResult && !parseSuccess) {
                    log("✓ 兼容性验证通过: validate和parse都失败");
                    passed++;
                } else {
                    log("✗ 兼容性验证失败: validate=" + validateResult + ", parse=" + parseSuccess);
                }
            } catch (Exception e) {
                log("parse方法抛出异常: " + e.getMessage());
                if (!validateResult) {
                    log("✓ 兼容性验证通过: validate返回false，parse抛出异常");
                    passed++;
                } else {
                    log("✗ 兼容性验证失败: validate返回true但parse抛出异常");
                }
            }
        }

        log("兼容性测试结果: " + passed + "/" + total + " 通过");
        assertEquals("所有测试用例应该都兼容", total, passed);
    }

    /**
     * 测试边界值和特殊情况
     */
    @Test
    public void testEdgeCases() {
        log("开始测试边界值和特殊情况");

        // 边界值测试用例
        Map<String, Boolean> edgeCases = new LinkedHashMap<>();

        // 最小长度
        edgeCases.put("a:b(0:c)", true);
        edgeCases.put("名:码(0:值)", false);

        // 各种空格情况
        edgeCases.put("  名称  :  代码  (  0  :  值  )  ", false);  // 前后空格
        edgeCases.put("名称:ab12(0 :值)", true);  // 键后空格
        edgeCases.put("名称:ab12(0: 值)", true);  // 值前空格
        edgeCases.put("名称:ab_12(0 : 值)", true);  // 键值前后都有空格

        edgeCases.put("  名称  :  代码  (  0  :  值  )  ", false);  // 前后空格
        edgeCases.put("名称:代码(0 :值)", false);  // 键后空格
        edgeCases.put("名称:代码(0: 值)", false);  // 值前空格
        edgeCases.put("名称:代码(0 : 值)", false);  // 键值前后都有空格

        // 特殊字符在值中
        edgeCases.put("名称:Code(0:值/带斜线)", true);
        edgeCases.put("名称:Code(0:值 带空格)", true);
        edgeCases.put("名称:Code(0:值-带横线)", true);
        edgeCases.put("名称:Code(0:值_带下划线)", true);

        // 各种数字格式
        edgeCases.put("测试:Test(0b0:零, 0b1:一, 0b10:二, 0b11:三)", true);
        edgeCases.put("测试:Test(0x0:零, 0x1:一, 0xA:十, 0xF:十五)", true);
        edgeCases.put("测试:Test(0:零, 1:一, 10:十, 100:百)", true);
        edgeCases.put("测试:Test(false:假, true:真)", true);

        // 混合数字格式
        edgeCases.put("混合:Mixed(0b1:二进制, 2:十进制, 0x3:十六进制, true:布尔)", true);

        // 错误的边界情况
        edgeCases.put("", false);  // 空字符串
        edgeCases.put("名称:abc", false);  // 缺少括号部分
        edgeCases.put("名称:abc()", false);  // 空括号
        edgeCases.put(":代码(0:值)", false);  // 空名称
        edgeCases.put("名称:(0:值)", false);  // 空代码
        edgeCases.put("名称:代码(0:值, )", false);  // 逗号后空

        int testCount = 1;
        for (Map.Entry<String, Boolean> entry : edgeCases.entrySet()) {
            String testCase = entry.getKey();
            Boolean expected = entry.getValue();
            log("边界测试 " + testCount + ": " + testCase);

            boolean actual = CommentEnumParse.validate(testCase);
            log("期望结果: " + expected + ", 实际结果: " + actual);

            if (actual == expected) {
                log("✓ 边界测试通过");
            } else {
                log("✗ 边界测试失败");
                CommentEnumParse.validate(testCase);
            }

            assertEquals("边界测试用例 " + testCase + " 应该返回 " + expected, expected, (Boolean)actual);
            testCount++;
        }

        log("边界值测试完成，共测试 " + (testCount - 1) + " 个用例");
    }

    /**
     * 测试validate方法的性能
     */
    @Test
    public void testPerformance() {
        log("开始测试validate方法的性能");

        // 创建一个标准测试用例
        String testCase = "测试名称:TestCode(0b1:值1, 0b10:值2, 0b100:值3, 0b1000:值4, 0b10000:值5)";
        log("测试用例: " + testCase);

        int iterations = 10000;
        log("执行 " + iterations + " 次validate方法");

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < iterations; i++) {
            CommentEnumParse.validate(testCase);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log("执行 " + iterations + " 次耗时: " + duration + "ms");
        log("平均每次耗时: " + String.format("%.4f", duration / (double) iterations) + "ms");

        // 性能断言：单次执行不超过10ms
        assertTrue("单次执行应该不超过10ms", duration < iterations * 10);

        log("性能测试完成");
    }

    /**
     * 测试实际使用场景
     */
    @Test
    public void testRealWorldScenarios() {
        log("开始测试实际使用场景");

        // 实际业务中可能出现的场景
        List<String> scenarios = Arrays.asList(
                "用户权限:UserPermission(0b0001:查看, 0b0010:编辑, 0b0100:删除, 0b1000:管理)",
                "订单状态:OrderStatus(0:待支付, 1:已支付, 2:已发货, 3:已完成, 4:已取消)",
                "性别:Gender(0:未知, 1:男, 2:女)",
                "是否有效:IsValid(false:无效, true:有效)",
                "菜单类型:MenuType(0:目录, 1:菜单, 2:按钮)",
                "日志级别:LogLevel(0x1:DEBUG, 0x2:INFO, 0x4:WARN, 0x8:ERROR, 0x10:FATAL)",
                "文件权限:FilePermission(0b1:读, 0b10:写, 0b100:执行)"
        );

        log("测试 " + scenarios.size() + " 个实际业务场景");

        for (int i = 0; i < scenarios.size(); i++) {
            String scenario = scenarios.get(i);
            log("场景 " + (i + 1) + ": " + scenario);

            // 验证格式
            boolean isValid = CommentEnumParse.validate(scenario);
            log("格式验证: " + (isValid ? "✓ 有效" : "✗ 无效"));
            assertTrue("业务场景应该验证通过: " + scenario, isValid);

            // 尝试解析
            try {
                CommentEnumParse.ParseResult result = CommentEnumParse.parse(scenario);
                log("解析成功:");
                log("  名称: " + result.getName());
                log("  代码: " + result.getCode());
                log("  键值对数量: " + result.getIntPairs().size());

                // 验证解析结果
                assertNotNull("解析结果不应为null", result);
                assertNotNull("名称不应为null", result.getName());
                assertNotNull("代码不应为null", result.getCode());
                assertTrue("至少应有一个键值对", result.getIntPairs().size() > 0);

                log("✓ 场景测试通过");
            } catch (Exception e) {
                log("解析失败: " + e.getMessage());
                fail("业务场景应该能成功解析: " + scenario);
            }
        }

        log("实际使用场景测试完成");
    }

    /**
     * 测试键值对的各种格式
     */
    @Test
    public void testKeyValueFormats() {
        log("开始测试键值对的各种格式");

        // 测试各种键的格式
        String[] keyFormats = {
                "二进制:Binary(0b0:零, 0b1:一, 0b10:二, 0b11:三, 0b101:五)",
                "十六进制:Hex(0x0:零, 0x1:一, 0xA:十, 0xF:十五, 0x10:十六)",
                "十进制:Decimal(0:零, 1:一, 10:十, 100:百, 255:二百五十五)",
                "布尔值:Boolean(false:假, true:真)",
                "混合格式:Mixed(0b1:二进制一, 2:十进制二, 0x3:十六进制三, true:布尔真)"
        };

        for (String format : keyFormats) {
            log("测试格式: " + format);
            boolean isValid = CommentEnumParse.validate(format);
            log("验证结果: " + (isValid ? "✓ 有效" : "✗ 无效"));
            assertTrue("键值对格式应该验证通过: " + format, isValid);

            // 测试键值对的转换
            try {
                CommentEnumParse.ParseResult result = CommentEnumParse.parse(format);
                Map<Integer, String> intPairs = result.getIntPairs();
                Map<String, String> stringPairs = result.getStringPairs();

                log("  解析成功，键值对数量: " + intPairs.size());
                log("  Int类型键值对: " + intPairs);
                log("  字符串类型键值对: " + stringPairs);

                // 验证转换是否正确
                for (Map.Entry<String, String> entry : stringPairs.entrySet()) {
                    String stringKey = entry.getKey();
                    int intKey = CommentEnumParse.convertKeyToInt(stringKey);
                    log("  转换验证: " + stringKey + " -> " + intKey);

                    // 确保转换后的键存在于intPairs中
                    assertTrue("转换后的键应该存在于intPairs中", intPairs.containsKey(intKey));
                    assertEquals("值应该一致", entry.getValue(), intPairs.get(intKey));
                }
            } catch (Exception e) {
                log("  解析失败: " + e.getMessage());
                fail("应该能成功解析: " + format);
            }
        }

        log("键值对格式测试完成");
    }

    /**
     * 测试错误处理和异常情况
     */
    @Test
    public void testErrorHandling() {
        log("开始测试错误处理和异常情况");

        // 各种错误情况
        Map<String, String> errorCases = new LinkedHashMap<>();

//        errorCases.put("名称:Code(0b123:值)", "二进制包含非0/1字符");
//        errorCases.put("名称:Code(1.5:值)", "浮点数不支持");
        errorCases.put("名称:Code(0xGG:值)", "十六进制包含非法字符");
        errorCases.put("名称:Code(True:值)", "布尔值大小写错误");
//        errorCases.put("名称:Code(0:值, 1:值2 2:值3)", "缺少逗号分隔");
//        errorCases.put("名称:Code(0:值, , 1:值2)", "连续逗号");
//        errorCases.put("名称:Code(0:值, 1:值2, )", "末尾多余逗号");
//        errorCases.put("名称:Code(,0:值, 1:值2)", "开头多余逗号");
//        errorCases.put("名称:Code(0:值, 1:值2,)", "末尾逗号");
        errorCases.put("😊表情:Code(0:值)", "名称包含表情符号");
        errorCases.put("名称:Code(😊:值)", "键包含表情符号");
        errorCases.put("名称:代码-带横线(0:值)", "代码包含横线");
        errorCases.put("名称:代码.带点(0:值)", "代码包含点");
        errorCases.put("", "空字符串");
        errorCases.put("   ", "空白字符串");
        errorCases.put(null, "null值");

        int testCount = 1;
        for (Map.Entry<String, String> entry : errorCases.entrySet()) {
            String testCase = entry.getKey();
            String errorDesc = entry.getValue();

            log("错误测试 " + testCount + ": " + errorDesc);
            log("测试用例: " + (testCase == null ? "null" : testCase));

            try {
                boolean result = CommentEnumParse.validate(testCase);
                log("验证结果: " + result);

                // 如果验证通过，但应该是错误的情况
                if (testCase != null && !testCase.trim().isEmpty()) {
                    assertFalse("错误用例应该验证失败: " + errorDesc, result);
                }
            } catch (Exception e) {
                log("验证抛出异常: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                // 对于null值，允许抛出异常
                if (testCase == null) {
                    log("✓ 正确处理null值");
                }
            }

            testCount++;
        }

        log("错误处理测试完成，共测试 " + (testCount - 1) + " 个错误用例");
    }

    /**
     * 综合测试：运行所有测试
     */
    @Test
    public void testComprehensive() {
        log("开始综合测试");

        // 创建一些复杂的测试用例
        String[] comprehensiveCases = {
                "复杂权限:ComplexPermission(0b1:读, 0b10:写, 0b100:执行, 0b1000:删除, 0b10000:管理, 0x20:特殊权限)",
                "状态机:StateMachine(0:初始, 1:处理中, 2:暂停, 3:完成, 4:失败, 5:取消, 0xFF:未知)",
                "配置项:ConfigItems(false:禁用, true:启用)",
                "多语言:MultiLang(0x1:中文, 0x2:英文, 0x4:日文, 0x8:韩文, 0x10:法文, 0x20:德文)",
                "文件类型:FileType(0b1:文本, 0b10:图片, 0b100:音频, 0b1000:视频, 0b10000:压缩, 0b100000:可执行)",
                "名称:Code(0:值, 0b01:值1,0b10:值2)",
                "名称:Code(0:值, , 0x0F:值2)",
                "名称:Code(0:值, 1:值2, )",
                "名称:Code(,0:值, 1:值2)",
                "名称:Code(0:值, 1:值2,)"
        };

        for (String testCase : comprehensiveCases) {
            log("综合测试用例: " + testCase);

            // 验证格式
            boolean isValid = CommentEnumParse.validate(testCase);
            assertTrue("综合测试用例应该验证通过", isValid);
            log("  格式验证: ✓ 通过");

            // 解析
            CommentEnumParse.ParseResult result = CommentEnumParse.parse(testCase);
            assertNotNull("解析结果不应为null", result);
            log("  解析: ✓ 成功");

            // 验证解析结果
            assertNotNull("名称不应为null", result.getName());
            assertNotNull("代码不应为null", result.getCode());
            assertTrue("键值对不应为空", !result.getIntPairs().isEmpty());
            log("  结果验证: ✓ 通过");

            // 验证键值对转换
            Map<Integer, String> intPairs = result.getIntPairs();
            Map<String, String> stringPairs = result.getStringPairs();
            log("  结果: " + result.toString());
            log("  结果: " + result.toString());
            assertEquals("两种映射的条目数应相同", intPairs.size(), stringPairs.size());
            log("  映射一致性: ✓ 通过");
        }

        log("综合测试完成");
    }
    

    @After
    public void tearDown() throws Exception {    }


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /**
     * 自定义日志方法，输出带时间戳的日志信息
     * @param message 日志消息
     */
    private void log(String message) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [DataBaseXUtilsTest] " + message);
    }

}
