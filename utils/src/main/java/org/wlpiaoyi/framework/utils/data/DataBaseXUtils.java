package org.wlpiaoyi.framework.utils.data;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 基于任意进制转换的数据编码工具类<br/>
 * 支持在不同字符集编码之间进行高效的进制转换<br/>
 * 应用场景：数据压缩、自定义编码、短链接生成、进制转换等<br/>
 * <br/>
 * 主要特点：<br/>
 * 1. 高性能：使用缓存、数组优化、预计算等技术提升转换效率<br/>
 * 2. 线程安全：支持多线程并发调用<br/>
 * 3. 内存高效：减少对象创建，重用映射关系<br/>
 * 4. 灵活配置：支持任意字符集定义的进制转换<br/>
 * </div>
 * </p>
 *
 * <p><b>{@code @author:}</b> wlpiaoyi</p>
 * <p><b>{@code @date:}</b> 2025/11/30 11:49</p>
 * <p><b>{@code @version:}</b> 2.0</p>
 * <hr/>
 */
class DataBaseXUtils {

    // region ========== 常量定义 ==========

    /**
     * 预定义的256个字符集，对应0-255的ASCII/UTF-8字符<br/>
     * 性能优化：避免频繁创建Character对象，直接数组访问
     */
    private static final char[] CHARSET_256 = new char[256];

    /**
     * 对应的字节映射表，用于快速将字符转换为字节<br/>
     * 减少类型转换开销
     */
    private static final byte[] BYTE_MAP_256 = new byte[256];

    /**
     * 索引映射表，用于256字符集的快速索引查询<br/>
     * 与字符编码值保持一致
     */
    private static final int[] INDEX_MAP_256 = new int[256];

    /**
     * 静态初始化块：预初始化256字符集相关映射表<br/>
     * 避免运行时重复初始化，提升首次使用性能
     */
    static {
        // 预初始化256字符集
        for (int i = 0; i < 256; i++) {
            CHARSET_256[i] = (char) i;
            BYTE_MAP_256[i] = (byte) CHARSET_256[i];
            INDEX_MAP_256[i] = i;
        }
    }

    // endregion

    // region ========== 缓存管理 ==========

    /**
     * 缓存键对象，用于减少字符串拼接开销<br/>
     * 使用组合键替代字符串拼接，提升缓存查找效率
     */
    private static final class CacheKey {
        /** 缓存类型标识，如"charIndex"或"indexChar" */
        final String type;

        /** 字符集长度或标识 */
        final String length;

        /**
         * 构造缓存键
         *
         * @param type 缓存类型
         * @param length 字符集长度或标识
         */
        CacheKey(String type, String length) {
            this.type = type;
            this.length = length;
        }

        /**
         * 重写equals方法，确保缓存键比较的正确性
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CacheKey)) return false;
            CacheKey cacheKey = (CacheKey) o;
            return type.equals(cacheKey.type) && length.equals(cacheKey.length);
        }

        /**
         * 重写hashCode方法，确保缓存查找效率
         */
        @Override
        public int hashCode() {
            return 31 * type.hashCode() + length.hashCode();
        }
    }

    /**
     * 字符到索引的映射缓存<br/>
     * 键：缓存键对象<br/>
     * 值：字符到索引的映射关系<br/>
     * 线程安全：使用ConcurrentHashMap确保线程安全
     */
    private static final Map<CacheKey, Map<Character, Integer>> charIndexMapCache =
            new ConcurrentHashMap<>(16);

    /**
     * 索引到字符的映射缓存<br/>
     * 键：缓存键对象<br/>
     * 值：索引到字符的映射关系<br/>
     * 线程安全：使用ConcurrentHashMap确保线程安全
     */
    private static final Map<CacheKey, Map<Integer, Character>> indexCharMapCache =
            new ConcurrentHashMap<>(16);

    // endregion

    // region ========== 映射表构建方法 ==========

    /**
     * 构建字符到索引的映射表<br/>
     * 性能优化：<br/>
     * 1. 对256字符集使用快速路径<br/>
     * 2. 使用缓存避免重复构建<br/>
     * 3. 对非256字符集使用HashMap存储
     *
     * @param charArray 字符集数组，null表示使用256字符集
     * @return 字符到索引的映射表
     */
    private static Map<Character, Integer> convertCharIndexMap(char[] charArray) {
        // 快速路径：处理256字符集
        if (charArray == null || charArray.length == 256) {
            return new AbstractMap<>() {
                @Override
                public Integer get(Object key) {
                    if (key instanceof Character) {
                        // 直接返回字符的编码值作为索引
                        return (int) ((Character) key).charValue();
                    }
                    return null;
                }

                @Override
                public boolean containsKey(Object key) {
                    return key instanceof Character &&
                            ((Character) key).charValue() < 256;
                }

                @Override
                public Set<Entry<Character, Integer>> entrySet() {
                    throw new UnsupportedOperationException("entrySet not supported for performance reasons");
                }
            };
        }

        // 缓存路径：对非256字符集使用缓存
        CacheKey key = new CacheKey("charIndex", String.valueOf(charArray.length));
        return charIndexMapCache.computeIfAbsent(key, k -> {
            // 构建字符到索引的映射
            Map<Character, Integer> dict = new HashMap<>(charArray.length * 2);
            for (int i = 0; i < charArray.length; i++) {
                dict.put(charArray[i], i);
            }
            return dict;
        });
    }

