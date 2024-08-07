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
    public static <T> boolean checkValueType(Map map, Object key, Class<T> valueType){
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
     * <p><b>{@code @description:}</b> 
     * TODO
     * </p>
     * 
     * <p><b>@param</b> <b>map</b>
     * {@link Map}
     * </p>
     * 
     * <p><b>@param</b> <b>keyPath</b>
     * {@link String}
     * </p>
     * 
     * <p><b>@param</b> <b>defaultValue</b>
     * {@link T}
     * </p>
     * 
     * <p><b>@param</b> <b>clazz</b>
     * {@link Class<T>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/7/10 7:56</p>
     * <p><b>{@code @return:}</b>{@link T}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static <T> T getValueByKeyPath(Map map, String keyPath, T defaultValue, Class<T> clazz){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        String keys[] = keyPath.split("\\.");
        Map valueMap = map;
        int ki = 0;
        int ksl = keys.length;
        for (String key : keys){
            ki ++;
            if(ki == ksl){
                if(clazz == Boolean.class){
                    return (T) MapUtils.getBoolean(valueMap, key, (Boolean) defaultValue);
                }
                if(clazz == Integer.class){
                    return (T) MapUtils.getInteger(valueMap, key, (Integer) defaultValue);
                }
                if(clazz == Long.class){
                    return (T) MapUtils.getLong(valueMap, key, (Long) defaultValue);
                }
                if(clazz == Float.class){
                    return (T) MapUtils.getFloat(valueMap, key, (Float) defaultValue);
                }
                if(clazz == Double.class){
                    return (T) MapUtils.getDouble(valueMap, key, (Double) defaultValue);
                }
                if(clazz == Map.class){
                    return (T) MapUtils.getMap(valueMap, key, (Map) defaultValue);
                }
                if(clazz == List.class){
                    return (T) MapUtils.getList(valueMap, key, (List) defaultValue);
                }
                if(clazz == Set.class){
                    return (T) MapUtils.getSet(valueMap, key, (Set) defaultValue);
                }
                return MapUtils.get(valueMap, key, defaultValue);
            }
            Object value = MapUtils.get(valueMap, key, null);
            if(value == null){
                return defaultValue;
            }
            if(!(value instanceof Map)){
                return defaultValue;
            }
            valueMap = (Map) value;
        }
        return defaultValue;
    }


    /**
     * 获取指定类型的对象
     * @param map
     * @param key
     * @param defaultValue
     * @return
     * @param <T>
     */
    public static <T> T get(Map map, Object key, T defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    public static <T> T get(Map map, Object key) {
        return get(map, key,null);
    }

    public static Object getObject(Map map, Object key, Object defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null){
            return defaultValue;
        }
        return value;
    }
    public static Object getObject(Map map, Object key){
        return getObject(map, key, null);
    }

    public static String getString(Map map, Object key){
        return MapUtils.getString(map, key, null);
    }
    public static String getString(Map map, Object key, String defaultValue){
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
            return DateUtils.formatLocalDateTime((LocalDateTime) value);
        }else if(value instanceof LocalDate){
            return DateUtils.formatLocalDate((LocalDate) value);
        }
        return value.toString();
    }

    public static Float getFloat(Map map, Object key){
        return MapUtils.getFloat(map, key, null);
    }
    public static Float getFloat(Map map, Object key, Float defaultValue){
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

    public static Double getDouble(Map map, Object key){
        return MapUtils.getDouble(map, key, null);
    }
    public static Double getDouble(Map map, Object key, Double defaultValue){
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

    public static Boolean getBoolean(Map map, Object key){
        return MapUtils.getBoolean(map, key, null);
    }
    public static Boolean getBoolean(Map map, Object key, Boolean defaultValue){
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
            return Boolean.valueOf((String) value);
        }
        return Boolean.valueOf(value.toString());
    }

    public static Byte getByte(Map map, Object key){
        return MapUtils.getByte(map, key, null);
    }
    public static Byte getByte(Map map, Object key, Byte defaultValue){
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

    public static Integer getInteger(Map map, Object key){
        return MapUtils.getInteger(map, key, null);
    }
    public static Integer getInteger(Map map, Object key, Integer defaultValue){
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
            return new Integer((int) DateUtils.parseToEpochDay(((LocalDate) value)));
        }else if(value instanceof Long){
            return ((Long) value).intValue();
        }else if(value instanceof Double){
            return ((Double) value).intValue();
        }else if(value instanceof Float){
            return ((Float) value).intValue();
        }
        return new Double(value.toString()).intValue();
    }

    public static Long getLong(Map map, Object key){
        return MapUtils.getLong(map, key, null);
    }
    public static Long getLong(Map map, Object key, Long defaultValue){
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
            return DateUtils.parseToTimestamp(((LocalDateTime) value));
        }else if(value instanceof LocalDate){
            return DateUtils.parseToEpochDay(((LocalDate) value)) * 24 * 3600;
        }else  if(value instanceof Date){
            return ((Date)value).getTime();
        }else if(value instanceof Integer){
            return ((Integer) value).longValue();
        }else if(value instanceof Double){
            return ((Double) value).longValue();
        }else if(value instanceof Float){
            return ((Float) value).longValue();
        }
        return new Double(value.toString()).longValue();
    }

    public static LocalDateTime getLocalDateTime(Map map, Object key){
        return MapUtils.getLocalDateTime(map, key, null);
    }
    public static LocalDateTime getLocalDateTime(Map map, Object key, LocalDateTime defaultValue){
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
            return DateUtils.parseToLocalDateTime((Long) value);
        }else if (value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (String) value);
            if(isMatch){
                return DateUtils.parseToLocalDateTime(new Long((String) value));
            }else{
                return DateUtils.formatToLoaTolDateTime((String) value);
            }
        }
        return null;
    }

    public static LocalDate getLocalDate(Map map, Object key){
        return MapUtils.getLocalDate(map, key, null);
    }
    public static LocalDate getLocalDate(Map map, Object key, LocalDate defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }
        if(value instanceof LocalDate){
            return (LocalDate) value;
        }else if (value instanceof Integer){
            return DateUtils.parseToLocalDate((Integer) value);
        }else if (value instanceof Long){
            return DateUtils.parseToLocalDate((Long) value);
        }else if (value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (String) value);
            if(isMatch){
                return DateUtils.parseToLocalDate(new Long((String) value));
            }else{
                return DateUtils.formatLocalDate((String) value);
            }
        }
        return null;
    }

    public static Date getDate(Map map, Object key){
        return MapUtils.getDate(map, key, null);
    }
    public static Date getDate(Map map, Object key, Date defaultValue){
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

    public static Map getMap(Map map, Object key){
        return MapUtils.getMap(map, key, null);
    }
    public static Map getMap(Map map, Object key, Map defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Map value = get(map, key);
        if(value == null || value.isEmpty()){
            return defaultValue;
        }
        return value;
    }

    public static Object[] getArray(Map map, Object key){
        return MapUtils.getArray(map, key, null);
    }
    public static Object[] getArray(Map map, Object key, Object[] defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object[] value = get(map, key);
        if(value == null || value.length == 0){
            return defaultValue;
        }
        return value;
    }

    public static List getList(Map map, Object key){
        return getList(map, key, null);
    }

    public static List getList(Map map, Object key, List defaultValue){
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


    public static <T> List<T> getListGeneric(Map map, Object key, Class<T> clazz){
        return getListGeneric(map, key, clazz, null);
    }

    public static <T> List<T> getListGeneric(Map map, Object key, Class<T> clazz, List<T> defaultValue){
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

    public static Set getSet(Map map, Object key){
        return getSet(map, key, null);
    }

    public static Set getSet(Map map, Object key, Set defaultValue){
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


    public static <T> Set<T> getSetGeneric(Map map, Object key, Class<T> clazz){
        return getSetGeneric(map, key, clazz, null);
    }

    public static <T> Set<T> getSetGeneric(Map map, Object key, Class<T> clazz, Set<T> defaultValue){
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
