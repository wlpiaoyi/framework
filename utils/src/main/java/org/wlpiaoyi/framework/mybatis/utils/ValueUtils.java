package org.wlpiaoyi.framework.mybatis.utils;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


public final class ValueUtils extends ValueParseUtils{
}


class ValueParseUtils extends ValueBlankUtils{

    public static String toString(Object value){
        return toString(value, "");
    }

    public static String toString(Object value, String defaultValue){
        if(value == null)
            return defaultValue;

        if(value instanceof String)
            return (String) value;
        else
            return value.toString();
    }

    public static Integer toInteger(Object value){
        return toInteger(value, 0);
    }

    public static Integer toInteger(Object value, Integer defaultValue){
        if(value == null)
            return defaultValue;

        if(value instanceof String)
            return new Integer((String) value);
        else if(value instanceof BigDecimal)
            return ((BigDecimal) value).intValue();
        else if(value instanceof BigInteger)
            return ((BigInteger) value).intValue();
        else if(value instanceof Long)
            return ((Long) value).intValue();
        else if(value instanceof Short)
            return ((Short) value).intValue();
        else if(value instanceof Byte)
            return ((Byte) value).intValue();
        else if(value instanceof Boolean)
            return ((Boolean) value).booleanValue() ? 1 : 0;
        else if(value instanceof Double)
            return ((Double) value).intValue();
        else if(value instanceof Float)
            return ((Float) value).intValue();
        else
            return new Integer(String.valueOf(value));
    }


    public static Long toLong(Object value){
        return toLong(value, 0L);
    }

    public static Long toLong(Object value, Long defaultValue){
        if(value == null)
            return defaultValue;

        if(value instanceof String)
            return new Long((String) value);
        else if(value instanceof BigDecimal)
            return ((BigDecimal) value).longValue();
        else if(value instanceof BigInteger)
            return ((BigInteger) value).longValue();
        else if(value instanceof Integer)
            return ((Integer) value).longValue();
        else if(value instanceof Short)
            return ((Short) value).longValue();
        else if(value instanceof Byte)
            return ((Byte) value).longValue();
        else if(value instanceof Boolean)
            return ((Boolean) value).booleanValue() ? 1L : 0L;
        else if(value instanceof Double)
            return ((Double) value).longValue();
        else if(value instanceof Float)
            return ((Float) value).longValue();
        else
            return new Long(String.valueOf(value));
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
}

class ValueBlankUtils {

    public interface ValueBlank{
        boolean isBlank();
    }
    public static boolean isNumber(Object value){
        if(value instanceof String)
            return PatternUtils.isNumber((String) value);
        if(value instanceof Long)
            return true;
        if(value instanceof Integer)
            return true;
        if(value instanceof Short)
            return true;
        if(value instanceof Byte)
            return true;
        if(value instanceof Float)
            return true;
        if(value instanceof Double)
            return true;
        if(value instanceof BigDecimal)
            return true;
        if(value instanceof BigInteger)
            return true;
        return false;
    }


    public static boolean isBlank(String value){
        if(value == null) return true;
        return value.length() == 0;
    }

    public static boolean isBlank(Integer value){
        if(value == null) return true;
        return value == 0;
    }

    public static boolean isBlank(Long value){
        if(value == null) return true;
        return value == 0;
    }

    public static boolean isBlank(Double value){
        if(value == null) return true;
        return value == 0;
    }

    public static boolean isBlank(Float value){
        if(value == null) return true;
        return value == 0;
    }

    public static boolean isBlank(Short value){
        if(value == null) return true;
        return value == 0;
    }

    public static boolean isBlank(Byte value){
        if(value == null) return true;
        return value == 0;
    }

    public static boolean isBlank(Boolean value){
        if(value == null) return true;
        return !value;
    }

    public static boolean isBlank(BigDecimal value){
        if(value == null) return true;
        return value.doubleValue() == 0;
    }

    public static boolean isBlank(BigInteger value){
        if(value == null) return true;
        return value.longValue() == 0;
    }

    public static boolean isBlank(Collection value){
        if(value == null) return true;
        return value.isEmpty();
    }

    public static boolean isBlank(Map value){
        if(value == null) return true;
        return value.isEmpty();
    }

    public static boolean isBlank(LocalTime value){
        if(value == null) return true;
        return value.getNano() == 0;
    }

    public static boolean isBlank(LocalDate value){
        return value == null;
    }

    public static boolean isBlank(LocalDateTime value){
        return value == null;
    }

    public static boolean isBlank(Date value){
        return value == null;
    }

    public static boolean isBlank(byte[] value){
        if(value == null) return true;
        return value.length == 0;
    }

    public static <T> boolean isBlank(T[] value){
        if(value == null) return true;
        return value.length == 0;
    }
}
