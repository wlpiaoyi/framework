package org.wlpiaoyi.framework.generator.plugin.utils;

import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.*;
import java.util.regex.*;
/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * TODO
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/12/6 10:54</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
class CommentEnumParse {

    // 正则表达式，用于匹配注释信息头
    private static final String REGEX_INFO_NAME = "([\\u4e00-\\u9fa5a-zA-Z0-9\\s/]+):([a-zA-Z0-9_]+)";
    private static final String REGEX_PAIR_PATTERN = "(0b[01]+|0x[0-9a-fA-F]+|\\d+|true|false)\\s*:\\s*([\\u4e00-\\u9fa5a-zA-Z0-9\\s/]+)";

    /**
     * 表示解析后的数据结构
     */
    public static class ParseResult {

        private String name;
        private String code;
        private Map<Integer, String> intPairs;        // key为Int类型的映射
        private Map<String, String> stringPairs;      // 原始字符串key的映射

        public ParseResult(String name, String code,
                           Map<Integer, String> intPairs,
                           Map<String, String> stringPairs) {
            this.name = name;
            this.code = code;
            this.intPairs = intPairs;
            this.stringPairs = stringPairs;
        }

        public String getName() { return name; }
        public String getCode() { return code; }
        public Map<Integer, String> getIntPairs() { return intPairs; }
        public Map<String, String> getStringPairs() { return stringPairs; }

        @Override
        public String toString() {
            return "ParseResult{name='" + name + "', code='" + code +
                    "', intPairs=" + intPairs + ", stringPairs=" + stringPairs + "}";
        }
    }

    /**
     * 主解析方法，返回包含Int类型key的ParseResult
     */
    public static ParseResult parse(String input) {
        // 主正则表达式
        String mainPattern = "^" + REGEX_INFO_NAME + "\\((.+)\\)$";
        Pattern pattern = Pattern.compile(mainPattern);
        Matcher matcher = pattern.matcher(input.trim());

        if (!matcher.find()) {
            throw new IllegalArgumentException("输入格式不正确: " + input);
        }

        // 提取三个部分
        String name = matcher.group(1).trim();
        String code = matcher.group(2).trim();
        String pairsStr = matcher.group(3).trim();

        // 解析键值对，同时获取Int类型和String类型的映射
        KeyValueResult kvResult = parseKeyValuePairs(pairsStr);

        return new ParseResult(name, code, kvResult.intPairs, kvResult.stringPairs);
    }

    /**
     * 键值对解析结果
     */
    private static class KeyValueResult {
        Map<Integer, String> intPairs;
        Map<String, String> stringPairs;

        KeyValueResult(Map<Integer, String> intPairs, Map<String, String> stringPairs) {
            this.intPairs = intPairs;
            this.stringPairs = stringPairs;
        }
    }

    /**
     * 解析键值对，返回包含Int类型key和原始String类型key的两个映射
     */
    private static KeyValueResult parseKeyValuePairs(String pairsStr) {
        Map<Integer, String> intPairs = new LinkedHashMap<>();
        Map<String, String> stringPairs = new LinkedHashMap<>();

        // 使用正则匹配键值对
        Pattern pattern = Pattern.compile(REGEX_PAIR_PATTERN);
        Matcher matcher = pattern.matcher(pairsStr);

        while (matcher.find()) {
            String keyStr = matcher.group(1).trim();
            String value = matcher.group(2).trim();

            // 将key转换为Int类型
            int intKey = convertKeyToInt(keyStr);

            // 同时存储两种类型的映射
            intPairs.put(intKey, value);
            stringPairs.put(keyStr, value);
        }

        if (intPairs.isEmpty()) {
            throw new IllegalArgumentException("键值对格式不正确: " + pairsStr);
        }

        return new KeyValueResult(intPairs, stringPairs);
    }

