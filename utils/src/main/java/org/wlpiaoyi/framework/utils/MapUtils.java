package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Map 取值器，提供从 Map 中安全获取各种类型值的方法，支持：
 * <ul>
 *   <li>基本类型及包装类（Boolean, Byte, Short, Integer, Long, Float, Double, String）</li>
 *   <li>日期时间类型（Date, LocalDate, LocalDateTime）</li>
 *   <li>集合类型（Map, List, Set, 数组）</li>
 *   <li>通过点号路径（如 "user.address.city"）和数组索引（如 "users[0].name"）深度取值</li>
 *   <li>类型自动转换（如字符串转数字、时间戳转日期等）</li>
 * </ul>
 *
 * @author wlpiaoyi
 */
public class MapUtils {
//
//    public static void main(String[] args) {
//        Map map = new HashMap(){{
//            put("dateTime", String.valueOf(DateUtils.parseTimestamp(LocalDateTime.now())));
//            put("dict", new HashMap() {{
//                put("liststr1", "1,2,3, 4");
//                put("liststr2", "[\"abc\",\"2adfasdf\"]");
//                put("liststr3", "\"123456\",\"2022-12-14 12:12:00\",\"17393838\"");
//                put("list", new ArrayList() {{
//                    add("1");
//                    add("2");
//                }});
//                put("array", new Map[]{
//                    new HashMap() {{
//                        put("a", "1");
//                        put("b", "2");
//                    }},
//                    new HashMap() {{
//                        put("a", "3");
//                        put("b", "4");
//                    }}
//                });
//            }});
//        }};
//        LocalDateTime dateTime = MapUtils.getLocalDateTime(map, "dateTime");
//        MapUtils.checkValueType(map, "dateTime", LocalDateTime.class);
//        Integer a =  MapUtils.getValueByKeyPath(map, "dict.list[0]", null, Integer.class);
//        Integer b =  MapUtils.getValueByKeyPath(map, "dict.array[1].a", null, Integer.class);
//        var liststr1 = MapUtils.getValueByKeyPath(map, "dict.liststr1",null, Integer[].class);
//        var liststr2 = MapUtils.getValueByKeyPath(map, "dict.liststr2",null, String[].class);
//        var liststr3 = MapUtils.getValueByKeyPath(map, "dict.liststr3",null, Date[].class);
//        System.out.println();
//    }

    /**
     * 检查 Map 中指定键对应的值是否为指定的类型。
     *
     * @param map       Map 对象，允许为 null 或空
     * @param key       键
     * @param valueType 期望的类型
     * @param <T>       类型参数
     * @return 如果值存在且是 valueType 类型（或其子类型），返回 true；否则返回 false
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
     * 根据键路径从 Map 中获取指定类型的值。
     * <p>
     * 键路径支持点号分隔（如 "user.address.city"）以及数组/集合索引（如 "users[0].name"）。
     * 如果路径中途遇到 null 或类型不匹配，将返回 defaultValue。
     * </p>
     *
     * @param map         Map 对象
     * @param keyPath     键路径，例如 "order.items[1].price"
     * @param defaultValue 当值为 null 或转换失败时的默认值
     * @param clazz       期望返回的类型
     * @param <T>         返回值类型
     * @return 转换后的值，若无法获取或转换则返回 defaultValue
     * @throws IllegalArgumentException 如果路径中的中间节点不是 Map 类型，或者索引访问的对象不是集合/数组
     * @throws NullPointerException     如果路径中的中间键对应的值为 null
     * @throws IndexOutOfBoundsException 如果索引越界
     */
    public static <T> T getValueByKeyPath(Map map, String keyPath, T defaultValue, Class<T> clazz){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        if(!keyPath.contains(".")){
            return MapUtils.get(map, keyPath, defaultValue);
        }
        String[] keys = keyPath.split("\\.");
        Object valueData = map;
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
            if(index >= 0){
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
            return parseKeyPathValue(valueData, clazz, defaultValue);
        }
        return defaultValue;
    }

