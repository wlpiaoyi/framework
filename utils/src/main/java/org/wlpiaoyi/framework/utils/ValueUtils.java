package org.wlpiaoyi.framework.utils;


import org.jetbrains.annotations.NotNull;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


public final class ValueUtils extends ValueParseUtils{

//    public static void main(String[] args) {
//        long value = 0xF1D1C121;
//        byte[] bytes = toBytes(Math.abs(value));
//        long res = toLong(bytes);
//        System.out.println();
//    }


}


class ValueParseUtils extends ValueBlankUtils{

//    public static void main(String[] args) {
//        byte[] res = toBytes(10000L, 7);
//        long v = toLong(res);
//        System.out.println();
//    }

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
            long v = ((long)bytes[i]) + 128;
            v = v << (ci * 8);
            res += v;
            ci ++;
        }
        return res;
    }

    /**
     * Long转化成Byte数组
     * @param value
     * @param length
     * @return
     */
    public static byte @NotNull [] toBytes(long value, int length){
        byte[] lbs = ValueUtils.toBytes(value);
        if(length < 1){
            return lbs;
        }
        final int lbsL = lbs.length;
        if(lbsL == length){
            return lbs;
        }
        byte[] res = new byte[length];
        int offL = length - lbsL;
        for (int i = length - 1; i >= 0; i--){
            if(i < offL){
                res[i] = -128;
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
        final long k = 128;
        if(value <= d){
            return new byte[] {(byte) (value - 128)};
        }
        byte[] temps = new byte[8];
        int i = 0;
        do{
            long v = value - ((value >> c) << c);
            temps[i] = (byte) (v - k);
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
            if(ValueUtils.isBlank((BigDecimal) value)){
                return defaultValue;
            }
            return new Integer((String) value);
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
            return ((Boolean) value).booleanValue() ? 1 : 0;
        } else if(value instanceof Double) {
            return ((Double) value).intValue();
        } else if(value instanceof Float) {
            return ((Float) value).intValue();
        } else {
            return new Integer(String.valueOf(value));
        }
    }


    public static Long toLong(Object value){
        return toLong(value, 0L);
    }

    public static Long toLong(Object value, Long defaultValue){
        if(value == null) {
            return defaultValue;
        }

        if(value instanceof String) {
            if(ValueUtils.isBlank((Long) value)){
                return defaultValue;
            }
            return new Long((String) value);
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
            return ((Boolean) value).booleanValue() ? 1L : 0L;
        } else if(value instanceof Double) {
            return ((Double) value).longValue();
        } else if(value instanceof Float) {
            return ((Float) value).longValue();
        } else {
            return new Long(String.valueOf(value));
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
}

class ValueBlankUtils {

    public interface ValueBlank{
        boolean isBlank();
    }
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


    public static boolean isBlank(String value){
        if(value == null) {
            return true;
        }
        return value.length() == 0;
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

    public static boolean isBlank(Boolean value){
        if(value == null) {
            return true;
        }
        return !value;
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

    public static boolean isBlank(LocalTime value){
        if(value == null) {
            return true;
        }
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
}