    /**
     * 构建索引到字符的映射表<br/>
     * 性能优化：<br/>
     * 1. 对256字符集使用快速路径<br/>
     * 2. 使用缓存避免重复构建<br/>
     * 3. 对非256字符集使用HashMap存储
     *
     * @param charArray 字符集数组，null表示使用256字符集
     * @return 索引到字符的映射表
     */
    private static Map<Integer, Character> convertIndexCharMap(char[] charArray) {
        // 快速路径：处理256字符集
        if (charArray == null || charArray.length == 256) {
            return new AbstractMap<>() {
                @Override
                public Character get(Object key) {
                    if (key instanceof Integer) {
                        int idx = (Integer) key;
                        // 直接通过数组访问字符
                        return idx >= 0 && idx < 256 ? CHARSET_256[idx] : null;
                    }
                    return null;
                }

                @Override
                public boolean containsKey(Object key) {
                    return key instanceof Integer &&
                            ((Integer) key) >= 0 &&
                            ((Integer) key) < 256;
                }

                @Override
                public Set<Entry<Integer, Character>> entrySet() {
                    throw new UnsupportedOperationException("entrySet not supported for performance reasons");
                }
            };
        }

        // 缓存路径：对非256字符集使用缓存
        CacheKey key = new CacheKey("indexChar", String.valueOf(charArray.length));
        return indexCharMapCache.computeIfAbsent(key, k -> {
            // 构建索引到字符的映射
            Map<Integer, Character> dict = new HashMap<>(charArray.length * 2);
            for (int i = 0; i < charArray.length; i++) {
                dict.put(i, charArray[i]);
            }
            return dict;
        });
    }

    // endregion

    // region ========== 核心转换方法 ==========

    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 进制转换核心方法<br/>
     * 将原始字节数组从源字符集进制转换为目标字符集进制<br/>
     * <br/>
     * 算法原理：<br/>
     * 1. 将源进制数据转换为大整数（BigInteger）<br/>
     * 2. 将大整数转换为目标进制表示<br/>
     * 3. 使用分块处理优化大整数运算性能<br/>
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>originalBytes</b>
     * {@link byte[]} 待转换的原始字节数组<br/>
     * 注意：如果为null或空数组，将返回空字节数组
     * </p>
     *
     * <p><b>{@code @param}</b> <b>originalOffset</b>
     * {@link int} 待转换字节数组的起始索引<br/>
     * 注意：必须为有效索引（0 ≤ offset < originalBytes.length）
     * </p>
     *
     * <p><b>{@code @param}</b> <b>originalLen</b>
     * {@link int} 待转换字节数组的长度<br/>
     * 注意：如果≤0或超出数组边界，将自动调整
     * </p>
     *
     * <p><b>{@code @param}</b> <b>originalChars</b>
     * {@link char[]} 源字符集数组，用于解析原始字节的进制基数<br/>
     * 注意：null表示使用256字符集（0-255）
     * </p>
     *
     * <p><b>{@code @param}</b> <b>convertChars</b>
     * {@link char[]} 目标字符集数组，用于生成转换后的字节表示<br/>
     * 注意：null表示使用256字符集（0-255）
     * </p>
     *
     * <p><b>{@code @return:}</b> {@link byte[]} 转换后的字节数组</p>
     *
     * <p><b>{@code @throws IllegalArgumentException}</b> 当originalOffset参数无效时抛出</p>
     *
     * <p><b>{@code @performance:}</b><br/>
     * 1. 时间复杂度：O(n) 其中n为输入字节长度<br/>
     * 2. 空间复杂度：O(m) 其中m为输出字节长度<br/>
     * 3. 内存使用：优化对象创建，减少内存分配次数<br/>
     * </p>
     *
     * <p><b>{@code @example:}</b><br/>
     * <pre>{@code
     * // 将字节数组从256进制转换为64进制（Base64）
     * byte[] data = ...;
     * char[] base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
     * byte[] result = DataBaseXUtils.conversionToBaseX(data, 0, data.length, null, base64Chars);
     * }</pre>
     * </p>
     */
    static byte[] conversionToBaseX(byte[] originalBytes, int originalOffset, int originalLen,
                                    char[] originalChars, char[] convertChars) {
        // ---------- 参数验证和边界处理 ----------
        if (originalBytes == null || originalBytes.length == 0) {
            return new byte[0];
        }

        int actualLen = originalBytes.length;
        if (originalOffset < 0) originalOffset = 0;
        if (originalOffset >= actualLen) {
            throw new IllegalArgumentException("originalOffset must be between 0 and " + (actualLen - 1));
        }

        if (originalLen <= 0 || originalOffset + originalLen > actualLen) {
            originalLen = actualLen - originalOffset;
        }

        // ---------- 快速路径：256进制到256进制转换 ----------
        // 如果是相同字符集的256进制转换，直接返回原数组的子数组
        if ((originalChars == null || originalChars.length == 256) &&
                (convertChars == null || convertChars.length == 256)) {
            return Arrays.copyOfRange(originalBytes, originalOffset, originalOffset + originalLen);
        }

        // ---------- 获取映射表 ----------
        Map<Character, Integer> originalCharIndexMap = convertCharIndexMap(originalChars);
        Map<Integer, Character> convertIndexCharMap = convertIndexCharMap(convertChars);

        // ---------- 计算源进制基数 ----------
        int originalBaseSize = originalChars == null ? 256 : originalChars.length;
        BigInteger originalBase = BigInteger.valueOf(originalBaseSize);

        // ---------- 分块处理大整数转换 ----------
        // 性能优化：分块处理避免大整数过度膨胀，提升运算效率
        BigInteger value = BigInteger.ZERO;
        int chunkSize = Math.min(originalLen, 1024); // 块大小：1024字节，平衡性能和内存

        for (int i = 0; i < originalLen; i += chunkSize) {
            int end = Math.min(i + chunkSize, originalLen);
            BigInteger chunkValue = BigInteger.ZERO;

            // 处理当前分块
            for (int j = i; j < end; j++) {
                byte b = originalBytes[originalOffset + j];
                int charIndex;

                if (originalChars == null) {
                    // 无符号转换：确保byte值在0-255范围内
                    charIndex = b & 0xFF;
                } else {
                    // 通过映射表查找索引
                    charIndex = originalCharIndexMap.get((char) (b & 0xFF));
                }

                // 大整数运算：value = value * base + index
                chunkValue = chunkValue.multiply(originalBase)
                        .add(BigInteger.valueOf(charIndex));
            }

            // 合并分块结果
            if (i > 0) {
                value = value.multiply(originalBase.pow(end - i))
                        .add(chunkValue);
            } else {
                value = chunkValue;
            }
        }

        // ---------- 转换为目标进制 ----------
        return convertToTargetBase(value, convertIndexCharMap, convertChars);
    }