    // 私有辅助方法，根据目标类型解析值
    private static <T> T parseKeyPathValue(Object valueData, Class<T> clazz, T defaultValue) {
        if (clazz == Boolean.class) {
            return (T) ValueParse.toBoolean(valueData, (Boolean) defaultValue);
        } else if (clazz == Byte.class) {
            return (T) ValueParse.toByte(valueData, (Byte) defaultValue);
        } else if (clazz == Short.class) {
            return (T) ValueParse.toShort(valueData, (Short) defaultValue);
        } else if (clazz == Integer.class) {
            return (T) ValueParse.toInteger(valueData, (Integer) defaultValue);
        } else if (clazz == Long.class) {
            return (T) ValueParse.toLong(valueData, (Long) defaultValue);
        } else if (clazz == Float.class) {
            return (T) ValueParse.toFloat(valueData, (Float) defaultValue);
        } else if (clazz == Double.class) {
            return (T) ValueParse.toDouble(valueData, (Double) defaultValue);
        } else if (clazz == String.class) {
            return (T) ValueParse.toString(valueData, (String) defaultValue);
        } else if (clazz == Map.class) {
            return (T) ValueParse.toMap(valueData, (Map<?, ?>) defaultValue);
        } else if (clazz == List.class) {
            return (T) ValueParse.toList(valueData, (List<?>) defaultValue);
        } else if (clazz == Set.class) {
            return (T) ValueParse.toSet(valueData, (Set<?>) defaultValue);
        } else if (clazz == Date.class) {
            return (T) ValueParse.toDate(valueData, (Date) defaultValue);
        } else if (clazz == LocalDate.class) {
            return (T) ValueParse.toLocalDate(valueData, (LocalDate) defaultValue);
        } else if (clazz == LocalDateTime.class) {
            return (T) ValueParse.toLocalDateTime(valueData, (LocalDateTime) defaultValue);
        } else if (clazz.isArray()) {
            var res = ValueParse.toArray(valueData);
            if (res == null) return defaultValue;
            Class<?> componentType = clazz.getComponentType();
            res = ValueParse.toArrayGeneric(res, componentType, null);
            if (res == null) return defaultValue;
            return (T) res;
        } else if (clazz.isAssignableFrom(valueData.getClass())){
            return (T) valueData;
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
    }

    /**
     * 从 Map 中获取指定键的值，不进行类型转换，直接返回 Object 类型。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值（当值为 null 时返回）
     * @param <T>         返回值类型（通常为 Object 子类）
     * @return 键对应的值，若为 null 则返回 defaultValue
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

    /**
     * 从 Map 中获取指定键的值，不进行类型转换，返回 Object 类型（可能为 null）。
     *
     * @param map Map 对象
     * @param key 键
     * @param <T> 返回值类型
     * @return 键对应的值，可能为 null
     */
    public static <T> T get(Map map, Object key) {
        return get(map, key,null);
    }

    /**
     * 从 Map 中获取指定键的值，并尝试转换为指定类型。
     *
     * @param map         Map 对象
     * @param key         键
     * @param tClass      目标类型
     * @param defaultValue 转换失败或值为 null 时的默认值
     * @param <T>         目标类型
     * @return 转换后的值，若无法转换则返回 defaultValue
     * @throws IllegalArgumentException 如果目标类型不受支持
     */
    public static <T> T get(Map map, Object key, Class<T> tClass, T defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null){
            return defaultValue;
        }
        return parseKeyPathValue(value, tClass, defaultValue);
    }

    /**
     * 从 Map 中获取指定键的值，并尝试转换为指定类型，无默认值（可能返回 null）。
     *
     * @param map    Map 对象
     * @param key    键
     * @param tClass 目标类型
     * @param <T>    目标类型
     * @return 转换后的值，若值为 null 或转换失败则返回 null
     */
    public static <T> T get(Map map, Object key, Class<T> tClass){
        return get(map, key, tClass, null);
    }

    /**
     * 从 Map 中获取 String 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 String，若值为 null 则返回 null
     */
    public static String getString(Map map, Object key){
        return MapUtils.getString(map, key, null);
    }