    /**
     * 将各种格式的key转换为Int类型
     */
    static int convertKeyToInt(String keyStr) {
        try {
            if (keyStr.startsWith("0b")) {
                // 二进制转十进制
                String binaryStr = keyStr.substring(2);
                return Integer.parseInt(binaryStr, 2);
            } else if (keyStr.startsWith("0x")) {
                // 十六进制转十进制
                String hexStr = keyStr.substring(2);
                return Integer.parseInt(hexStr, 16);
            } else if (keyStr.equalsIgnoreCase("true")) {
                // true转换为1
                return 1;
            } else if (keyStr.equalsIgnoreCase("false")) {
                // false转换为0
                return 0;
            } else {
                // 十进制直接转换
                return Integer.parseInt(keyStr);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无法将键转换为Int类型: " + keyStr, e);
        }
    }

    /**
     * 将Int类型的key转换回原始字符串格式
     */
    private static String convertIntToKey(int intKey, String originalKeyStr) {
        // 根据原始字符串的格式来决定返回什么格式
        if (originalKeyStr.startsWith("0b")) {
            // 保持二进制格式
            return "0b" + Integer.toBinaryString(intKey);
        } else if (originalKeyStr.startsWith("0x")) {
            // 保持十六进制格式
            return "0x" + Integer.toHexString(intKey).toUpperCase();
        } else if (originalKeyStr.equalsIgnoreCase("true") || originalKeyStr.equalsIgnoreCase("false")) {
            // 保持布尔格式
            return intKey == 0 ? "false" : "true";
        } else {
            // 保持十进制格式
            return String.valueOf(intKey);
        }
    }

    /**
     * 将ParseResult格式化为字符串
     */
    public static String format(ParseResult result) {
        return format(result.getName(), result.getCode(), result.getStringPairs());
    }

    /**
     * 使用原始字符串key进行格式化
     */
    public static String format(String name, String code, Map<String, String> stringPairs) {
        StringBuilder sb = new StringBuilder();

        sb.append(name).append(":").append(code).append("(");

        List<String> pairStrs = new ArrayList<>();
        for (Map.Entry<String, String> entry : stringPairs.entrySet()) {
            pairStrs.add(entry.getKey() + ":" + entry.getValue());
        }

        sb.append(String.join(", ", pairStrs));
        sb.append(")");

        return sb.toString();
    }

    /**
     * 使用Int类型key进行格式化（默认为十进制格式）
     */
    public static String formatWithIntKeys(String name, String code, Map<Integer, String> intPairs) {
        StringBuilder sb = new StringBuilder();

        sb.append(name).append(":").append(code).append("(");

        List<String> pairStrs = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : intPairs.entrySet()) {
            pairStrs.add(entry.getKey() + ":" + entry.getValue());
        }

        sb.append(String.join(", ", pairStrs));
        sb.append(")");

        return sb.toString();
    }

    /**
     * 验证输入是否匹配格式的正则表达式
     */
    public static boolean validate(String input) {
        String mainPattern = "^" + REGEX_INFO_NAME + "\\((.+)\\)$";
        if(!Pattern.matches(mainPattern, input.trim())) return  false;

        // 使用正则匹配键值对
        Pattern pattern = Pattern.compile(REGEX_PAIR_PATTERN);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String keyStr = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            if(ValueUtils.isNotBlank(keyStr) && ValueUtils.isNotBlank(value))
                return true;
        }
        return false;
    }

    /**
     * 测试方法
     */
    public static void test() {
        // 测试用例
        String[] testCases = {
                "对外数据权限/二进制:Data_Index(0b1:查看, 0b10:下载, 0b100:修改/删除)",
                "数据权限类型:FileRole1(0:默认, 1:向下继承, 2:被动向下继承)",
                "状态:Status(false:无效, true:有效)",
                "用户角色:UserRole(0b001:游客, 0b010:普通用户, 0b100:管理员)",
                "权限等级:AuthLevel(0x1:一级, 0x2:二级, 0x4:三级)",
                "测试混合格式:Mixed(0b1:A, 2:B, 0xC:C, true:D)"
        };

        System.out.println("=== 测试解析功能 ===");
        for (String testCase : testCases) {
            try {
                System.out.println("\n输入: " + testCase);
                ParseResult result = parse(testCase);
                System.out.println("解析结果:");
                System.out.println("  名称: " + result.getName());
                System.out.println("  代码: " + result.getCode());
                System.out.println("  Int类型键值对: " + result.getIntPairs());
                System.out.println("  字符串类型键值对: " + result.getStringPairs());

                // 测试格式化回原格式
                String formatted = format(result);
                System.out.println("  格式化回原格式: " + formatted);

                // 测试使用Int类型key格式化
                String formattedWithInt = formatWithIntKeys(
                        result.getName(), result.getCode(), result.getIntPairs());
                System.out.println("  使用Int类型key格式化: " + formattedWithInt);
            } catch (Exception e) {
                System.out.println("  解析失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /**
     * 提取所有键值对中的Int类型键
     */
    public static List<Integer> extractIntKeys(String input) {
        List<Integer> keys = new ArrayList<>();

        // 匹配键部分的正则
        String keyPattern = "(0b[01]+|0x[0-9a-fA-F]+|\\d+|true|false)(?=\\s*:)";
        Pattern pattern = Pattern.compile(keyPattern);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String keyStr = matcher.group();
            keys.add(convertKeyToInt(keyStr));
        }

        return keys;
    }

    /**
     * 查找指定Int值对应的描述
     */
    public static String findValueByIntKey(String input, int intKey) {
        ParseResult result = parse(input);
        return result.getIntPairs().get(intKey);
    }

    /**
     * 查找指定字符串key对应的描述
     */
    public static String findValueByStringKey(String input, String stringKey) {
        ParseResult result = parse(input);
        return result.getStringPairs().get(stringKey);
    }

}
