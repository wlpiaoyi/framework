package org.wlpiaoyi.framework.utils;


import org.jetbrains.annotations.NotNull;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


/**
 * value function
 * @author wlpiaoyi
 */
public final class ValueUtils extends ValueEqualsUtils{

//    public static void main(String[] args) {
//        long value = 240;
//        byte[] bytes = toBytes(Math.abs(value), 8);
//        long res = toLong(bytes);
//        System.out.println();
//    }


}

class ValueEqualsUtils extends ValueParseUtils{
    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        } else if (o1 != null && o2 != null) {
            if (o1.equals(o2)) {
                return true;
            } else {
                return o1.getClass().isArray() && o2.getClass().isArray() ? arrayEquals(o1, o2) : false;
            }
        } else {
            return false;
        }
    }
    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[])o1, (Object[])o2);
        } else if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[])o1, (boolean[])o2);
        } else if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[])o1, (byte[])o2);
        } else if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[])o1, (char[]) o2);
        } else if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[])o1, (double[])o2);
        } else if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[])o1, (float[])o2);
        } else if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[])o1, (int[])o2);
        } else if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[])o1, (long[])o2);
        } else {
            return o1 instanceof short[] && o2 instanceof short[] ? Arrays.equals((short[])o1, (short[])o2) : false;
        }
    }
}

/**
 * value parse to some structure
 * @author wlpiaoyi
 */
class ValueParseUtils extends ValueBlankUtils{

