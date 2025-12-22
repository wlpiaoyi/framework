package org.wlpiaoyi.framework.generator.plugin.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.LinkedHashMap;

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
                "文件权限类型:FileRoleType(0:默认-Defalut, 1 :向下继承-DownInherit,  2:被动向下继承-PassiveDownInherit)",
                "对外数据权限/二进制,ABC_123:Data_Index(0b1:查看-View, 0b10:下载-Download, 0b100:修改/删除-ModifyDelete)",
                "数据权限类型:FileRole1(0:默认-Default, 1:向下继承-DownInherit, 2:被动向下继承-PassiveDownInherit)",
                "状态:Status(false:无效-Invalid, true:有效-Valid)",
                "用户角色:UserRole(0b001:游客-Guest, 0b010:普通用户-User, 0b100:管理员-Admin)",
                "权限等级:AuthLevel(0x1:一级-Level1, 0x2:二级-Level2, 0x4:三级-Level3)"
        };

        log("测试正确格式的字符串");
        for (int i = 0; i < validCases.length; i++) {
            String testCase = validCases[i];
            log("测试用例 " + (i + 1) + ": " + testCase);
            boolean result = CommentEnumParse.validate(testCase);
            assertTrue("应通过验证: " + testCase, result);
            log("验证结果: ✓ 通过");
        }

        // 错误的测试用例
        String[] invalidCases = {
                "名称Code(0:值)",                     // 缺少冒号
                "名称:Code0:值)",                    // 括号不匹配
                "名称:Code(:空键)",                  // 空键（实际正则不允许空键）
                "名称:Code(0:)",                     // 空值（缺少 - 或内容）
                "名称:Code(0)",                      // 缺少冒号和值
                "名称:Code(0123:值)",                // 八进制格式不支持（但会被当作十进制？注意：0123 是合法十进制字符串）
                "名称@特殊字符:Code(0:值)",          // 名称包含 @（REGEX_INFO_NAME 不允许 @）
        };

        log("测试错误格式的字符串");
        for (int i = 0; i < invalidCases.length; i++) {
            String testCase = invalidCases[i];
            log("测试用例 " + (i + 1) + ": " + testCase);
            boolean result = CommentEnumParse.validate(testCase);
            assertFalse("应拒绝无效格式: " + testCase, result);
            log("验证结果: ✓ 正确拒绝");
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
                "文件权限类型:FileRoleType(0:默认-Defalut, 1 :向下继承-DownInherit,  2:被动向下继承-PassiveDownInherit)",
                "对外数据权限/二进制,ABC_123:Data_Index(0b1:查看-View, 0b10:下载-Download, 0b100:修改/删除-ModifyDelete)",
                "数据权限类型:FileRole1(0:默认-Default, 1:向下继承-DownInherit, 2:被动向下继承-PassiveDownInherit)",
                "状态:Status(false:无效-Invalid, true:有效-Valid)",
                "用户角色:UserRole(0b001:游客-Guest, 0b010:普通用户-User, 0b100:管理员-Admin)",
                "权限等级:AuthLevel(0x1:一级-Level1, 0x2:二级-Level2, 0x4:三级-Level3)",
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
                log("parse方法结果: 成功");

                if (validateResult && parseSuccess) {
                    log("✓ 兼容性验证通过: validate和parse都成功");
                    passed++;
                } else {
                    log("✗ 兼容性验证失败");
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

        Map<String, Boolean> edgeCases = new LinkedHashMap<>();

        // 最小合法长度
        edgeCases.put("a:b(0:c-d)", true);           // 最小合法
        edgeCases.put("名:码(0:值-Val)", false);       // 中文合法（REGEX_INFO_NAME 支持中文）

        // 空格处理（trim 后应合法）
        edgeCases.put("  名称 ： abc  (  0  :  值  -  Val  )  ", true);
        edgeCases.put("名称:ab12(0 :值-Val)", true);
        edgeCases.put("名称:ab12(0: 值-Val)", true);
        edgeCases.put("名称:ab_12(0 : 值-Val)", true);

        // 特殊字符在描述或编码中（允许 / - _ 空格）
        edgeCases.put("名称:Code(0:值/带斜线-ValSlash)", true);
        edgeCases.put("名称:Code(0:值 带空格-ValSpace)", true);
        edgeCases.put("名称:Code(0:值-带横线-ValDash)", true);   // 注意：这里 value 是 "值-带横线" 和 "ValDash" → 实际合法！因为只分割第一个 '-'
        edgeCases.put("名称:Code(0:值_带下划线-ValUnderscore)", true);

        // 各种数字格式
        edgeCases.put("测试:Test(0b0:零-Zero, 0b1:一-One, 0b10:二-Two, 0b11:三-Three)", true);
        edgeCases.put("测试:Test(0x0:零-Zero, 0x1:一-One, 0xA:十-Ten, 0xF:十五-Fifteen)", true);
        edgeCases.put("测试:Test(0:零-Zero, 1:一-One, 10:十-Ten, 100:百-Hundred)", true);
        edgeCases.put("测试:Test(false:假-False, true:真-True)", true);

//        // 混合格式
//        edgeCases.put("混合:Mixed(0b1:二进制-Bin, 2:十进制-Dec, 0x3:十六进制-Hex, true:布尔-Bool)", false);

        // 错误情况
        edgeCases.put("", false);
        edgeCases.put("名称:abc", false);            // 缺少括号
        edgeCases.put("名称:abc()", false);          // 空括号
        edgeCases.put(":代码(0:值-Val)", false);     // 空描述
        edgeCases.put("名称:(0:值-Val)", false);     // 空code
        edgeCases.put("名称:代码(0:值-Val, )", false); // 末尾逗号+空

        int testCount = 1;
        for (Map.Entry<String, Boolean> entry : edgeCases.entrySet()) {
            String testCase = entry.getKey();
            Boolean expected = entry.getValue();
            log("边界测试 " + testCount + ": " + testCase);

            boolean actual = testCase == null ? false : CommentEnumParse.validate(testCase);
            log("期望: " + expected + ", 实际: " + actual);

            assertEquals("边界测试失败: " + testCase, expected, actual);
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

        String testCase = "测试名称:TestCode(0b1:值1-V1, 0b10:值2-V2, 0b100:值3-V3, 0b1000:值4-V4, 0b10000:值5-V5)";
        int iterations = 10000;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            CommentEnumParse.validate(testCase);
        }
        long duration = System.currentTimeMillis() - startTime;

        log("执行 " + iterations + " 次耗时: " + duration + "ms");
        assertTrue("性能应合理", duration < 5000); // 5秒内
    }

    /**
     * 测试实际使用场景（含 boolean 枚举）
     */
    @Test
    public void testRealWorldScenarios() {
        log("开始测试实际使用场景");

        List<String> scenarios = Arrays.asList(
                "用户权限:UserPermission(0b0001:查看-View, 0b0010:编辑-Edit, 0b0100:删除-Del, 0b1000:管理-Manage)",
                "订单状态:OrderStatus(0:待支付-Pending, 1:已支付-Paid, 2:已发货-Shipped, 3:已完成-Completed, 4:已取消-Cancelled)",
                "性别:Gender(0:未知-Unknown, 1:男-Male, 2:女-Female)",
                "是否有效:IsValid(false:无效-Invalid, true:有效-Valid)",
                "菜单类型:MenuType(0:目录-Dir, 1:菜单-Menu, 2:按钮-Button)",
                "日志级别:LogLevel(0x1:DEBUG-Debug, 0x2:INFO-Info, 0x4:WARN-Warn, 0x8:ERROR-Error, 0x10:FATAL-Fatal)",
                "文件权限:FilePermission(0b1:读-Read, 0b10:写-Write, 0b100:执行-Execute)"
        );

        for (String scenario : scenarios) {
            assertTrue("业务场景应有效: " + scenario, CommentEnumParse.validate(scenario));
            CommentEnumParse.ParseResult result = CommentEnumParse.parse(scenario);
            assertNotNull(result);

            if (result.isIntType()) {
                assertTrue(((CommentEnumParse.IntParseResult) result).getIntDescPairs().size() > 0);
            } else if (result.isBooleanType()) {
                assertTrue(((CommentEnumParse.BooleanParseResult) result).getBoolInfoPairs().size() > 0);
            } else {
                fail("未知的 ParseResult 类型: " + result.getClass());
            }
        }
    }

    /**
     * 测试键值对的各种格式（包括 boolean）
     */
    @Test
    public void testKeyValueFormats() {
        log("开始测试键值对的各种格式");

        String[] keyFormats = {
                "二进制:Binary(0b0:零-Zero, 0b1:一-One, 0b10:二-Two, 0b11:三-Three, 0b101:五-Five)",
                "十六进制:Hex(0x0:零-Zero, 0x1:一-One, 0xA:十-Ten, 0xF:十五-Fifteen, 0x10:十六-Sixteen)",
                "十进制:Decimal(0:零-Zero, 1:一-One, 10:十-Ten, 100:百-Hundred, 255:二百五十五-TwoFiftyFive)",
                "布尔值:Boolean(false:假-False, true:真-True)",
                // 注意：混合格式已被禁止，故移除
        };

        int testCaseIndex = 1;
        for (String format : keyFormats) {
            log(">>> 测试用例 " + testCaseIndex + ": " + format);

            // Step 1: 验证格式
            boolean isValid = CommentEnumParse.validate(format);
            log("  - validate() 结果: " + (isValid ? "✓ 通过" : "✗ 失败"));
            assertTrue("格式应合法: " + format, isValid);

            // Step 2: 解析
            CommentEnumParse.ParseResult result;
            try {
                result = CommentEnumParse.parse(format);
                assertNotNull("解析结果不应为 null", result);
                log("  - parse() 成功，解析出 " + result.getStringDescPairs().size() + " 个键值对");
            } catch (Exception e) {
                log("  - parse() 抛出异常: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                fail("解析不应失败: " + format + " | 错误: " + e.getMessage());
                return;
            }

            // Step 3: 打印解析内容
            log("    字符串键 -> (描述, 编码):");
            for (Map.Entry<String, String> entry : result.getStringDescPairs().entrySet()) {
                String keyStr = entry.getKey();
                String desc = entry.getValue();
                String code = result.getStringCodePairs().get(keyStr);
                log("      '" + keyStr + "' => 描述: \"" + desc + "\", 编码: \"" + code + "\"");
            }

            if (result.isIntType()) {
                CommentEnumParse.IntParseResult intRes = (CommentEnumParse.IntParseResult) result;
                log("    整数键 -> 描述:");
                for (Map.Entry<Integer, String> entry : intRes.getIntDescPairs().entrySet()) {
                    log("      " + entry.getKey() + " => \"" + entry.getValue() + "\"");
                }

                // 验证一致性
                for (String k : result.getStringDescPairs().keySet()) {
                    try {
                        int ik = CommentEnumParse.convertKeyToInt(k);
                        assertTrue("整数映射应包含键 " + ik + "（来自字符串键 '" + k + "'）",
                                intRes.getIntDescPairs().containsKey(ik));
                        String descFromStr = result.getStringDescPairs().get(k);
                        String descFromInt = intRes.getIntDescPairs().get(ik);
                        assertEquals("描述应一致（字符串键 vs 整数键）: " + k, descFromStr, descFromInt);
                        log("  - ✓ 键 '" + k + "' -> 整数 " + ik + " 映射一致");
                    } catch (Exception e) {
                        fail("键转换或一致性检查失败: " + k + " | " + e.getMessage());
                    }
                }
            } else if (result.isBooleanType()) {
                CommentEnumParse.BooleanParseResult boolRes = (CommentEnumParse.BooleanParseResult) result;
                log("    布尔键 -> 描述:");
                for (Map.Entry<Boolean, String> entry : boolRes.getBoolInfoPairs().entrySet()) {
                    log("      " + entry.getKey() + " => \"" + entry.getValue() + "\"");
                }
                // boolean 不需要 convertKeyToInt，跳过一致性检查
            }

            log("  <<< 测试用例 " + testCaseIndex + " 通过\n");
            testCaseIndex++;
        }

        log("所有键值对格式测试完成 ✅");
    }

    /**
     * 测试错误处理和异常情况
     */
    @Test
    public void testErrorHandling() {
        log("开始测试错误处理和异常情况");

        Map<String, String> errorCases = new LinkedHashMap<>();
        errorCases.put("名称:Code(0xGG:值-Val)", "十六进制非法字符");
        errorCases.put("名称:Code(True:值-Val)", "布尔值大小写错误（应为 true/false）");
        errorCases.put("😊表情:Code(0:值-Val)", "名称含表情（REGEX_INFO_NAME 不支持）");
        errorCases.put("名称:Code(😊:值-Val)", "键含表情");
        errorCases.put("名称:代码-带横线(0:值-Val)", "code 含横线（code 只允许 [a-zA-Z0-9_]+）");
        errorCases.put("名称:代码.带点(0:值-Val)", "code 含点（不合法）");
        errorCases.put("", "空字符串");
        errorCases.put("   ", "空白字符串");
        // 新增：混合类型
//        errorCases.put("混合:Mixed(0:true-True, false:false-False)", "混合 int 和 boolean 键");

        for (Map.Entry<String, String> entry : errorCases.entrySet()) {
            String testCase = entry.getKey();
            log("测试错误用例: " + entry.getValue() + " -> " + testCase);
            if (testCase == null || testCase.trim().isEmpty()) {
                assertFalse(CommentEnumParse.validate(testCase));
                continue;
            }
            assertFalse("应拒绝: " + entry.getValue(), CommentEnumParse.validate(testCase));
        }
    }

    /**
     * 综合测试：运行所有测试（含 boolean）
     */
    @Test
    public void testComprehensive() {
        log("开始综合测试");

        String[] comprehensiveCases = {
                "复杂权限:ComplexPermission(0b1:读-Read, 0b10:写-Write, 0b100:执行-Execute, 0b1000:删除-Delete, 0b10000:管理-Manage, 0x20:特殊权限-Special)",
                "状态机:StateMachine(0:初始-Init, 1:处理中-Processing, 2:暂停-Paused, 3:完成-Done, 4:失败-Failed, 5:取消-Cancelled, 0xFF:未知-Unknown)",
                "配置项:ConfigItems(false:禁用-Disabled, true:启用-Enabled)",
                "多语言:MultiLang(0x1:中文-ZH, 0x2:英文-EN, 0x4:日文-JA, 0x8:韩文-KO, 0x10:法文-FR, 0x20:德文-DE)",
                "文件类型:FileType(0b1:文本-Text, 0b10:图片-Image, 0b100:音频-Audio, 0b1000:视频-Video, 0b10000:压缩-Archive, 0b100000:可执行-Executable)"
        };

        for (String testCase : comprehensiveCases) {
            assertTrue(CommentEnumParse.validate(testCase));
            CommentEnumParse.ParseResult result = CommentEnumParse.parse(testCase);
            assertNotNull(result);

            if (result.isIntType()) {
                CommentEnumParse.IntParseResult intRes = (CommentEnumParse.IntParseResult) result;
                assertTrue(!intRes.getIntDescPairs().isEmpty());
                assertEquals(intRes.getIntDescPairs().size(), result.getStringDescPairs().size());
            } else if (result.isBooleanType()) {
                CommentEnumParse.BooleanParseResult boolRes = (CommentEnumParse.BooleanParseResult) result;
                assertTrue(!boolRes.getBoolInfoPairs().isEmpty());
                assertEquals(boolRes.getBoolInfoPairs().size(), result.getStringDescPairs().size());
            } else {
                fail("未知类型: " + result.getClass());
            }
        }

        log("综合测试完成");
    }

    @After
    public void tearDown() throws Exception {}

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private void log(String message) {
        System.out.println("[" + dateFormat.format(new Date()) + "] [CommentEnumParseTest] " + message);
    }
}