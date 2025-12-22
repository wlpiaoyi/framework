package org.wlpiaoyi.framework.generator.plugin.utils;

import lombok.Getter;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.*;
import java.util.regex.*;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 解析注释中的枚举定义，支持 int 和 boolean 两种键类型。
 * <br/>示例：
 * <br/>&emsp;- int:    "文件权限类型:FileRoleType(0:默认-Defalut, 1:向下继承-DownInherit)"
 * <br/>&emsp;- boolean:"状态:Status(false:无效-Invalid, true:有效-Valid)"
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/12/6 10:54</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
public class CommentEnumParse {

    private static final String REGEX_INFO_NAME = "([\\u4e00-\\u9fa5a-zA-Z0-9\\s/\\,_]+)\\s*[：:]{1}\\s*([a-zA-Z0-9_]+)";
    private static final String REGEX_PAIR_PATTERN = "(0b[01]+|0x[0-9a-fA-F]+|\\d+|true|false)\\s*[：:]{1}\\s*([\\u4e00-\\u9fa5a-zA-Z0-9\\s/\\-_]+)";

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 解析输入字符串，返回对应的 ParseResult 子类
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>input</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/12/22 12:57</p>
     * <p><b>{@code @return:}</b>{@link ParseResult}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static ParseResult parse(String input) {
        if (ValueUtils.isBlank(input)) {
            throw new IllegalArgumentException("输入不能为空");
        }

        String mainPattern = "^" + REGEX_INFO_NAME + "\\s*\\((.+)\\)$";
        Pattern pattern = Pattern.compile(mainPattern);
        Matcher matcher = pattern.matcher(input.trim());

        if (!matcher.find()) {
            throw new IllegalArgumentException("输入格式不正确: " + input);
        }

        String desc = matcher.group(1).trim();
        String name = matcher.group(2).trim();
        String pairsStr = matcher.group(3).trim();

        KeyValueResult kvResult = parseKeyValuePairs(pairsStr);

        // 判断类型：只要有一个 boolean key，就视为 boolean 枚举（不允许混合）
        boolean hasBool = !kvResult.boolInfoPairs.isEmpty();
        boolean hasInt = !kvResult.intInfoPairs.isEmpty();

        if (hasBool && hasInt) {
            throw new IllegalArgumentException("不支持混合 int 和 boolean 键: " + input);
        }

        if (hasBool) {
            return new BooleanParseResult(
                    desc, name,
                    kvResult.stringInfoPairs, kvResult.stringCodePairs,
                    kvResult.boolInfoPairs, kvResult.boolCodePairs
            );
        } else if (hasInt) {
            return new IntParseResult(
                    desc, name,
                    kvResult.stringInfoPairs, kvResult.stringCodePairs,
                    kvResult.intInfoPairs, kvResult.intCodePairs
            );
        } else {
            throw new IllegalArgumentException("未识别到有效的键值对: " + input);
        }
    }

    private static KeyValueResult parseKeyValuePairs(String pairsStr) {
        KeyValueResult result = new KeyValueResult();

        Pattern pattern = Pattern.compile(REGEX_PAIR_PATTERN);
        Matcher matcher = pattern.matcher(pairsStr);

        while (matcher.find()) {
            String kStr = matcher.group(1).trim();
            String valuePart = matcher.group(2).trim();

            if (!valuePart.contains("-")) {
                throw new IllegalArgumentException("键值对格式不正确: " + pairsStr);
            }
            String[] parts = valuePart.split("-", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("键值对必须恰好包含一个 '-' 分隔符: " + valuePart);
            }
            String vInfo = parts[0].trim();
            String vCode = parts[1].trim();
            if (vInfo.isEmpty() || vCode.isEmpty()) {
                throw new IllegalArgumentException("描述或编码不能为空: " + valuePart);
            }

            // 区分 boolean 和 int
            if ("true".equalsIgnoreCase(kStr) || "false".equalsIgnoreCase(kStr)) {
                Boolean kBool = Boolean.parseBoolean(kStr.toLowerCase());
                result.boolInfoPairs.put(kBool, vInfo);
                result.boolCodePairs.put(kBool, vCode);
            } else {
                Integer kInt = convertKeyToInt(kStr);
                result.intInfoPairs.put(kInt, vInfo);
                result.intCodePairs.put(kInt, vCode);
            }

            result.stringInfoPairs.put(kStr, vInfo);
            result.stringCodePairs.put(kStr, vCode);
        }

        if (result.stringInfoPairs.isEmpty()) {
            throw new IllegalArgumentException("未找到有效的键值对: " + pairsStr);
        }

        return result;
    }