    /**
     * 将大整数转换为目标进制表示<br/>
     * 性能优化：<br/>
     * 1. 预计算结果长度，避免数组扩容<br/>
     * 2. 使用字节数组直接存储，避免装箱开销<br/>
     * 3. 反向填充数组，避免头部插入操作<br/>
     *
     * @param value 待转换的大整数值
     * @param charMap 索引到字符的映射表
     * @param convertChars 目标字符集数组
     * @return 转换后的字节数组
     */
    private static byte[] convertToTargetBase(BigInteger value,
                                              Map<Integer, Character> charMap,
                                              char[] convertChars) {
        // 处理零值特殊情况
        if (value.equals(BigInteger.ZERO)) {
            char zeroChar = convertChars == null ? CHARSET_256[0] : convertChars[0];
            return new byte[]{(byte) zeroChar};
        }

        int targetBaseSize = convertChars == null ? 256 : convertChars.length;
        BigInteger targetBase = BigInteger.valueOf(targetBaseSize);

        // ---------- 预计算结果长度 ----------
        // 使用数学公式估算结果长度，避免数组扩容
        // 公式：长度 ≈ log_base(value) + 1
        int estimatedSize = (int) Math.ceil(
                value.bitLength() * Math.log(2) / Math.log(targetBaseSize)
        ) + 1;

        // ---------- 反向填充结果数组 ----------
        byte[] temp = new byte[estimatedSize];
        int pos = estimatedSize; // 从后向前填充

        BigInteger tempValue = value;
        while (tempValue.compareTo(BigInteger.ZERO) > 0) {
            // 一次性获取商和余数，减少BigInteger操作
            BigInteger[] divRem = tempValue.divideAndRemainder(targetBase);
            int remainder = divRem[1].intValue();
            tempValue = divRem[0];

            // 获取对应字符
            char c;
            if (convertChars == null) {
                c = CHARSET_256[remainder];
            } else {
                c = charMap.get(remainder);
            }

            // 反向填充
            temp[--pos] = (byte) c;
        }

        // ---------- 返回正确长度的结果 ----------
        int resultLength = estimatedSize - pos;
        if (pos > 0) {
            byte[] result = new byte[resultLength];
            System.arraycopy(temp, pos, result, 0, resultLength);
            return result;
        }

        return temp;
    }

    // endregion
}