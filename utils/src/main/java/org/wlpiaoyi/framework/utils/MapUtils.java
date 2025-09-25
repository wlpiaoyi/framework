package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
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
        Object valueData = map;
        int ki = 0;
        int ksl = keys.length;
        for (String key : keys){
            ki ++;
            if (!(valueData instanceof Map<?,?>)){
                throw new IllegalArgumentException("valueData is not a Map type: " + valueData.getClass().getName());
            }
            int index;
            if((index = key.indexOf("[")) > 0 && key.endsWith("]")){
                String k = key.substring(0, index);
                index = Integer.parseInt(key.substring(index + 1, key.length() - 1));
                key = k;
            }else index = -1;
            if(index > 0){
                Object value = ((Map) valueData).get(key);
                if (value == null) {
                    throw new NullPointerException("Value for key '" + key + "' is null");
                }
                if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    if (index < 0 || index >= list.size()) {
                        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list size " + list.size());
                    }
                    valueData = list.get(index);
                } else if (value instanceof Collection) {
                    Collection<?> collection = (Collection<?>) value;
                    if (index < 0 || index >= collection.size()) {
                        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for collection size " + collection.size());
                    }
                    // 转换为List以便通过索引访问
                    valueData = new ArrayList<>(collection).get(index);
                } else if (value != null && value.getClass().isArray()) {
                    int length = Array.getLength(value);
                    if (index < 0 || index >= length) {
                        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for array length " + length);
                    }
                    valueData = Array.get(value, index);
                } else if (value instanceof Iterable) {
                    // 对于其他Iterable类型，转换为List
                    List<Object> list = new ArrayList<>();
                    for (Object item : (Iterable<?>) value) {
                        list.add(item);
                    }
                    if (index < 0 || index >= list.size()) {
                        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for iterable size " + list.size());
                    }
                    valueData = list.get(index);
                } else {
                    throw new IllegalArgumentException("Value is not a collection or array type: " + value.getClass().getName());
                }
            }else {
                valueData = ((Map) valueData).get(key);
            }
            if(valueData == null){
                return defaultValue;
            }
            if(ki == ksl){
                if (clazz == Boolean.class) {
                    if (valueData instanceof String) {
                        valueData = Boolean.valueOf(valueData.toString());
                    } else if (valueData instanceof Number) {
                        valueData = ((Number) valueData).intValue() != 0;
                    } else if (valueData instanceof Boolean) {
                        // 已经是Boolean类型，无需转换
                        valueData = (Boolean) valueData;
                    } else {
                        throw new IllegalArgumentException("Boolean conversion failed - valueData is not a String, Number or Boolean: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == Integer.class) {
                    if (valueData instanceof Number) {
                        valueData = ((Number) valueData).intValue();
                    } else if (valueData instanceof String) {
                        try {
                            valueData = Integer.parseInt(valueData.toString().trim());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Integer conversion failed - invalid string format: " + valueData, e);
                        }
                    } else {
                        throw new IllegalArgumentException("Integer conversion failed - valueData is not a Number or String: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == Long.class) {
                    if (valueData instanceof Number) {
                        valueData = ((Number) valueData).longValue();
                    } else if (valueData instanceof String) {
                        try {
                            valueData = Long.parseLong(valueData.toString().trim());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Long conversion failed - invalid string format: " + valueData, e);
                        }
                    } else {
                        throw new IllegalArgumentException("Long conversion failed - valueData is not a Number or String: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == Float.class) {
                    if (valueData instanceof Number) {
                        valueData = ((Number) valueData).floatValue();
                    } else if (valueData instanceof String) {
                        try {
                            valueData = Float.parseFloat(valueData.toString().trim());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Float conversion failed - invalid string format: " + valueData, e);
                        }
                    } else {
                        throw new IllegalArgumentException("Float conversion failed - valueData is not a Number or String: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == Double.class) {
                    if (valueData instanceof Number) {
                        valueData = ((Number) valueData).doubleValue();
                    } else if (valueData instanceof String) {
                        try {
                            valueData = Double.parseDouble(valueData.toString().trim());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Double conversion failed - invalid string format: " + valueData, e);
                        }
                    } else {
                        throw new IllegalArgumentException("Double conversion failed - valueData is not a Number or String: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == String.class) {
                    valueData = valueData.toString();
                } else if (clazz == BigDecimal.class) {
                    if (valueData instanceof Number) {
                        valueData = BigDecimal.valueOf(((Number) valueData).doubleValue());
                    } else if (valueData instanceof String) {
                        try {
                            valueData = new BigDecimal(valueData.toString().trim());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("BigDecimal conversion failed - invalid string format: " + valueData, e);
                        }
                    } else {
                        throw new IllegalArgumentException("BigDecimal conversion failed - valueData is not a Number or String: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == Map.class) {
                    if (valueData instanceof Map) {
                        // 已经是Map类型，无需转换
                        valueData = (Map<?, ?>) valueData;
                    } else if (valueData instanceof String) {
                        try {
                            Gson gson = GsonBuilder.gsonDefault();
                            // 尝试解析JSON字符串为Map
                            valueData = gson.fromJson(valueData.toString(), Map.class);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Map conversion failed - invalid JSON string: " + valueData, e);
                        }
                    } else {
                        throw new IllegalArgumentException("Map conversion failed - valueData is not a Map or JSON String: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == List.class) {
                    if (valueData instanceof List) {
                        // 已经是List类型，无需转换
                        valueData = (List<?>) valueData;
                    } else if (valueData instanceof Collection) {
                        // 其他Collection类型转换为List
                        valueData = new ArrayList<>((Collection<?>) valueData);
                    } else if (valueData.getClass().isArray()) {
                        // 数组转换为List
                        valueData = Arrays.asList((Object[]) valueData);
                    } else if (valueData instanceof String) {
                        try {
                            Gson gson = GsonBuilder.gsonDefault();
                            // 尝试解析JSON字符串为Map
                            valueData = gson.fromJson(valueData.toString(), List.class);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("List conversion failed - invalid JSON string: " + valueData, e);
                        }
                    } else {
                        throw new IllegalArgumentException("List conversion failed - valueData is not a Collection, Array or JSON String: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == Set.class) {
                    if (valueData instanceof Set) {
                        // 已经是Set类型，无需转换
                        valueData = (Set<?>) valueData;
                    } else if (valueData instanceof Collection) {
                        // 其他Collection类型转换为Set
                        valueData = new HashSet<>((Collection<?>) valueData);
                    } else if (valueData.getClass().isArray()) {
                        // 数组转换为Set
                        valueData = new HashSet<>(Arrays.asList((Object[]) valueData));
                    } else if (valueData instanceof String) {
                        try {
                            Gson gson = GsonBuilder.gsonDefault();
                            // 尝试解析JSON字符串为Map
                            valueData = gson.fromJson(valueData.toString(), Set.class);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Set conversion failed - invalid JSON string: " + valueData, e);
                        }
                    } else {
                        throw new IllegalArgumentException("Set conversion failed - valueData is not a Collection, Array or JSON String: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == LocalDate.class) {
                    if (valueData instanceof String) {
                        try {
                            valueData = LocalDate.parse(valueData.toString());
                        } catch (DateTimeParseException e) {
                            throw new IllegalArgumentException("LocalDate conversion failed - invalid date format: " + valueData, e);
                        }
                    } else if (valueData instanceof java.util.Date) {
                        valueData = ((java.util.Date) valueData).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    } else {
                        throw new IllegalArgumentException("LocalDate conversion failed - valueData is not a String or Date: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else if (clazz == LocalDateTime.class) {
                    if (valueData instanceof String) {
                        try {
                            valueData = LocalDateTime.parse(valueData.toString());
                        } catch (DateTimeParseException e) {
                            throw new IllegalArgumentException("LocalDateTime conversion failed - invalid datetime format: " + valueData, e);
                        }
                    } else if (valueData instanceof java.util.Date) {
                        valueData = ((java.util.Date) valueData).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    } else {
                        throw new IllegalArgumentException("LocalDateTime conversion failed - valueData is not a String or Date: " +
                                (valueData != null ? valueData.getClass().getName() : "null"));
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported target type: " + clazz.getName());
                }
                return (T) valueData;
            }
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
            return Float.valueOf((String) value);
        }
        return Float.valueOf(value.toString());
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
            return Double.valueOf((String) value);
        }
        return Double.valueOf(value.toString());
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
            return Byte.valueOf((String) value);
        }
        return Byte.valueOf(value.toString());
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
            return Integer.valueOf((int) DateUtils.parseToEpochDay(((LocalDate) value)));
        }else if(value instanceof Long){
            return ((Long) value).intValue();
        }else if(value instanceof Double){
            return ((Double) value).intValue();
        }else if(value instanceof Float){
            return ((Float) value).intValue();
        }
        return Double.valueOf(value.toString()).intValue();
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
        return Double.valueOf(value.toString()).longValue();
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
                return DateUtils.parseToLocalDateTime(Long.valueOf((String) value));
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
                return DateUtils.parseToLocalDate(Long.valueOf((String) value));
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
                return new Date(Long.valueOf((String) value));
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
                item = (T) Integer.valueOf(data.toString());
            }else if(clazz == Long.class){
                item = (T) Long.valueOf(data.toString());
            }else if(clazz == Float.class){
                item = (T) Float.valueOf(data.toString());
            }else if(clazz == Double.class){
                item = (T) Double.valueOf(data.toString());
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
                item = (T) Integer.valueOf(data.toString());
            }else if(clazz == Long.class){
                item = (T) Long.valueOf(data.toString());
            }else if(clazz == Float.class){
                item = (T) Float.valueOf(data.toString());
            }else if(clazz == Double.class){
                item = (T) Double.valueOf(data.toString());
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

    public static void main(String[] args) {
        Map map = new HashMap(){{
                put("dateTime", String.valueOf(DateUtils.parseToTimestamp(LocalDateTime.now())));
                put("dict", new HashMap() {{
                    put("list", new ArrayList() {{
                        add("1");
                        add("2");
                    }});
                    put("array", new Map[]{new HashMap() {{
                        put("a", "1");
                        put("b", "2");
                    }}, new HashMap() {{
                        put("a", "3");
                        put("b", "4");
                    }} });
                }});
            }};
        LocalDateTime dateTime = MapUtils.getLocalDateTime(map, "dateTime");
        MapUtils.checkValueType(map, "dateTime", LocalDateTime.class);
        Integer a =  MapUtils.getValueByKeyPath(map, "dict.list[1]", -1, Integer.class);
        Integer b =  MapUtils.getValueByKeyPath(map, "dict.array[1].a", -1, Integer.class);
        System.out.println();
    }
}
