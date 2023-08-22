package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Map取值器
 * @author wlpiaoyi
 */
public class MapUtils {

    /**
     * 检查对象类型
     * @param map
     * @param key
     * @param valueType
     * @return
     * @param <T>
     */
    public static final <T> boolean checkValueType(Map map, Object key, Class<T> valueType){
        if(map == null || map.isEmpty()) {
            return false;
        }
        Object value = map.get(key);
        if(value == null) {
            return false;
        }
        return valueType.isInstance(value);
    }

    /**
     * 获取指定类型的对象
     * @param map
     * @param key
     * @param defaultValue
     * @return
     * @param <T>
     */
    public static final <T> T get(Map map, Object key, T defaultValue){
        if(map == null || map.isEmpty()) {
            return  null;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    public static final <T> T get(Map map, Object key) {
        return get(map, key,null);
    }

    public static final Object getObject(Map map, Object key, Object defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null){
            return defaultValue;
        }
        return value;
    }
    public static final Object getObject(Map map, Object key){
        return getObject(map, key, null);
    }

    public static final String getString(Map map, Object key){
        return MapUtils.getString(map, key, null);
    }
    public static final String getString(Map map, Object key, String defaultValue){
        if(map == null || map.isEmpty()) {
            return defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof String){
            return (String) value;
        }else if(value instanceof LocalDateTime){
            return DateUtils.formatToLocalDateTime((LocalDateTime) value);
        }else if(value instanceof LocalDate){
            return DateUtils.formatToLocalDate((LocalDate) value);
        }
        return value.toString();
    }

    public static final Float getFloat(Map map, Object key){
        return MapUtils.getFloat(map, key, null);
    }
    public static final Float getFloat(Map map, Object key, Float defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof Float){
            return (Float) value;
        }else if (value instanceof String){
            return new Float((String) value);
        }
        return new Float(value.toString());
    }

