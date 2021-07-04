package org.wlpiaoyi.framework.utils;


import java.math.BigDecimal;
import java.math.BigInteger;

public class ValueUtils {

    /**
     * value是否为空或者无效
     * String 长度为0,基础类型等于0
     * 其他情况除非value是null否则都返回true
     * @param value
     * @param <T>
     * @return
     */
    public static <T> boolean isBlank(T value){
        if(value == null) return true;
        if(value instanceof String) {
            String arg = (String) value;
            return arg.length() == 0;
        }
        if(value instanceof Long){
            return (Long)value == 0;
        }
        if(value instanceof Boolean){
            return (Boolean) value == false;
        }
        if(value instanceof Double){
            return (Double)value == 0;
        }
        if(value instanceof Integer){
            return (Integer)value == 0;
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
        return false;
    }
}
