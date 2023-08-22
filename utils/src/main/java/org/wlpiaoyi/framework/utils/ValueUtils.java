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
public final class ValueUtils extends ValueParseUtils{

//    public static void main(String[] args) {
//        long value = 0xF1D1C121;
//        byte[] bytes = toBytes(Math.abs(value));
//        long res = toLong(bytes);
//        System.out.println();
//    }


}

/**
 * value parse to some structure
 * @author wlpiaoyi
 */
class ValueParseUtils extends ValueBlankUtils{


//    public static void main(String[] args) {
////        byte b = (byte) ((0b1 << 7) - 1);
//        for (int j = 1; j <= 7 ; j++) {
//            System.out.println("j=============" + j);
//            for (int i = Byte.MAX_VALUE; i >= Byte.MIN_VALUE ; i--) {
//                byte b = (byte) i;
//                byte b2 = offset(b, j);
//                byte b3 = offset(b2, -j);
//                System.out.println("b:" + b + ", b2:" + b2 +  ", b3:" + b3 + ", flag=" + (b == b3));
//                if(b3 != b){
//                    throw new BusinessException("---------------");
//                }
//            }
//        }
//        byte[] res = toBytes(10000L, 7);
//        System.out.println();
//    }

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
            if(ValueUtils.isBlank((String) value)){
                return defaultValue;
            }
            return new Integer((String) value);
        } else  if(value instanceof StringBuffer) {
            if(ValueUtils.isBlank((StringBuffer) value)){
                return defaultValue;
            }
            return new Integer(((StringBuffer) value).toString());
        } else if(value instanceof StringBuilder) {
            if(ValueUtils.isBlank((StringBuilder) value)){
                return defaultValue;
            }
            return new Integer(((StringBuilder) value).toString());
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
            if(ValueUtils.isBlank((String) value)){
                return defaultValue;
            }
            return new Long((String) value);
        } else if(value instanceof StringBuffer) {
            if(ValueUtils.isBlank((StringBuffer) value)){
                return defaultValue;
            }
            return new Long(((StringBuffer) value).toString());
        } else if(value instanceof StringBuilder) {
            if(ValueUtils.isBlank((StringBuilder) value)){
                return defaultValue;
            }
            return new Long(((StringBuilder) value).toString());
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

    public static String toStrings(Collection values) {
        if (isBlank(values)) {
            return "";
        } else {
            Object[] objs = values.toArray();
            return toStrings(objs);
        }
    }

    public static String toStrings(Object[] values) {
        if (isBlank(values)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            for (Object value : values){
                sb.append(",");
                sb.append(value.toString());
            }
            if(sb.length() == 0){
                return "";
            }
            return sb.substring(1);
        }
    }
}

class ValueBlankUtils extends  ValueTypeUtils{

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
        return value.length() == 0;
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
