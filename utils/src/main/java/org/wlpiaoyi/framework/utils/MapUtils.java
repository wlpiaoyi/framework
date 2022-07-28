package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Map取值器
 * @author wlpiaoyi
 */
public class MapUtils {

    public static final <T> T get(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        return (T) value;
    }

    public static final Object getObject(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        return value;
    }

    public static final String getString(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        if(value instanceof String){
            return (String) value;
        }else if(value instanceof LocalDateTime){
            return DateUtils.formatLocalDateTime((LocalDateTime) value);
        }else if(value instanceof LocalDate){
            return DateUtils.formatLocalDate((LocalDate) value);
        }
        return value.toString();
    }

    public static final Float getFloat(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        if(value instanceof Float){
            return (Float) value;
        }else if (value instanceof String){
            return new Float((String) value);
        }
        return new Float(value.toString());
    }

    public static final Double getDouble(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        if(value instanceof Double){
            return (Double) value;
        }else if (value instanceof String){
            return new Double((String) value);
        }
        return new Double(value.toString());
    }

    public static final Boolean getBoolean(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        if(value instanceof Boolean){
            return (Boolean) value;
        }else if (value instanceof String){
            return new Boolean((String) value);
        }
        return new Boolean(value.toString());
    }

    public static final Byte getByte(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        if(value instanceof Byte){
            return (Byte) value;
        }else if (value instanceof String){
            return new Byte((String) value);
        }
        return new Byte(value.toString());
    }

    public static final Integer getInteger(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        if(value instanceof Integer){
            return (Integer) value;
        }else if(value instanceof LocalDate){
            return new Integer((int) DateUtils.toEpochDay(((LocalDate) value)));
        }else if(value instanceof Long){
            return ((Long) value).intValue();
        }else if(value instanceof Double){
            return ((Double) value).intValue();
        }else if(value instanceof Float){
            return ((Float) value).intValue();
        }
        return new Double(value.toString()).intValue();
    }


    public static final Long getLong(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        if(value instanceof Long){
            return (Long) value;
        }else if(value instanceof LocalDateTime){
            return DateUtils.toTimestamp(((LocalDateTime) value));
        }else if(value instanceof LocalDate){
            return DateUtils.toEpochDay(((LocalDate) value)) * 24 * 3600;
        }else if(value instanceof Integer){
            return ((Integer) value).longValue();
        }else if(value instanceof Double){
            return ((Double) value).longValue();
        }else if(value instanceof Float){
            return ((Float) value).longValue();
        }
        return new Double(value.toString()).longValue();
    }

    public static final LocalDateTime getDateTime(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        if(value instanceof LocalDateTime){
            return (LocalDateTime) value;
        }else if (value instanceof Long){
            return DateUtils.toLocalDateTime((Long) value);
        }else if (value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (String) value);
            if(isMatch){
                return DateUtils.toLocalDateTime(new Long((String) value));
            }else{
                return DateUtils.parseLocalDateTime((String) value);
            }
        }
        return null;
    }

    public static final LocalDate getDate(Map map, Object key){
        if(map == null || map.isEmpty()) return  null;
        Object value = map.get(key);
        if(value == null) return null;
        if(value instanceof LocalDate){
            return (LocalDate) value;
        }else if (value instanceof Long){
            return DateUtils.toLocalDate((Long) value);
        }else if (value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (String) value);
            if(isMatch){
                return DateUtils.toLocalDate(new Long((String) value));
            }else{
                return DateUtils.parseLocalDate((String) value);
            }
        }
        return null;
    }

    public static final <T> List<T> getList(Map map, Object key, Class<T> clazz){
        if(map == null || map.isEmpty()) return  null;
        List datas = MapUtils.get(map, key);
        if(datas == null) return null;
        if(!(datas instanceof List)) return null;
        if(ValueUtils.isBlank(datas)) return null;
        Gson gson = GsonBuilder.gsonDefault();
        List<T> items = new ArrayList<>();
        for (Object data : datas){
            T item = gson.fromJson(gson.toJson(data), clazz);
            items.add(item);
        }
        return items;
    }

    public static final <T> Set<T> getSet(Map map, Object key, Class<T> clazz){
        if(map == null || map.isEmpty()) return  null;
        Set datas = MapUtils.get(map, key);
        if(datas == null) return null;
        if(!(datas instanceof Set)) return null;
        if(ValueUtils.isBlank(datas)) return null;
        Gson gson = GsonBuilder.gsonDefault();
        Set<T> items = new HashSet<>();
        for (Object data : datas){
            T item = gson.fromJson(gson.toJson(data), clazz);
            items.add(item);
        }
        return items;
    }

//    public static void main(String[] args) {
//        Map map = new HashMap(){{
//           put("dateTime", String.valueOf(DateUtils.toTimestamp(LocalDateTime.now())));
//        }};
//        LocalDateTime dateTime = MapUtils.getDateTime(map, "dateTime");
//        System.out.println();
//    }
}