    final protected static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String bytesToHex(byte[] bytes, int startIndex, int len) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            if(startIndex < j){
                continue;
            }
            if(j - startIndex <= len){
                continue;
            }
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String s) {
        String us = s.toUpperCase(Locale.ROOT);
        int len = us.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            b[i / 2] = (byte) ((Character.digit(us.charAt(i), 16) << 4) + Character
                    .digit(us.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * byte偏移
     * @param data
     * @param offi
     * @return
     */
    public static byte offset(byte data, int offi){
        final int iv = data + 128;
        if(Math.abs(offi) > 7){
            throw new RuntimeException("offi can't lt 7");
        }
        if(offi > 0){
            final int offi2 = 8 - offi;
            final int offv =  ((0b1 << offi ) - 1);
            int b_res = (((iv & offv) << offi2) | (iv >> offi));
            return ((byte)(b_res - 128));
        }else if (offi < 0){
            offi = -offi;
            final int offi2 = 8 - offi;
            int b_res = (((iv & (((0b1 << offi ) - 1) << offi2)) >> offi2) | ((iv & ((0b1 << offi2 ) - 1)) << offi));
            return ((byte)(b_res - 128));
        }else{
            throw new RuntimeException("offi can't be zero");
        }
    }

    /**
     * byte数组转成Long
     * @param bytes
     * @return
     */
    public static long toLong(byte @NotNull [] bytes){
        int pow = bytes.length;
        long res = 0;
        int ci = 0;
        for (int i = pow - 1; i >= 0; i --){
            long v = bytes[i];
            if(v < 0){
                v = 256 + v;
            }
            v = v << (ci * 8);
            res += v;
            ci ++;
        }
        return res;
    }

    /**
     * Long转化成Byte数组
     * @param value
     * @param minLen
     * @return
     */
    public static byte @NotNull [] toBytes(long value, int minLen){
        byte[] lbs = ValueUtils.toBytes(value);
        if(minLen < 1){
            return lbs;
        }
        if(minLen > 8){
            minLen = 8;
        }
        final int lbsL = lbs.length;
        if(lbsL == minLen){
            return lbs;
        }
        byte[] res = new byte[minLen];
        int offL = minLen - lbsL;
        for (int i = minLen - 1; i >= 0; i--){
            if(i < offL){
                res[i] = 0;
            }else{
                res[i] = lbs[i - offL];
            }
        }
        return res;

    }

    /**
     * Long转化成Byte数组
     * @param value
     * @return
     */
    public static byte @NotNull [] toBytes(long value){
        if(value < 0){
            throw new BusinessException("this value must be unsigned");
        }
        final long d = 0xFFL;
        final int c = 8;
        if(value <= d){
            return new byte[] {(byte)value};
        }
        byte[] temps = new byte[8];
        int i = 0;
        do{
            long v = value - ((value >> c) << c);
            temps[i] = (byte) v;
            if(value < d){
                break;
            }
            value = value >> c;
            i ++;
        }while (i < d);

        byte[] res = new byte[i + 1];
        do{
            res[res.length - (i + 1)] = temps[i];
            i --;
        }while (i >= 0);
        return res;
    }

    public static String toString(Object value){
        return toString(value, "");
    }

    public static String toString(Object value, String defaultValue){
        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }

    public static Integer toInteger(Object value){
        return toInteger(value, 0);
    }

    public static Integer toInteger(Object value, Integer defaultValue){
        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            if(ValueUtils.isBlank((String) value)){
                return defaultValue;
            }
            return Integer.valueOf((String) value);
        } else  if(value instanceof StringBuffer) {
            if(ValueUtils.isBlank((StringBuffer) value)){
                return defaultValue;
            }
            return Integer.valueOf(((StringBuffer) value).toString());
        } else if(value instanceof StringBuilder) {
            if(ValueUtils.isBlank((StringBuilder) value)){
                return defaultValue;
            }
            return Integer.valueOf(((StringBuilder) value).toString());
        } else if(value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        } else if(value instanceof BigInteger) {
            return ((BigInteger) value).intValue();
        } else if(value instanceof Long) {
            return ((Long) value).intValue();
        } else if(value instanceof Short) {
            return ((Short) value).intValue();
        } else if(value instanceof Byte) {
            return ((Byte) value).intValue();
        } else if(value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        } else if(value instanceof Double) {
            return ((Double) value).intValue();
        } else if(value instanceof Float) {
            return ((Float) value).intValue();
        } else {
            return Integer.valueOf(String.valueOf(value));
        }
    }


    public static Long toLong(Object value){
        return toLong(value, 0L);
    }

    public static Long toLong(Object value, Long defaultValue){
        if(value == null) {
            return defaultValue;
        }
        if(ValueUtils.isBlank(value)) {
            return defaultValue;
        }
        if(value instanceof Long) {
            return (Long) value;
        }

        if(value instanceof String) {
            if(ValueUtils.isBlank((String) value)){
                return defaultValue;
            }
            return Long.valueOf((String) value);
        } else if(value instanceof StringBuffer) {
            if(ValueUtils.isBlank((StringBuffer) value)){
                return defaultValue;
            }
            return Long.valueOf(((StringBuffer) value).toString());
        } else if(value instanceof StringBuilder) {
            if(ValueUtils.isBlank((StringBuilder) value)){
                return defaultValue;
            }
            return Long.valueOf(((StringBuilder) value).toString());
        } else if(value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        } else if(value instanceof BigInteger) {
            return ((BigInteger) value).longValue();
        } else if(value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if(value instanceof Short) {
            return ((Short) value).longValue();
        } else if(value instanceof Byte) {
            return ((Byte) value).longValue();
        } else if(value instanceof Boolean) {
            return (Boolean) value ? 1L : 0L;
        } else if(value instanceof Double) {
            return ((Double) value).longValue();
        } else if(value instanceof Float) {
            return ((Float) value).longValue();
        } else {
            return Long.valueOf(String.valueOf(value));
        }
    }


    public static List<String> toStringList(String str) {
        return Arrays.asList(toStringArray(str));
    }

    public static String[] toStringArray(String str) {
        return toStringArray(",", str);
    }

    public static String[] toStringArray(String split, String str) {
        if (isBlank(str)) {
            return new String[0];
        } else {
            String[] arr = str.split(split);
            return arr;
        }
    }

    public static List<Integer> toIntegerList(String str) {
        return Arrays.asList(toIntegerArray(str));
    }

    public static Integer[] toIntegerArray(String str) {
        return toIntegerArray(",", str);
    }

    public static Integer[] toIntegerArray(String split, String str) {
        if (isBlank(str)) {
            return new Integer[0];
        } else {
            String[] arr = str.split(split);
            Integer[] longs = new Integer[arr.length];

            for(int i = 0; i < arr.length; ++i) {
                Integer v = toInteger(arr[i]);
                longs[i] = v;
            }
            return longs;
        }
    }

    public static List<Long> toLongList(String str) {
        return Arrays.asList(toLongArray(str));
    }

    public static Long[] toLongArray(String str) {
        return toLongArray(",", str);
    }

    public static Long[] toLongArray(String split, String str) {
        if (isBlank(str)) {
            return new Long[0];
        } else {
            String[] arr = str.split(split);
            Long[] longs = new Long[arr.length];

            for(int i = 0; i < arr.length; ++i) {
                Long v = toLong(arr[i]);
                longs[i] = v;
            }

            return longs;
        }
    }

    public static String toStrings(Collection values) {
        return toStrings(values, ",");
    }

    public static String toStrings(Collection values, String splitTag) {
        if (isBlank(values)) {
            return "";
        } else {
            Object[] objs = values.toArray();
            return toStrings(objs, splitTag);
        }
    }

    public static String toStrings(Object[] values) {
        return toStrings(values, ",");
    }
    public static String toStrings(Object[] values, String splitTag) {
        if (isBlank(values)) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Object value : values){
                sb.append(splitTag);
                sb.append(value.toString());
            }
            if(sb.length() == 0){
                return "";
            }
            return sb.substring(1);
        }
    }
}

class ValueBlankUtils extends ValueTypeUtils{

//    public interface ValueBlank{
//        boolean isBlank();
//    }