    static int convertKeyToInt(String keyStr) {
        try {
            if (keyStr.startsWith("0b")) {
                return Integer.parseInt(keyStr.substring(2), 2);
            } else if (keyStr.startsWith("0x")) {
                return Integer.parseInt(keyStr.substring(2), 16);
            } else {
                return Integer.parseInt(keyStr);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无法将键转换为Int类型: " + keyStr, e);
        }
    }

    // ==================== 工具方法 ====================

    public static String format(ParseResult result) {
        if (ValueUtils.isBlank(result)) {
            throw new IllegalArgumentException("输入不能为空");
        }
        return format(result.getDesc(), result.getName(), result.getStringDescPairs(), result.getStringCodePairs());
    }

    public static String format(String desc, String name,
                                Map<String, String> stringInfoPairs,
                                Map<String, String> stringCodePairs) {
        StringBuilder sb = new StringBuilder();
        sb.append(desc).append(":").append(name).append("(");

        List<String> pairStrs = new ArrayList<>();
        for (Map.Entry<String, String> entry : stringInfoPairs.entrySet()) {
            String key = entry.getKey();
            String info = entry.getValue();
            String codeVal = stringCodePairs.get(key);
            if (codeVal == null) {
                throw new IllegalStateException("Missing code for key: " + key);
            }
            pairStrs.add(key + ":" + info + "-" + codeVal);
        }

        sb.append(String.join(", ", pairStrs));
        sb.append(")");
        return sb.toString();
    }

    public static boolean validate(String input) {
        if (ValueUtils.isBlank(input)) return false;

        String mainPattern = "^\\s*" + REGEX_INFO_NAME + "\\s*[（(]\\s*(.+)\\s*[）)]\\s*$";
        Matcher mainMatcher = Pattern.compile(mainPattern).matcher(input.trim());
        if (!mainMatcher.matches()) return false;

        String pairsStr = mainMatcher.group(3).trim();
        if (pairsStr.isEmpty()) return false;

        Pattern pairPattern = Pattern.compile(REGEX_PAIR_PATTERN);
        Matcher pairMatcher = pairPattern.matcher(pairsStr);

        boolean hasPair = false;
        while (pairMatcher.find()) {
            hasPair = true;
            String valuePart = pairMatcher.group(2).trim();
            if (!valuePart.contains("-")) return false;
            String[] parts = valuePart.split("-", 2);
            if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
                return false;
            }
        }
        return hasPair;
    }

    public static List<Object> extractKeys(String input) {
        String mainPattern = "^" + REGEX_INFO_NAME + "\\((.+)\\)$";
        Matcher mainMatcher = Pattern.compile(mainPattern).matcher(input.trim());
        if (!mainMatcher.matches()) {
            return Collections.emptyList();
        }

        String pairsStr = mainMatcher.group(3).trim();
        List<Object> keys = new ArrayList<>();
        Pattern keyPattern = Pattern.compile("(0b[01]+|0x[0-9a-fA-F]+|\\d+|true|false)(?=\\s*:)");

        Matcher matcher = keyPattern.matcher(pairsStr);
        while (matcher.find()) {
            String keyStr = matcher.group().trim();
            if ("true".equalsIgnoreCase(keyStr) || "false".equalsIgnoreCase(keyStr)) {
                keys.add(Boolean.parseBoolean(keyStr.toLowerCase()));
            } else {
                try {
                    keys.add(convertKeyToInt(keyStr));
                } catch (Exception ignored) {}
            }
        }
        return keys;
    }


