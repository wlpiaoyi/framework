package org.wlpiaoyi.framework.utils;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;


public final class ValueUtils {

    public interface ValueBlank{
        boolean isBlank();
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

    /**
     * value是否为空或者无效
     * 1.String 长度为0
     * 2.基础类型等于0
     * 3.集合、数组等类型空
     * 其他情况除非value是null否则都返回true
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> boolean isBlankObject(T obj){
        if(obj == null) return true;
        if(obj instanceof String) {
            return ValueUtils.isBlank((String) obj);
        }
        if(obj instanceof Integer){
            return ValueUtils.isBlank((Integer) obj);
        }
        if(obj instanceof Long){
            return ValueUtils.isBlank((Long) obj);
        }
        if(obj instanceof Double){
            return ValueUtils.isBlank((Double) obj);
        }
        if(obj instanceof Float){
            return ValueUtils.isBlank((Float) obj);
        }
        if(obj instanceof Short){
            return ValueUtils.isBlank((Short) obj);
        }
        if(obj instanceof Byte){
            return ValueUtils.isBlank((Byte) obj);
        }
        if(obj instanceof Boolean){
            return ValueUtils.isBlank((Boolean) obj);
        }
        if(obj instanceof BigDecimal){
            return ValueUtils.isBlank((BigDecimal) obj);
        }
        if(obj instanceof BigInteger){
            return ValueUtils.isBlank((BigInteger) obj);
        }
        if(obj instanceof Collection){
            return ValueUtils.isBlank((Collection) obj);
        }
        if(obj instanceof Map){
            return ValueUtils.isBlank((Map) obj);
        }
        if(obj instanceof LocalTime){
            return ValueUtils.isBlank((LocalTime) obj);
        }
        if(obj instanceof LocalDate){
            return ValueUtils.isBlank((LocalDate) obj);
        }
        if(obj instanceof LocalDateTime){
            return ValueUtils.isBlank((LocalDateTime) obj);
        }
        if(obj instanceof Date){
            return ValueUtils.isBlank((Date) obj);
        }
        if(obj.getClass().isArray()) {
            if(obj == null) return true;
            return ((T[])obj).length == 0;
        }
        if(obj instanceof ValueBlank){
            return ((ValueBlank) obj).isBlank();
        }
        return false;
    }
}