    public static boolean isBlank(Object value){
        if(value instanceof String){
            return isBlank((String) value);
        }
        if(value instanceof StringBuffer){
            return isBlank((StringBuffer) value);
        }
        if(value instanceof StringBuilder){
            return isBlank((StringBuilder) value);
        }
        if(value instanceof Number){
            return isBlank((Number) value);
        }
        if(value instanceof Collection){
            return isBlank((Collection) value);
        }
        if(value instanceof Map){
            return isBlank((Map) value);
        }
        if(value instanceof BigDecimal){
            return isBlank((BigDecimal) value);
        }
        if(value instanceof BigInteger){
            return isBlank((BigInteger) value);
        }
        return value == null;

    }
    public static boolean isBlank(String value){
        if(value == null) {
            return true;
        }
        return value.isEmpty();
    }
    public static boolean isBlank(StringBuffer value){
        if(value == null) {
            return true;
        }
        return value.length() == 0;
    }
    public static boolean isBlank(StringBuilder value){
        if(value == null) {
            return true;
        }
        return value.length() == 0;
    }
    public static boolean isBlank(Number value){
        if(value instanceof Byte){
            return isBlank((Byte) value);
        }
        if(value instanceof Short){
            return isBlank((Short) value);
        }
        if(value instanceof Integer){
            return isBlank((Integer) value);
        }
        if(value instanceof Long){
            return isBlank((Long) value);
        }
        if(value instanceof Float){
            return isBlank((Float) value);
        }
        if(value instanceof Double){
            return isBlank((Double) value);
        }
        return value.longValue() == 0;
    }
    public static boolean isBlank(Boolean value){
        if(value == null) {
            return true;
        }
        return !value;
    }
    public static boolean isBlank(Short value){
        if(value == null) {
            return true;
        }
        return value == 0;
    }
    public static boolean isBlank(Byte value){
        if(value == null) {
            return true;
        }
        return value == 0;
    }
    public static boolean isBlank(Integer value){
        if(value == null) {
            return true;
        }
        return value == 0;
    }
    public static boolean isBlank(Long value){
        if(value == null) {
            return true;
        }
        return value == 0;
    }
    public static boolean isBlank(Double value){
        if(value == null) {
            return true;
        }
        return value == 0;
    }
    public static boolean isBlank(Float value){
        if(value == null) {
            return true;
        }
        return value == 0;
    }
    public static boolean isBlank(BigDecimal value){
        if(value == null) {
            return true;
        }
        return value.doubleValue() == 0;
    }

    public static boolean isBlank(BigInteger value){
        if(value == null) {
            return true;
        }
        return value.longValue() == 0;
    }

    public static boolean isBlank(Collection value){
        if(value == null) {
            return true;
        }
        return value.isEmpty();
    }

    public static boolean isBlank(Map value){
        if(value == null) {
            return true;
        }
        return value.isEmpty();
    }

    public static boolean isBlank(boolean[] value){
        if(value == null) {
            return true;
        }
        return value.length == 0;
    }
    public static boolean isBlank(byte[] value){
        if(value == null) {
            return true;
        }
        return value.length == 0;
    }
    public static boolean isBlank(short[] value){
        if(value == null) {
            return true;
        }
        return value.length == 0;
    }
    public static boolean isBlank(int[] value){
        if(value == null) {
            return true;
        }
        return value.length == 0;
    }
    public static boolean isBlank(long[] value){
        if(value == null) {
            return true;
        }
        return value.length == 0;
    }
    public static boolean isBlank(double[] value){
        if(value == null) {
            return true;
        }
        return value.length == 0;
    }
    public static boolean isBlank(float[] value){
        if(value == null) {
            return true;
        }
        return value.length == 0;
    }
    public static boolean isBlank(char[] value){
        if(value == null) {
            return true;
        }
        return value.length == 0;
    }
    public static <T> boolean isBlank(T[] value){
        if(value == null) {
            return true;
        }
        return value.length == 0;
    }

    public static boolean isNotBlank(Object value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(String value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(StringBuilder value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(StringBuffer value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Boolean value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Number value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Byte value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Short value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Integer value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Long value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Double value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Float value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(BigDecimal value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(BigInteger value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Collection value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(Map value){
        return !isBlank(value);
    }
    public static boolean isNotBlank(boolean[] value){
        return !isBlank(value);
    }

    public static boolean isNotBlank(byte[] value){
        return !isBlank(value);
    }

    public static boolean isNotBlank(short[] value){
        return !isBlank(value);
    }

    public static boolean isNotBlank(int[] value){
        return !isBlank(value);
    }

    public static boolean isNotBlank(long[] value){
        return !isBlank(value);
    }

    public static boolean isNotBlank(float[] value){
        return !isBlank(value);
    }

    public static boolean isNotBlank(double[] value){
        return !isBlank(value);
    }

    public static boolean isNotBlank(char[] value){
        return !isBlank(value);
    }

    public static <T> boolean isNotBlank(T[] value){
        return !isBlank(value);
    }


}


class ValueTypeUtils {

    /**
     * check value is number type
     * @param value
     * @return
     */
    public static boolean isNumber(Object value){
        if(value instanceof String) {
            return PatternUtils.isNumber((String) value);
        }
        if(value instanceof Long) {
            return true;
        }
        if(value instanceof Integer) {
            return true;
        }
        if(value instanceof Short) {
            return true;
        }
        if(value instanceof Byte) {
            return true;
        }
        if(value instanceof Float) {
            return true;
        }
        if(value instanceof Double) {
            return true;
        }
        if(value instanceof BigDecimal) {
            return true;
        }
        if(value instanceof BigInteger) {
            return true;
        }
        return false;
    }
}