    /**
     * 抽象解析结果基类
     */
    @Getter
    public abstract static class ParseResult {
        protected final String desc;
        protected final String name;
        protected final Map<String, String> stringDescPairs;
        protected final Map<String, String> stringCodePairs;

        protected ParseResult(String desc, String name,
                              Map<String, String> stringDescPairs,
                              Map<String, String> stringCodePairs) {
            this.desc = desc;
            this.name = name;
            this.stringDescPairs = new LinkedHashMap<>(stringDescPairs);
            this.stringCodePairs = new LinkedHashMap<>(stringCodePairs);
        }

        public abstract boolean isBooleanType();
        public abstract boolean isIntType();

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{desc='" + desc + "', name='" + name +
                    "', stringInfoPairs=" + stringDescPairs +
                    ", stringCodePairs=" + stringCodePairs + "}";
        }
    }

    /**
     * 整型枚举解析结果
     */
    @Getter
    public static class IntParseResult extends ParseResult {
        private final Map<Integer, String> intDescPairs;
        private final Map<Integer, String> intCodePairs;

        public IntParseResult(String desc, String name,
                              Map<String, String> stringDescPairs,
                              Map<String, String> stringCodePairs,
                              Map<Integer, String> intDescPairs,
                              Map<Integer, String> intCodePairs) {
            super(desc, name, stringDescPairs, stringCodePairs);
            this.intDescPairs = new LinkedHashMap<>(intDescPairs);
            this.intCodePairs = new LinkedHashMap<>(intCodePairs);
        }

        @Override
        public boolean isBooleanType() { return false; }

        @Override
        public boolean isIntType() { return true; }

        @Override
        public String toString() {
            return "Int" + super.toString().substring(0, super.toString().length() - 1) +
                    ", intInfoPairs=" + intDescPairs +
                    ", intCodePairs=" + intCodePairs + "}";
        }
    }

    /**
     * 布尔型枚举解析结果
     */
    @Getter
    public static class BooleanParseResult extends ParseResult {
        private final Map<Boolean, String> boolInfoPairs;
        private final Map<Boolean, String> boolDescPairs;

        public BooleanParseResult(String desc, String name,
                                  Map<String, String> stringDescPairs,
                                  Map<String, String> stringCodePairs,
                                  Map<Boolean, String> boolInfoPairs,
                                  Map<Boolean, String> boolDescPairs) {
            super(desc, name, stringDescPairs, stringCodePairs);
            this.boolInfoPairs = new LinkedHashMap<>(boolInfoPairs);
            this.boolDescPairs = new LinkedHashMap<>(boolDescPairs);
        }

        @Override
        public boolean isBooleanType() { return true; }

        @Override
        public boolean isIntType() { return false; }

        @Override
        public String toString() {
            return "Boolean" + super.toString().substring(0, super.toString().length() - 1) +
                    ", boolInfoPairs=" + boolInfoPairs +
                    ", boolCodePairs=" + boolDescPairs + "}";
        }
    }



    private static class KeyValueResult {
        final Map<Integer, String> intInfoPairs = new LinkedHashMap<>();
        final Map<Boolean, String> boolInfoPairs = new LinkedHashMap<>();
        final Map<String, String> stringInfoPairs = new LinkedHashMap<>();

        final Map<Integer, String> intCodePairs = new LinkedHashMap<>();
        final Map<Boolean, String> boolCodePairs = new LinkedHashMap<>();
        final Map<String, String> stringCodePairs = new LinkedHashMap<>();
    }
}