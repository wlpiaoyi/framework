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
     * <p><b>{@code @description:}</b>
     * 检查对象类型
     * </p>
     *
     * <p><b>{@code @param}</b> <b>map</b>
     * {@link Map}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>key</b>
     * {@link Object}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>valueType</b>
     * {@link Class<T>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/6 9:52</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    @SuppressWarnings("rawtypes")
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
    @SuppressWarnings({"rawtypes", "StatementWithEmptyBody", "unchecked"})
    public static <T> T getValueByKeyPath(Map map, String keyPath, T defaultValue, Class<T> clazz){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        if(!keyPath.contains(".")){
            return MapUtils.get(map, keyPath, defaultValue);
        }
        String[] keys = keyPath.split("\\.");
        Object valueData = map;
//        int ki = 0;
        int ksl = keys.length;
        for (String key : keys){
            ksl --;
            if(key.contains("[%1%]")){
                key = key.replaceAll("\\[%1%]", ".");
            }
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
                Object value = ((Map<?, ?>) valueData).get(key);
                if (value == null) {
                    throw new NullPointerException("Value for key '" + key + "' is null");
                }
                if (value instanceof List<?> list) {
                    if (index >= list.size()) {
                        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list size " + list.size());
                    }
                    valueData = list.get(index);
                } else if (value instanceof Collection<?> collection) {
                    if (index >= collection.size()) {
                        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for collection size " + collection.size());
                    }
                    // 转换为List以便通过索引访问
                    valueData = new ArrayList<>(collection).get(index);
                } else if (value.getClass().isArray()) {
                    int length = Array.getLength(value);
                    if (index >= length) {
                        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for array length " + length);
                    }
                    valueData = Array.get(value, index);
                } else if (value instanceof Iterable) {
                    // 对于其他Iterable类型，转换为List
                    List<Object> list = new ArrayList<>();
                    for (Object item : (Iterable<?>) value) {
                        list.add(item);
                    }
                    if (index >= list.size()) {
                        throw new IndexOutOfBoundsException("Index " + index + " out of bounds for iterable size " + list.size());
                    }
                    valueData = list.get(index);
                } else {
                    throw new IllegalArgumentException("Value is not a collection or array type: " + value.getClass().getName());
                }
            }else{
                valueData = ((Map<?, ?>) valueData).get(key);
            }
            if(valueData == null){
                return defaultValue;
            }
            if(ksl > 0) continue;
            return parseValue(valueData, clazz);
        }
        return defaultValue;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取指定类型的对象
     * </p>
     *
     * <p><b>{@code @param}</b> <b>map</b>
     * {@link Map}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>key</b>
     * {@link Object}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>defaultValue</b>
     * {@link T}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/6 9:32</p>
     * <p><b>{@code @return:}</b>{@link T}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
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

    @SuppressWarnings("rawtypes")
    public static <T> T get(Map map, Object key) {
        return get(map, key,null);
    }

    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings("rawtypes")
    public static Object getObject(Map map, Object key){
        return getObject(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static String getString(Map map, Object key){
        return MapUtils.getString(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static String getString(Map map, Object key, String defaultValue){
        if(map == null || map.isEmpty()) {
            return defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }else if(value instanceof String){
            return (String) value;
        }else if(value instanceof LocalDateTime){
            return DateUtils.formatLocalDateTime((LocalDateTime) value);
        }else if(value instanceof LocalDate){
            return DateUtils.formatLocalDate((LocalDate) value);
        }else if(value instanceof Date){
            return DateUtils.parseDate((Date) value);
        }else{
            return value.toString();
        }
    }

    @SuppressWarnings("rawtypes")
    public static Float getFloat(Map map, Object key){
        return MapUtils.getFloat(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static Float getFloat(Map map, Object key, Float defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }else if(value instanceof Float){
            return (Float) value;
        }else if(value instanceof String){
            return Float.valueOf((String) value);
        }else if(value instanceof Number){
            return ((Number) value).floatValue();
        }else if(value instanceof Boolean){
            return ((Boolean) value) ? 1.0f : 0.0f;
        }else throw new IllegalArgumentException("Float conversion failed - valueData is not a Float, String or Number: " +
                value.getClass().getName());
    }

    @SuppressWarnings("rawtypes")
    public static Double getDouble(Map map, Object key){
        return MapUtils.getDouble(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static Double getDouble(Map map, Object key, Double defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }else if(value instanceof Double){
            return (Double) value;
        }else if(value instanceof String){
            return Double.valueOf((String) value);
        }else if(value instanceof Number){
            return ((Number) value).doubleValue();
        }else if(value instanceof Boolean){
            return ((Boolean) value) ? 1.0d : 0.0d;
        }else throw new IllegalArgumentException("Double conversion failed - valueData is not a Double, String or Number: " +
                value.getClass().getName());
    }

    @SuppressWarnings("rawtypes")
    public static Boolean getBoolean(Map map, Object key){
        return MapUtils.getBoolean(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static Boolean getBoolean(Map map, Object key, Boolean defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }else if(value instanceof Boolean){
            return (Boolean) value;
        }else if(value instanceof String){
            return Boolean.valueOf((String) value);
        }else if(value instanceof Number){
            return ((Number) value).intValue() == 1;
        }else throw new IllegalArgumentException("Boolean conversion failed - valueData is not a Boolean, String or Number: " +
                value.getClass().getName());
    }

    @SuppressWarnings("rawtypes")
    public static Byte getByte(Map map, Object key){
        return MapUtils.getByte(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static Byte getByte(Map map, Object key, Byte defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }else if(value instanceof Byte){
            return (Byte) value;
        }else if(value instanceof String){
            return Byte.valueOf((String) value);
        }else if(value instanceof Number){
            return ((Number) value).byteValue();
        }else throw new IllegalArgumentException("Byte conversion failed - valueData is not a Byte, String or Number: " +
                value.getClass().getName());
    }

    @SuppressWarnings("rawtypes")
    public static Integer getInteger(Map map, Object key){
        return MapUtils.getInteger(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static Integer getInteger(Map map, Object key, Integer defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }else if(value instanceof Integer){
            return (Integer) value;
        }else if(value instanceof String){
            return Integer.valueOf((String) value);
        }else if(value instanceof Number){
            return ((Number) value).intValue();
        }else if(value instanceof Boolean){
            return ((Boolean) value) ? 1 : 0;
        }else throw new IllegalArgumentException("Integer conversion failed - valueData is not a Integer, String or Number: " +
                value.getClass().getName());
    }

    @SuppressWarnings("rawtypes")
    public static Long getLong(Map map, Object key){
        return MapUtils.getLong(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static Long getLong(Map map, Object key, Long defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if (value == null){
            return defaultValue;
        }else if (value instanceof Long) {
            return (Long) value;
        }else if (value instanceof String) {
            return Long.valueOf((String) value);
        }else if (value instanceof Number) {
            return ((Number) value).longValue();
        }else if (value instanceof Boolean) {
            return ((Boolean) value) ? 1L : 0L;
        }else throw new IllegalArgumentException("Long conversion failed - valueData is not a Long, String or Number: " +
                value.getClass().getName());
    }

    @SuppressWarnings("rawtypes")
    public static LocalDateTime getLocalDateTime(Map map, Object key){
        return MapUtils.getLocalDateTime(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static LocalDateTime getLocalDateTime(Map map, Object key, LocalDateTime defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }else if(value instanceof LocalDateTime){
            return (LocalDateTime) value;
        }else if(value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (CharSequence) value);
            if (isMatch) {
                return DateUtils.parseLocalDateTime(Long.parseLong((String) value));
            } else {
                return DateUtils.parseLocalDateTime((String) value);
            }
        }else if(value instanceof Number){
            return DateUtils.parseLocalDateTime(((Number) value).longValue());
        }else throw new IllegalArgumentException("LocalDateTime conversion failed - valueData is not a LocalDateTime, String or Number: " +
                value.getClass().getName());
    }

    @SuppressWarnings("rawtypes")
    public static LocalDate getLocalDate(Map map, Object key){
        return MapUtils.getLocalDate(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static LocalDate getLocalDate(Map map, Object key, LocalDate defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }else if(value instanceof LocalDate){
            return (LocalDate) value;
        }else if(value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (CharSequence) value);
            if (isMatch) {
                return DateUtils.parseLocalDate(Long.parseLong((String) value));
            } else {
                return DateUtils.parseLocalDate((String) value);
            }
        }else if(value instanceof Number){
            return DateUtils.parseLocalDate(((Number) value).longValue());
        }else throw new IllegalArgumentException("LocalDate conversion failed - valueData is not a LocalDate, String or Number: " +
                value.getClass().getName());
    }

    @SuppressWarnings("rawtypes")
    public static Date getDate(Map map, Object key){
        return MapUtils.getDate(map, key, null);
    }

    @SuppressWarnings("rawtypes")
    public static Date getDate(Map map, Object key, Date defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null) {
            return defaultValue;
        }else if(value instanceof Date){
            return (Date) value;
        }else if(value instanceof String){
            boolean isMatch = Pattern.matches("^\\d+$", (CharSequence) value);
            if (isMatch) {
                return new Date(Long.parseLong((String) value));
            } else {
                return DateUtils.parseDate((String) value);
            }
        }else if(value instanceof Number){
            return new Date(((Number) value).longValue());
        }else throw new IllegalArgumentException("Date conversion failed - valueData is not a Date, String or Number: " +
                value.getClass().getName());
    }

    @SuppressWarnings("rawtypes")
    public static Map getMap(Map map, Object key){
        return MapUtils.getMap(map, key, null);
    }

    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings("rawtypes")
    public static Object[] getArray(Map map, Object key){
        return MapUtils.getArray(map, key, null);
    }

    @SuppressWarnings("rawtypes")
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

    @SuppressWarnings("rawtypes")
    public static <T> T[] getArrayGeneric(Map map, Object key, Class<T> clazz, T[] defaultValue){
        return ValueParse.toArrayGeneric(map.get(key), clazz, defaultValue);
    }

    @SuppressWarnings("rawtypes")
    public static List getList(Map map, Object key){
        return getList(map, key, null);
    }

    @SuppressWarnings({"rawtypes"})
    public static List getList(Map map, Object key, List defaultValue){
        return ValueParse.toList(map.get(key), defaultValue);
    }

    @SuppressWarnings("rawtypes")
    public static <T> List<T> getListGeneric(Map map, Object key, Class<T> clazz){
        return getListGeneric(map, key, clazz, null);
    }

    @SuppressWarnings({"rawtypes"})
    public static <T> List<T> getListGeneric(Map map, Object key, Class<T> clazz, List<T> defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        return ValueParse.toListGeneric(MapUtils.getList(map, key, defaultValue), clazz, defaultValue);
    }

    @SuppressWarnings("rawtypes")
    public static Set getSet(Map map, Object key){
        return getSet(map, key, null);
    }

    @SuppressWarnings({"rawtypes"})
    public static Set getSet(Map map, Object key, Set defaultValue){
        return ValueParse.toSet(map.get(key), defaultValue);
    }

    @SuppressWarnings("rawtypes")
    public static <T> Set<T> getSetGeneric(Map map, Object key, Class<T> clazz){
        return getSetGeneric(map, key, clazz, null);
    }

    public static <T> Set<T> getSetGeneric(Map map, Object key, Class<T> clazz, Set<T> defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        return ValueParse.toSetGeneric(MapUtils.getSet(map, key, defaultValue), clazz, defaultValue);
    }

    private static <T> T parseValue(Object valueData, Class<T> clazz) {
        if (clazz == Boolean.class) {
            if (valueData instanceof String) {
                valueData = Boolean.valueOf((String) valueData);
            } else if (valueData instanceof Number) {
                valueData = ((Number) valueData).intValue() != 0;
            } else if (valueData instanceof Boolean) {
                // 已经是Boolean类型，无需转换
            } else {
                throw new IllegalArgumentException("Boolean conversion failed - valueData is not a String, Number or Boolean: " +
                        valueData.getClass().getName());
            }
        }else if (clazz == Integer.class) {
            if (valueData instanceof Number) {
                valueData = ((Number) valueData).intValue();
            } else if (valueData instanceof Boolean){
                valueData  = ((Boolean) valueData) ? 1 : 0;
            } else if (valueData instanceof String) {
                try {
                    valueData = Integer.parseInt(valueData.toString().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Integer conversion failed - invalid string format: " + valueData, e);
                }
            } else {
                throw new IllegalArgumentException("Integer conversion failed - valueData is not a Number or String: " +
                        valueData.getClass().getName());
            }
        } else if (clazz == Long.class) {
            if (valueData instanceof Number) {
                valueData = ((Number) valueData).longValue();
            } else if (valueData instanceof Boolean){
                valueData  = ((Boolean) valueData) ? 1L : 0L;
            } else if (valueData instanceof Date) {
                valueData = ((Date) valueData).getTime();
            } else if (valueData instanceof LocalDate) {
                valueData = DateUtils.parseEpochDay((LocalDate) valueData) * 24 * 3600 * 1000;
            } else if (valueData instanceof LocalDateTime) {
                valueData = DateUtils.parseTimestamp((LocalDateTime) valueData);
            } else if (valueData instanceof String) {
                try {
                    valueData = Long.parseLong(valueData.toString().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Long conversion failed - invalid string format: " + valueData, e);
                }
            } else {
                throw new IllegalArgumentException("Long conversion failed - valueData is not a Number or String: " +
                        valueData.getClass().getName());
            }
        } else if (clazz == Float.class) {
            if (valueData instanceof Number) {
                valueData = ((Number) valueData).floatValue();
            } else if (valueData instanceof Boolean){
                valueData  = ((Boolean) valueData) ? 1.0f : 0.0f;
            } else if (valueData instanceof String) {
                try {
                    valueData = Float.parseFloat(valueData.toString().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Float conversion failed - invalid string format: " + valueData, e);
                }
            } else {
                throw new IllegalArgumentException("Float conversion failed - valueData is not a Number or String: " +
                        valueData.getClass().getName());
            }
        } else if (clazz == Double.class) {
            if (valueData instanceof Number) {
                valueData = ((Number) valueData).doubleValue();
            } else if (valueData instanceof Boolean){
                valueData  = ((Boolean) valueData) ? 1.0d : 0.0d;
            } else if (valueData instanceof Date) {
                valueData = (double) ((Date) valueData).getTime();
            } else if (valueData instanceof LocalDate) {
                valueData = (double) (DateUtils.parseEpochDay((LocalDate) valueData) * 24 * 3600 * 1000);
            } else if (valueData instanceof LocalDateTime) {
                valueData = (double) (DateUtils.parseTimestamp((LocalDateTime) valueData));
            } else if (valueData instanceof String) {
                try {
                    valueData = Double.parseDouble(valueData.toString().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Double conversion failed - invalid string format: " + valueData, e);
                }
            } else {
                throw new IllegalArgumentException("Double conversion failed - valueData is not a Number or String: " +
                        valueData.getClass().getName());
            }
        } else if (clazz == String.class) {
            if (valueData instanceof Date) {
                valueData = DateUtils.parseDate((Date) valueData);
            } else if (valueData instanceof LocalDate) {
                valueData = DateUtils.formatLocalDate((LocalDate) valueData);
            } else if (valueData instanceof LocalDateTime) {
                valueData = DateUtils.formatLocalDateTime((LocalDateTime) valueData);
            } else valueData = valueData.toString();
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
                        valueData.getClass().getName());
            }
        } else if (clazz == Map.class) {
            if (valueData instanceof Map) {
                // 已经是Map类型，无需转换
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
                        valueData.getClass().getName());
            }
        } else if (clazz == List.class) {
            if (valueData instanceof List) {
                // 已经是List类型，无需转换
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
                        valueData.getClass().getName());
            }
        } else if (clazz == Set.class) {
            if (valueData instanceof Set) {
                // 已经是Set类型，无需转换
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
                        valueData.getClass().getName());
            }
        } else if (clazz == Date.class) {
            if (valueData instanceof Date) {
                // 已经是Date类型，无需转换
            }else if (valueData instanceof String) {
                valueData = DateUtils.parseDate(valueData.toString());
            } else if (valueData instanceof LocalDate) {
                valueData = DateUtils.parseDate((LocalDate) valueData);
            } else if (valueData instanceof LocalDateTime) {
                valueData = DateUtils.parseDate((LocalDateTime) valueData);
            } else if (valueData instanceof Number){
                valueData = new Date(((Number) valueData).longValue());
            }else throw new IllegalArgumentException("Date conversion failed - valueData is not a String or Date: " +
                    valueData.getClass().getName());
        } else if (clazz == LocalDate.class) {
            if (valueData instanceof LocalDate) {
                // 已经是LocalDate类型，无需转换
            } else if (valueData instanceof Date) {
                valueData = ((Date) valueData).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (valueData instanceof LocalDateTime){
                valueData = ((LocalDateTime) valueData).toLocalDate();
            }else if (valueData instanceof String) {
                try {
                    valueData = LocalDate.parse(valueData.toString());
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("LocalDate conversion failed - invalid date format: " + valueData, e);
                }
            } else if (valueData instanceof Number){
                valueData = DateUtils.parseLocalDate(((Number) valueData).longValue());
            } else {
                throw new IllegalArgumentException("LocalDate conversion failed - valueData is not a String or Date: " +
                        valueData.getClass().getName());
            }
        } else if (clazz == LocalDateTime.class) {
            if (valueData instanceof LocalDateTime) {
                // 已经是LocalDateTime类型，无需转换
            } else if (valueData instanceof Date) {
                valueData = ((Date) valueData).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else if (valueData instanceof LocalDate){
                valueData = ((LocalDate) valueData).atStartOfDay();
            } else if (valueData instanceof Number){
                valueData = DateUtils.parseLocalDateTime(((Number) valueData).longValue());
            } else if (valueData instanceof String) {
                try {
                    valueData = LocalDateTime.parse(valueData.toString());
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("LocalDateTime conversion failed - invalid datetime format: " + valueData, e);
                }
            } else {
                throw new IllegalArgumentException("LocalDateTime conversion failed - valueData is not a String or Date: " +
                        valueData.getClass().getName());
            }
        } else {
            throw new IllegalArgumentException("Unsupported target type: " + clazz.getName());
        }
        return (T) valueData;
    }

    public static void main(String[] args) {
        Map map = new HashMap(){{
                put("dateTime", String.valueOf(DateUtils.parseTimestamp(LocalDateTime.now())));
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

    private static class ValueParse {

        @SuppressWarnings({"unchecked"})
        private static <T> T toGeneric(Object value, Class<T> clazz, Gson[] gsons){
            if (ValueUtils.isBlank(value)) {
                return null;
            }
            Gson gson = null;
            if(gsons.length > 0) gson = gsons[0];

            T item;
            if(clazz.isAssignableFrom(value.getClass())){
                item = (T) value;
            }else if(clazz == String.class){
                item = (T) value.toString();
            }else if(clazz == Integer.class){
                item = (T) Integer.valueOf(value.toString());
            }else if(clazz == Long.class){
                item = (T) Long.valueOf(value.toString());
            }else if(clazz == Float.class){
                item = (T) Float.valueOf(value.toString());
            }else if(clazz == Double.class){
                item = (T) Double.valueOf(value.toString());
            }else if(clazz == Character[].class){
                item = (T) value.toString().toCharArray();
            }else if(clazz == BigDecimal.class){
                item = (T) new BigDecimal(value.toString());
            }else if(clazz == BigInteger.class){
                item = (T) new BigInteger(value.toString());
            }else if(clazz == Map.class){
                if(value instanceof Map) {
                    item = (T) value;
                }else if (value instanceof String){
                    if(gson == null){
                        gson = GsonBuilder.gsonDefault();
                        gsons[0] = gson;
                    }
                    item = (T) gson.fromJson((String) value, Map.class);
                }else throw new IllegalArgumentException("Unsupported target type: " + clazz.getName());
            }else{
                if(gson == null){
                    gson = GsonBuilder.gsonDefault();
                    gsons[0] = gson;
                }
                item = gson.fromJson(gson.toJson(value), clazz);
            }
            return item;
        }


        @SuppressWarnings({"rawtypes", "unchecked"})
        static Set toSet(Object value, Set defaultValue){
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Set<?>) {
                return (Set) value;
            }else if (value instanceof Object[] objects) {
                if (objects.length == 0) {
                    return defaultValue;
                }
                return new HashSet(Arrays.asList(objects));
            }else if (value instanceof Collection<?>) {
                if (((Collection) value).isEmpty()) {
                    return defaultValue;
                }
                return new HashSet((Collection) value);
            }else return defaultValue;
        }
        @SuppressWarnings({"rawtypes", "unchecked"})
        static <T> Set<T> toSetGeneric(Set value, Class<T> clazz, Set<T> defaultValue){
            if (ValueUtils.isBlank(value)) {
                return defaultValue;
            }
            int length = value.size();

            // 检查是否所有元素都是 T 类型（或子类型）
            boolean allMatch = true;
            for (Object element : value) {
                if (element != null && !clazz.isAssignableFrom(element.getClass())) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                return (Set<T>) value;
            }
            Gson[] gsons = new Gson[1];
            Set<T> items = new HashSet<>();
            for (Object data : value){
                T item = toGeneric(data, clazz, gsons);
                items.add(item);
            }
            return items;
        }
        @SuppressWarnings({"rawtypes", "unchecked"})
        public static List toList(Object value, List defaultValue){
            if (value == null) {
                return defaultValue;
            }else if (value instanceof List<?>) {
                return (List) value;
            }else if (value instanceof Object[] objects) {
                if (objects.length == 0) {
                    return defaultValue;
                }
                return new ArrayList(Arrays.asList(objects));
            }else if (value instanceof Collection<?>) {
                if (((Collection) value).isEmpty()) {
                    return defaultValue;
                }
                return new ArrayList((Collection) value);
            }else return defaultValue;
        }
        @SuppressWarnings({"rawtypes", "unchecked"})
        static <T> List<T> toListGeneric(List value, Class<T> clazz, List<T> defaultValue){
            if (ValueUtils.isBlank(value)) {
                return defaultValue;
            }
            int length = value.size();

            // 检查是否所有元素都是 T 类型（或子类型）
            boolean allMatch = true;
            for (Object element : value) {
                if (element != null && !clazz.isAssignableFrom(element.getClass())) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                return (List<T>) value;
            }
            Gson[] gsons = new Gson[1];
            List<T> items = new ArrayList<>();
            for (Object data : value){
                T item = toGeneric(data, clazz, gsons);
                items.add(item);
            }
            return items;
        }


        @SuppressWarnings({"unchecked"})
        static <T> T[] toArrayGeneric(Object value, Class<T> clazz, T[] defaultValue){
            if (ValueUtils.isBlank(value) || !value.getClass().isArray()) {
                return defaultValue;
            }
            Object[] array = (Object[]) value;
            int length = array.length;

            // 检查是否所有元素都是 T 类型（或子类型）
            boolean allMatch = true;
            for (Object element : array) {
                if (element != null && !clazz.isAssignableFrom(element.getClass())) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                return (T[]) array;
            }
            Gson[] gsons = new Gson[1];
            T[] items = (T[]) Array.newInstance(clazz, length);
            for (int i = 0; i < length; i++) {
                items[i] = toGeneric(array[i], clazz, gsons);
            }
            return items;
        }
    }

}