    public static final Double getDouble(Map map, Object key){
        return MapUtils.getDouble(map, key, null);
    }
    public static final Double getDouble(Map map, Object key, Double defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof Double){
            return (Double) value;
        }else if (value instanceof String){
            return new Double((String) value);
        }
        return new Double(value.toString());
    }

    public static final Boolean getBoolean(Map map, Object key){
        return MapUtils.getBoolean(map, key, null);
    }
    public static final Boolean getBoolean(Map map, Object key, Boolean defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof Boolean){
            return (Boolean) value;
        }else if (value instanceof String){
            return new Boolean((String) value);
        }
        return new Boolean(value.toString());
    }

    public static final Byte getByte(Map map, Object key){
        return MapUtils.getByte(map, key, null);
    }
    public static final Byte getByte(Map map, Object key, Byte defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof Byte){
            return (Byte) value;
        }else if (value instanceof String){
            return new Byte((String) value);
        }
        return new Byte(value.toString());
    }

    public static final Integer getInteger(Map map, Object key){
        return MapUtils.getInteger(map, key, null);
    }
    public static final Integer getInteger(Map map, Object key, Integer defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
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
        return MapUtils.getLong(map, key, null);
    }
    public static final Long getLong(Map map, Object key, Long defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
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

    public static final LocalDateTime getLocalDateTime(Map map, Object key){
        return MapUtils.getLocalDateTime(map, key, null);
    }
    public static final LocalDateTime getLocalDateTime(Map map, Object key, LocalDateTime defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof LocalDateTime){
            return (LocalDateTime) value;
        }else if (value instanceof Long){
            return DateUtils.toLocalDateTime((Long) value);
        }else if (value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (String) value);
            if(isMatch){
                return DateUtils.toLocalDateTime(new Long((String) value));
            }else{
                return DateUtils.formatToLoaTolDateTime((String) value);
            }
        }
        return null;
    }

    public static final LocalDate getLocalDate(Map map, Object key){
        return MapUtils.getLocalDate(map, key, null);
    }
    public static final LocalDate getLocalDate(Map map, Object key, LocalDate defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof LocalDate){
            return (LocalDate) value;
        }else if (value instanceof Long){
            return DateUtils.toLocalDate((Long) value);
        }else if (value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (String) value);
            if(isMatch){
                return DateUtils.toLocalDate(new Long((String) value));
            }else{
                return DateUtils.formatToLocalDate((String) value);
            }
        }
        return null;
    }

    public static final Date getDate(Map map, Object key){
        return MapUtils.getDate(map, key, null);
    }
    public static final Date getDate(Map map, Object key, Date defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof Date){
            return (Date) value;
        }else if (value instanceof Long){
            return new Date((Long) value);
        }else if (value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (String) value);
            if(isMatch){
                return new Date(new Long((String) value));
            }else{
                return DateUtils.formatToDate((String) value);
            }
        }
        return null;
    }

    public static final Map getMap(Map map, Object key){
        return MapUtils.getMap(map, key, null);
    }
    public static final Map getMap(Map map, Object key, Map defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Map value = get(map, key);
        if(value == null || value.isEmpty()){
            return defaultValue;
        }
        return value;
    }

    public static final Object[] getArray(Map map, Object key){
        return MapUtils.getArray(map, key, null);
    }
    public static final Object[] getArray(Map map, Object key, Object[] defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object[] value = get(map, key);
        if(value == null || value.length == 0){
            return defaultValue;
        }
        return value;
    }

    public static final List getList(Map map, Object key){
        return getList(map, key, null);
    }

    public static final List getList(Map map, Object key, List defaultValue){
        Object datas = map.get(key);
        if(datas == null){
            return defaultValue;
        }
        if(datas instanceof Object[]){
            if(((Object[]) datas).length == 0){
                return defaultValue;
            }
            return Arrays.asList(((Object[]) datas));
        }
        return (List) datas;
    }


    public static final <T> List<T> getListGeneric(Map map, Object key, Class<T> clazz){
        return getListGeneric(map, key, clazz, null);
    }

    public static final <T> List<T> getListGeneric(Map map, Object key, Class<T> clazz, List<T> defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        List datas = MapUtils.getList(map, key, defaultValue);
        if(datas == null || datas == defaultValue) {
            return defaultValue;
        }
        if(!(datas instanceof List)) {
            return defaultValue;
        }
        if(ValueUtils.isBlank(datas)) {
            return defaultValue;
        }
        Gson gson = GsonBuilder.gsonDefault();
        List<T> items = new ArrayList<>();
        for (Object data : datas){
            T item;
            if(data.getClass() == clazz){
                item = (T) data;
            }else if(clazz == String.class){
                item = (T) data.toString();
            }else if(clazz == Integer.class){
                item = (T) new Integer(data.toString());
            }else if(clazz == Long.class){
                item = (T) new Long(data.toString());
            }else if(clazz == Float.class){
                item = (T) new Float(data.toString());
            }else if(clazz == Double.class){
                item = (T) new Double(data.toString());
            }else if(clazz == Character[].class){
                item = (T) data.toString().toCharArray();
            }else if(clazz == BigDecimal.class){
                item = (T) new BigDecimal(data.toString());
            }else if(clazz == BigInteger.class){
                item = (T) new BigInteger(data.toString());
            }else if(clazz == Map.class){
                if(data instanceof Map) {
                    item = (T) data;
                } else {
                    item = null;
                }
            }else{
                item = gson.fromJson(gson.toJson(data), clazz);
            }
            items.add(item);
        }
        return items;
    }

    public static final Set getSet(Map map, Object key){
        return getSet(map, key, null);
    }

    public static final Set getSet(Map map, Object key, Set defaultValue){
        Object datas = map.get(key);
        if(datas == null){
            return defaultValue;
        }
        if(datas instanceof Object[]){
            if(((Object[]) datas).length == 0){
                return defaultValue;
            }
            return new HashSet(Arrays.asList(((Object[]) datas)));
        }
        return (Set) datas;
    }


    public static final <T> Set<T> getSetGeneric(Map map, Object key, Class<T> clazz){
        return getSetGeneric(map, key, clazz, null);
    }

    public static final <T> Set<T> getSetGeneric(Map map, Object key, Class<T> clazz, Set<T> defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Set datas = MapUtils.getSet(map, key, defaultValue);
        if(datas == null || datas == defaultValue) {
            return defaultValue;
        }
        if(!(datas instanceof Set)) {
            return defaultValue;
        }
        if(ValueUtils.isBlank(datas)) {
            return defaultValue;
        }
        Gson gson = GsonBuilder.gsonDefault();
        Set<T> items = new HashSet<>();
        for (Object data : datas){
            T item;
            if(data.getClass() == clazz){
                item = (T) data;
            }else if(clazz == String.class){
                item = (T) data.toString();
            }else if(clazz == Integer.class){
                item = (T) new Integer(data.toString());
            }else if(clazz == Long.class){
                item = (T) new Long(data.toString());
            }else if(clazz == Float.class){
                item = (T) new Float(data.toString());
            }else if(clazz == Double.class){
                item = (T) new Double(data.toString());
            }else if(clazz == Character[].class){
                item = (T) data.toString().toCharArray();
            }else if(clazz == BigDecimal.class){
                item = (T) new BigDecimal(data.toString());
            }else if(clazz == BigInteger.class){
                item = (T) new BigInteger(data.toString());
            }else if(clazz == Map.class){
                if(data instanceof Map) {
                    item = (T) data;
                }else{
                    item = null;
                }
            }else{
                item = gson.fromJson(gson.toJson(data), clazz);
            }
            items.add(item);
        }
        return items;
    }

//    public static void main(String[] args) {
//        Map map = new HashMap(){{
//           put("dateTime", String.valueOf(DateUtils.toTimestamp(LocalDateTime.now())));
//        }};
//        LocalDateTime dateTime = MapUtils.getLocalDateTime(map, "dateTime");
//        MapUtils.checkValueType(map, "dateTime", LocalDateTime.class);
//        System.out.println();
//    }
}