    /**
     * 从 Map 中获取 String 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 String，若值为 null 则返回 defaultValue
     */
    public static String getString(Map map, Object key, String defaultValue){
        if(map == null || map.isEmpty()) {
            return defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toString(value, defaultValue);
    }

    /**
     * 从 Map 中获取 Float 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 Float，若值为 null 则返回 null
     */
    public static Float getFloat(Map map, Object key){
        return MapUtils.getFloat(map, key, null);
    }

    /**
     * 从 Map 中获取 Float 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 Float，若值为 null 则返回 defaultValue
     */
    public static Float getFloat(Map map, Object key, Float defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toFloat(value, defaultValue);
    }

    /**
     * 从 Map 中获取 Double 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 Double，若值为 null 则返回 null
     */
    public static Double getDouble(Map map, Object key){
        return MapUtils.getDouble(map, key, null);
    }

    /**
     * 从 Map 中获取 Double 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 Double，若值为 null 则返回 defaultValue
     */
    public static Double getDouble(Map map, Object key, Double defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toDouble(value, defaultValue);
    }

    /**
     * 从 Map 中获取 Boolean 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 Boolean，若值为 null 则返回 null
     */
    public static Boolean getBoolean(Map map, Object key){
        return MapUtils.getBoolean(map, key, null);
    }

    /**
     * 从 Map 中获取 Boolean 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 Boolean，若值为 null 则返回 defaultValue
     */
    public static Boolean getBoolean(Map map, Object key, Boolean defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toBoolean(value, defaultValue);
    }

    /**
     * 从 Map 中获取 Byte 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 Byte，若值为 null 则返回 null
     */
    public static Byte getByte(Map map, Object key){
        return MapUtils.getByte(map, key, null);
    }

    /**
     * 从 Map 中获取 Byte 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 Byte，若值为 null 则返回 defaultValue
     */
    public static Byte getByte(Map map, Object key, Byte defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toByte(value, defaultValue);
    }

    /**
     * 从 Map 中获取 Short 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 Short，若值为 null 则返回 null
     */
    public static Short getShort(Map map, Object key){
        return MapUtils.getShort(map, key, null);
    }

    /**
     * 从 Map 中获取 Short 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 Short，若值为 null 则返回 defaultValue
     */
    public static Short getShort(Map map, Object key, Short defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toShort(value, defaultValue);
    }

    /**
     * 从 Map 中获取 Integer 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 Integer，若值为 null 则返回 null
     */
    public static Integer getInteger(Map map, Object key){
        return MapUtils.getInteger(map, key, null);
    }

    /**
     * 从 Map 中获取 Integer 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 Integer，若值为 null 则返回 defaultValue
     */
    public static Integer getInteger(Map map, Object key, Integer defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toInteger(value, defaultValue);
    }

    /**
     * 从 Map 中获取 Long 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 Long，若值为 null 则返回 null
     */
    public static Long getLong(Map map, Object key){
        return MapUtils.getLong(map, key, null);
    }

    /**
     * 从 Map 中获取 Long 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 Long，若值为 null 则返回 defaultValue
     */
    public static Long getLong(Map map, Object key, Long defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toLong(value, defaultValue);
    }

    /**
     * 从 Map 中获取 LocalDateTime 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 LocalDateTime，若值为 null 则返回 null
     */
    public static LocalDateTime getLocalDateTime(Map map, Object key){
        return MapUtils.getLocalDateTime(map, key, null);
    }

    /**
     * 从 Map 中获取 LocalDateTime 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 LocalDateTime，若值为 null 则返回 defaultValue
     */
    public static LocalDateTime getLocalDateTime(Map map, Object key, LocalDateTime defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toLocalDateTime(value, defaultValue);
    }

    /**
     * 从 Map 中获取 LocalDate 类型的值。
     *
     * @param map Map 对象
     * @param key 键
     * @return 转换后的 LocalDate，若值为 null 则返回 null
     */
    public static LocalDate getLocalDate(Map map, Object key){
        return MapUtils.getLocalDate(map, key, null);
    }

    /**
     * 从 Map 中获取 LocalDate 类型的值，支持默认值。
     *
     * @param map         Map 对象
     * @param key         键
     * @param defaultValue 默认值
     * @return 转换后的 LocalDate，若值为 null 则返回 defaultValue
     */
    public static LocalDate getLocalDate(Map map, Object key, LocalDate defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toLocalDate(value, defaultValue);
    }

    public static Date getDate(Map map, Object key){
        return MapUtils.getDate(map, key, null);
    }

    public static Date getDate(Map map, Object key, Date defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        return ValueParse.toDate(value, defaultValue);
    }

    public static Map getMap(Map map, Object key){
        return MapUtils.getMap(map, key, null);
    }

    public static Map getMap(Map map, Object key, Map defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        Object value = map.get(key);
        if(value == null){
            return defaultValue;
        }
        return ValueParse.toMap(value, defaultValue);
    }

    public static Object[] getArray(Map map, Object key){
        return MapUtils.getArray(map, key, null);
    }

    public static Object[] getArray(Map map, Object key, Object[] defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        return ValueParse.toArray(map.get(key));
    }

    public static <T> T[] getArrayGeneric(Map map, Object key, Class<T> clazz, T[] defaultValue){
        var res = MapUtils.getArray(map, key, defaultValue);
        if(res == defaultValue) return defaultValue;
        return ValueParse.toArrayGeneric(res, clazz, defaultValue);
    }

    public static List getList(Map map, Object key){
        return getList(map, key, null);
    }

    public static List getList(Map map, Object key, List defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        return ValueParse.toList(map.get(key), defaultValue);
    }

    public static <T> List<T> getListGeneric(Map map, Object key, Class<T> clazz){
        return getListGeneric(map, key, clazz, null);
    }

    public static <T> List<T> getListGeneric(Map map, Object key, Class<T> clazz, List<T> defaultValue){
        var res = MapUtils.getList(map, key, defaultValue);
        if(res == defaultValue) return defaultValue;
        return ValueParse.toListGeneric(res, clazz, defaultValue);
    }

    public static Set getSet(Map map, Object key){
        return getSet(map, key, null);
    }

    public static Set getSet(Map map, Object key, Set defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        return ValueParse.toSet(map.get(key), defaultValue);
    }

    public static <T> Set<T> getSetGeneric(Map map, Object key, Class<T> clazz){
        return getSetGeneric(map, key, clazz, null);
    }

    public static <T> Set<T> getSetGeneric(Map map, Object key, Class<T> clazz, Set<T> defaultValue){
        if(map == null || map.isEmpty()) {
            return  defaultValue;
        }
        var res = MapUtils.getSet(map, key, defaultValue);
        if (res == defaultValue) return defaultValue;
        return ValueParse.toSetGeneric(res, clazz, defaultValue);
    }

    private static final Gson GSON = GsonBuilder.gsonDefault();

    static class ValueParse {
        static Boolean toBoolean(Object value, Boolean defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Boolean) {
                return (Boolean) value;
            }else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }else if (value instanceof Number) {
                return ((Number) value).intValue() == 1;
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static Byte toByte(Object value, byte defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Byte) {
                return (Byte) value;
            }else if (value instanceof String) {
                return Byte.parseByte((String) value);
            }else if (value instanceof Boolean) {
                return (byte) ((Boolean) value ? 1 : 0);
            }else if (value instanceof Number) {
                return ((Number) value).byteValue();
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static Short toShort(Object value, short defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Short) {
                return (Short) value;
            }else if (value instanceof String) {
                return Short.parseShort((String) value);
            }else if (value instanceof Boolean) {
                return (short) ((Boolean) value ? 1 : 0);
            }else if (value instanceof Number) {
                return ((Number) value).shortValue();
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static Integer toInteger(Object value, Integer defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Integer) {
                return (Integer) value;
            }else if (value instanceof String) {
                return Integer.parseInt((String) value);
            }else if (value instanceof Boolean) {
                return ((Boolean) value ? 1 : 0);
            }else if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static Long toLong(Object value, Long defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Long) {
                return (Long) value;
            }else if (value instanceof String) {
                return Long.parseLong((String) value);
            }else if (value instanceof Boolean) {
                return (long) ((Boolean) value ? 1 : 0);
            }else if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static Float toFloat(Object value, Float defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Float) {
                return (Float) value;
            }else if (value instanceof String) {
                return Float.parseFloat((String) value);
            }else if (value instanceof Boolean) {
                return (float) ((Boolean) value ? 1 : 0);
            }else if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static Double toDouble(Object value, Double defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Double) {
                return (Double) value;
            }else if (value instanceof String) {
                return Double.parseDouble((String) value);
            }else if (value instanceof Boolean) {
                return (double) ((Boolean) value ? 1 : 0);
            }else if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static String toString(Object value, String defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof String) {
                return (String) value;
            }else return value.toString();
        }


        static Date toDate(Object value, Date defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Date) {
                return (Date) value;
            }else if (value instanceof LocalDateTime) {
                return DateUtils.parseDate((LocalDateTime) value);
            }else if (value instanceof LocalDate) {
                return DateUtils.parseDate((LocalDate) value);
            }else if (value instanceof String) {
                return DateUtils.parseDate((String) value);
            }else if (value instanceof Number) {
                return new Date(((Number) value).longValue());
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static LocalDateTime toLocalDateTime(Object value, LocalDateTime defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Date) {
                return DateUtils.parseLocalDateTime((Date) value);
            }else if (value instanceof LocalDateTime) {
                return (LocalDateTime) value;
            }else if (value instanceof LocalDate) {
                return ((LocalDate) value).atStartOfDay();
            }else if (value instanceof String) {
                return DateUtils.parseLocalDateTime((String) value);
            }else if (value instanceof Number) {
                return DateUtils.parseLocalDateTime(((Number) value).longValue());
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static LocalDate toLocalDate(Object value, LocalDate defaultValue) {
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Date) {
                return DateUtils.parseLocalDate((Date) value);
            }else if (value instanceof LocalDateTime) {
                return ((LocalDateTime) value).toLocalDate();
            }else if (value instanceof LocalDate) {
                return (LocalDate) value;
            }else if (value instanceof String) {
                return DateUtils.parseLocalDate((String) value);
            }else if (value instanceof Number) {
                return DateUtils.parseLocalDate(((Number) value).longValue());
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static Map toMap(Object value, Map defaultValue){
            if (value == null) {
                return defaultValue;
            }else if (value instanceof Map<?, ?> map) {
                return (Map) map;
            }else if (value instanceof Object[] objects) {
                if (objects.length == 0) {
                    return defaultValue;
                }
                Map map = new HashMap();
                for (Object object : objects) {
                    if (object instanceof Map.Entry<?, ?> entry) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
                return map;
            }else if (value instanceof String str){
                if (ValueUtils.isBlank(str)) {  // Java 11+
                    return defaultValue;
                }
                // 可进一步检查是否以 '{' 开头（快速过滤非 JSON）
                if (str.trim().charAt(0) != '{') {
                    throw new IllegalArgumentException("Invalid JSON string for Map: " + str);
                }
                return GSON.fromJson(str, Map.class);
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        static Object[] toArray(Object value){
            if (value == null) {
                return null;
            }else if (value instanceof Object[] objects) {
                return objects;
            }else if (value instanceof Collection<?> collection){
                return collection.toArray();
            }else if (value instanceof String string){
                if (!string.contains(",")) {
                   return new Object[]{string};
                }
                if (string.startsWith("[")) {
                    string = string.substring(1, string.length() - 1);
                }
                if (string.endsWith("[")){
                    string = string.substring(0, string.length() - 1);
                }
                // 统计逗号数量，用于预分配列表容量
                int commaCount = 0;
                for (int i = 0; i < string.length(); i++) {
                    if (string.charAt(i) == ',') {
                        commaCount++;
                    }
                }
                List<String> tokens = new ArrayList<>(commaCount + 1);

                int start = 0, end;
                while ((end = string.indexOf(',', start)) >= 0) {
                    tokens.add(extractToken(string, start, end));
                    start = end + 1;
                }
                // 处理最后一个 token
                tokens.add(extractToken(string, start, string.length()));

                return tokens.toArray(new String[0]); // 或 new Object[0] 均可
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }


        /**
         * 从字符串 s 的 [start, end) 区间提取 token，去除前后空白和首尾引号。
         */
        private static String extractToken(String s, int start, int end) {
            // 跳过前导空白
            int i = start;
            while (i < end && s.charAt(i) == ' ') {
                i++;
            }
            // 跳过尾部空白
            int j = end - 1;
            while (j >= i && s.charAt(j) == ' ') {
                j--;
            }

            // 完全空白的情况
            if (i > j) {
                return "";
            }

            // 去除首尾引号（如果存在）
            if (s.charAt(i) == '"') {
                i++;
            }
            if (j >= i && s.charAt(j) == '"') {
                j--;
            }

            // 注意：去除引号后不需要再次跳过空白，因为内部空白应保留
            return s.substring(i, j + 1);
        }

        @SuppressWarnings({"rawtypes"})
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
            }else if (value instanceof String string){
                Object[] objects = ValueParse.toArray(string);
                if(objects == null || objects.length == 0)
                    return defaultValue;
                return new HashSet(Arrays.asList(objects));
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        @SuppressWarnings({"rawtypes"})
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
            Set<T> items = new HashSet<>();
            for (Object data : value){
                T item = toGeneric(data, clazz);
                items.add(item);
            }
            return items;
        }

        @SuppressWarnings({"rawtypes"})
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
            }else if (value instanceof String string){
                Object[] objects = ValueParse.toArray(string);
                if(objects == null || objects.length == 0)
                    return defaultValue;
                return Arrays.asList(objects);
            }
            throw new IllegalArgumentException("Unsupported target type: " + value.getClass().getName());
        }

        @SuppressWarnings({"rawtypes"})
        static <T> List<T> toListGeneric(List value, Class<T> clazz, List<T> defaultValue){
            if (ValueUtils.isBlank(value)) {
                return defaultValue;
            }
            int length = value.size();

            // 检查是否所有元素都是 T 类型（或子类型）
            boolean allMatch = true;
            for (Object element : value) {
                if (element == null) {
                    continue;
                }
                if (!clazz.isAssignableFrom(element.getClass())) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                return (List<T>) value;
            }
            List<T> items = new ArrayList<>();
            for (Object data : value){
                T item = toGeneric(data, clazz);
                items.add(item);
            }
            return items;
        }

        static <T> T[] toArrayGeneric(Object value, Class<T> clazz, T[] defaultValue){
            if (ValueUtils.isBlank(value) || !value.getClass().isArray()) {
                return defaultValue;
            }
            Object[] array = (Object[]) value;
            int length = array.length;

            // 检查是否所有元素都是 T 类型（或子类型）
            boolean allMatch = true;
            for (Object element : array) {
                if (element == null) {
                    continue;
                }
                if (!clazz.isAssignableFrom(element.getClass())) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                return (T[]) array;
            }
            T[] items = (T[]) Array.newInstance(clazz, length);
            for (int i = 0; i < length; i++) {
                items[i] = toGeneric(array[i], clazz);
            }
            return items;
        }

        private static <T> T toGeneric(Object value, Class<T> clazz){
            if (ValueUtils.isBlank(value)) {
                return null;
            }

            T item;
            if(clazz.isAssignableFrom(value.getClass())){
                item = (T) value;
            }else if (clazz == String.class) {
                item = (T) ValueParse.toString(value, null);
            }else if (clazz == Integer.class) {
                item = (T) ValueParse.toInteger(value, null);
            }else if (clazz == Long.class) {
                item = (T) ValueParse.toLong(value, null);
            }else if (clazz == Float.class) {
                item = (T) ValueParse.toFloat(value, null);
            }else if (clazz == Double.class) {
                item = (T) ValueParse.toDouble(value, null);
            }else if (clazz == Character[].class) {
                char[] chars = value.toString().toCharArray();
                Character[] charArray = new Character[chars.length];
                for (int i = 0; i < chars.length; i++) {
                    charArray[i] = chars[i];
                }
                item = (T) charArray;
            }else if (clazz == BigInteger.class) {
                if (value instanceof Number) {
                    item = (T) BigInteger.valueOf(((Number) value).longValue());
                } else {
                    item = (T) new BigInteger(value.toString());
                }
            }else if (clazz == Date.class){
                item = (T) ValueParse.toDate(value, null);
            }else if (clazz == LocalDateTime.class){
                item = (T) ValueParse.toLocalDateTime(value, null);
            }else if (clazz == LocalDate.class){
                item = (T) ValueParse.toLocalDate(value, null);
            }else if (clazz == Map.class){
                item = (T) ValueParse.toMap(value, null);
            }else if (value instanceof Serializable){
                item = GSON.fromJson(GSON.toJson(value), clazz);
            }else item = null;
            return item;
        }
    }

}
