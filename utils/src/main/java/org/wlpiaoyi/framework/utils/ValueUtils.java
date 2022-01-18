package org.wlpiaoyi.framework.utils;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;


public final class ValueUtils {

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

    public static boolean isBlank(byte[] value){
        if(value == null) return true;
        return value.length == 0;
    }

    public static <T> boolean isBlank(T[] value){
        if(value == null) return true;
        return value.length == 0;
    }

//    /**
//     * value是否为空或者无效
//     * 1.String 长度为0
//     * 2.基础类型等于0
//     * 3.集合、数组等类型空
//     * 其他情况除非value是null否则都返回true
//     * @param value
//     * @param <T>
//     * @return
//     */
//    public static <T> boolean isBlank(T value){
//        if(value == null) return true;
//        if(value instanceof String) {
//            return ((String) value).length() == 0;
//        }
//        if(value instanceof Long){
//            return (Long)value == 0;
//        }
//        if(value instanceof Integer){
//            return (Integer)value == 0;
//        }
//        if(value instanceof Boolean){
//            return (Boolean)value == false;
//        }
//        if(value instanceof Double){
//            return (Double)value == 0;
//        }
//        if(value instanceof Float){
//            return (Float)value == 0;
//        }
//        if(value instanceof Short){
//            return (Short)value == 0;
//        }
//        if(value instanceof Byte){
//            return (Byte)value == 0;
//        }
//        if(value instanceof BigDecimal){
//            return ((BigDecimal) value).doubleValue() == 0;
//        }
//        if(value instanceof BigInteger){
//            return ((BigInteger) value).intValue() == 0;
//        }
//        if(value instanceof Collection){
//            return ((Collection) value).isEmpty();
//        }
//        if(value instanceof Map){
//            return ((Map) value).isEmpty();
//        }
//        if(value.getClass().isArray()) {
//            T[] values = (T[]) value;
//            return values.length == 0;
//        }
//        return false;
//    }
}
