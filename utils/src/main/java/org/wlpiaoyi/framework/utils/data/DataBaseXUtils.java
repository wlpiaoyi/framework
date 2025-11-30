package org.wlpiaoyi.framework.utils.data;

import org.wlpiaoyi.framework.utils.ValueUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 提供基于任意进制转换的数据编码工具类，支持不同字符集之间的进制转换操作。
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/30 11:49</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
class DataBaseXUtils {

    private static final Map<String, Map<Character, Integer>> charIndexMapCache = new ConcurrentHashMap<>();
    private static final Map<String, Map<Integer, Character>> indexCharMapCache = new ConcurrentHashMap<>();

    static Map<Character, Integer> convertCharIndexMap(char[] charArray) {
        if(charArray == null){
            String key = "256";
            return charIndexMapCache.computeIfAbsent(key, k -> {
                Map<Character, Integer> dict = new HashMap<>();
                for (int i = 0; i < 256; i++) {
                    dict.put(((char) ((byte) i)), i);
                }
                return dict;
            });
        }
        String key = charArray.length + "";
        return charIndexMapCache.computeIfAbsent(key, k -> {
            Map<Character, Integer> dict = new HashMap<>();
            for (int i = 0; i < charArray.length; i++) {
                dict.put(charArray[i], i);
            }
            return dict;
        });
    }

    static Map<Integer, Character> convertIndexCharMap(char[] charArray) {
        if(charArray == null){
            String key = "256";
            return indexCharMapCache.computeIfAbsent(key, k -> {
                Map<Integer, Character> dict = new HashMap<>();
                for (int i = 0; i < 256; i++) {
                    dict.put(i, ((char) ((byte) i)));
                }
                return dict;
            });
        }
        String key = charArray.length + "";
        return indexCharMapCache.computeIfAbsent(key, k -> {
            Map<Integer, Character> dict = new HashMap<>();
            for (int i = 0; i < charArray.length; i++) {
                dict.put(i, charArray[i]);
            }
            return dict;
        });
    }



    /**
     * <p><b>{@code @description:}</b>
     * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 将原始字节数组从源字符集进制转换为目标字符集进制
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>originalBytes</b>
     * 待转换的原始字节数组
     * {@link byte}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>originalChars</b>
     * 源字符集数组，用于解析原始字节的进制基数
     * {@link char}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>convertChars</b>
     * 目标字符集数组，用于生成转换后的字节表示
     * {@link char}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/30 11:50</p>
     * <p><b>{@code @return:}</b>{@link byte[]} 转换后的字节数组</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    static byte[] conversionToBaseX(byte[] originalBytes, char[] originalChars, char[] convertChars) {
        if (originalBytes == null || originalBytes.length == 0) {
            return new byte[0];
        }
        // 将字符映射回索引值
        Map<Character, Integer> originalCharIndexMap = convertCharIndexMap(originalChars);
        BigInteger originalBase = BigInteger.valueOf(originalCharIndexMap.size());
        List<Byte> result = new java.util.ArrayList<>();
        BigInteger value = BigInteger.ZERO;
        for (byte b : originalBytes) {
            int originalCharIndex = originalCharIndexMap.get((char) b);
            value = value.multiply(originalBase) // 乘以进制
                    .add(BigInteger.valueOf(originalCharIndex)); // 加上当前字节Index值
        }
        Map<Integer, Character> convertIndexCharMap = convertIndexCharMap(convertChars);
        conversionToBaseXScale(value, convertIndexCharMap, result);
        byte[] output = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) {
            output[i] = result.get(i);
        }
        return output;


    }


    /**
     * 将大整数值按目标进制转换为字符表示
     *
     * @param value 待转换的大整数值
     * @param charMap 目标进制的索引到字符映射表
     * @param result 转换结果存储列表
     */
    private static void conversionToBaseXScale(BigInteger value, Map<Integer, Character> charMap, List<Byte> result){
        if (value.equals(BigInteger.ZERO)) {
            result.add((byte) charMap.get(0).charValue());
            return;
        }

        List<Byte> items = new LinkedList<>(); // 使用LinkedList提高插入效率
        BigInteger cLength = BigInteger.valueOf(charMap.size());

        while (value.compareTo(BigInteger.ZERO) > 0) {
            int remainder = value.remainder(cLength).intValue();
            value = value.divide(cLength);
            char c = charMap.get(remainder);
            items.addFirst((byte) c);
        }
        result.addAll(items);
    }


}
