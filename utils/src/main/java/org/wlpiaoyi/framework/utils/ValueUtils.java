package org.wlpiaoyi.framework.utils;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public class ValueUtils {

    /**
     * value是否为空或者无效
     * 1.String 长度为0
     * 2.基础类型等于0
     * 3.集合、数组等类型空
     * 其他情况除非value是null否则都返回true
     * @param value
     * @param <T>
     * @return
     */
    public static <T> boolean isBlank(T value){
        if(value == null) return true;
        if(value instanceof String) {
            return ((String) value).length() == 0;
        }
        if(value instanceof Long){
            return (Long)value == 0;
        }
        if(value instanceof Integer){
            return (Integer)value == 0;
        }
        if(value instanceof Boolean){
            return (Boolean)value == false;
        }
        if(value instanceof Double){
            return (Double)value == 0;
        }
        if(value instanceof Float){
            return (Float)value == 0;
        }
        if(value instanceof Short){
            return (Short)value == 0;
        }
        if(value instanceof BigDecimal){
            return ((BigDecimal) value).doubleValue() == 0;
        }
        if(value instanceof BigInteger){
            return ((BigInteger) value).intValue() == 0;
        }
        if(value instanceof Collection){
            return ((Collection) value).isEmpty();
        }
        if(value instanceof Map){
            return ((Map) value).isEmpty();
        }
        if(value.getClass().isArray()) {
            T[] values = (T[]) value;
            return values.length == 0;
        }
        return false;
    }
}
