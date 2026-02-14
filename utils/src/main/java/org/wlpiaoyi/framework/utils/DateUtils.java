package org.wlpiaoyi.framework.utils;


import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Range;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p><b>{@code @description:}</b>  时间工具</p>
 * <p><b>{@code @date:}</b>         2019/10/6 9:51</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class DateUtils {

    /**
     * <p><b>{@code @description:}</b>
     * String format Date with pattern
     * </p>
     *
     * <p><b>@param</b> <b>dateStr</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2023/5/13 9:56</p>
     * <p><b>{@code @return:}</b>{@link Date}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期字符串不能为空");
        }
        dateStr = dateStr.trim();

        // 1. 尝试使用预定义格式解析
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                TemporalAccessor parsed = formatter.parse(dateStr);
                return toDate(parsed);
            } catch (DateTimeParseException ignored) {
                // 继续尝试下一个格式
            }
        }

        // 2. 尝试解析为数字时间戳
        Date date = parseNumeric(dateStr);
        if (date != null) {
            return date;
        }

        throw new IllegalArgumentException("日期字符串格式错误: " + dateStr);
    }

    /**
     * 将 TemporalAccessor 转换为 java.util.Date
     */
    private static Date toDate(TemporalAccessor parsed) {
        if (parsed.isSupported(ChronoField.INSTANT_SECONDS)) {
            // 包含时区信息（如 CST 格式）
            Instant instant = Instant.from(parsed);
            return Date.from(instant);
        } else if (parsed.isSupported(ChronoField.YEAR)) {
            // 本地日期时间，使用系统默认时区
            LocalDateTime ldt;
            if (parsed.isSupported(ChronoField.HOUR_OF_DAY)) {
                ldt = LocalDateTime.from(parsed);
            } else {
                ldt = LocalDate.from(parsed).atStartOfDay();
            }
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            throw new IllegalArgumentException("无法解析的日期格式");
        }
    }

    /**
     * 尝试将字符串解析为数字时间戳（支持整数和浮点数）
     * @return 成功返回 Date，失败返回 null
     */
    private static Date parseNumeric(String dateStr) {
        // 快速判断是否可能为数字（允许负号、小数点）
        boolean isNumeric = true;
        boolean hasDot = false;
        for (int i = 0; i < dateStr.length(); i++) {
            char c = dateStr.charAt(i);
            if (c == '-') {
                if (i != 0) { // 负号只能在首位
                    isNumeric = false;
                    break;
                }
            } else if (c == '.') {
                if (hasDot) { // 多个小数点非法
                    isNumeric = false;
                    break;
                }
                hasDot = true;
            } else if (c < '0' || c > '9') {
                isNumeric = false;
                break;
            }
        }
        if (!isNumeric) {
            return null;
        }

        try {
            if (hasDot) {
                return new Date((long) Double.parseDouble(dateStr));
            } else {
                return new Date(Long.parseLong(dateStr));
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }


    /**
     * <p><b>{@code @description:}</b>
     * String format to LocalDateTime
     * </p>
     *
     * <p><b>@param</b> <b>localDateTime</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/13 9:56</p>
     * <p><b>{@code @return:}</b>{@link LocalDateTime}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */public static LocalDateTime parseLocalDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期字符串不能为空");
        }
        dateStr = dateStr.trim();

        // 1. 尝试使用预定义格式解析
        for (DateTimeFormatter formatter : FORMATTERS) { // 复用之前的 FORMATTERS 列表
            try {
                TemporalAccessor parsed = formatter.parse(dateStr);
                return toLocalDateTime(parsed);
            } catch (DateTimeParseException ignored) {
                // continue
            }
        }

        // 2. 尝试解析为数字时间戳
        LocalDateTime ldt = parseNumericToLocalDateTime(dateStr);
        if (ldt != null) {
            return ldt;
        }

        throw new IllegalArgumentException("日期字符串格式错误: " + dateStr);
    }

    /**
     * 将 TemporalAccessor 转换为 LocalDateTime
     */
    private static LocalDateTime toLocalDateTime(TemporalAccessor parsed) {
        if (parsed.isSupported(ChronoField.NANO_OF_DAY)) {
            // 包含时间部分
            return LocalDateTime.from(parsed);
        } else if (parsed.isSupported(ChronoField.EPOCH_DAY)) {
            // 只有日期部分
            return LocalDate.from(parsed).atStartOfDay();
        } else {
            throw new IllegalArgumentException("无法解析为 LocalDateTime");
        }
    }

    /**
     * 尝试将字符串解析为数字时间戳并转换为 LocalDateTime
     */
    private static LocalDateTime parseNumericToLocalDateTime(String dateStr) {
        // 快速判断是否可能为数字（允许负号、小数点）
        boolean isNumeric = true;
        boolean hasDot = false;
        for (int i = 0; i < dateStr.length(); i++) {
            char c = dateStr.charAt(i);
            if (c == '-') {
                if (i != 0) { // 负号只能在首位
                    isNumeric = false;
                    break;
                }
            } else if (c == '.') {
                if (hasDot) { // 多个小数点非法
                    isNumeric = false;
                    break;
                }
                hasDot = true;
            } else if (c < '0' || c > '9') {
                isNumeric = false;
                break;
            }
        }
        if (!isNumeric) {
            return null;
        }

        try {
            long millis;
            if (hasDot) {
                millis = (long) Double.parseDouble(dateStr);
            } else {
                millis = Long.parseLong(dateStr);
            }
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * String format to LocalDate
     * </p>
     *
     * <p><b>@param</b> <b>localDate</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2019/10/6 9:56</p>
     * <p><b>{@code @return:}</b>{@link LocalDate}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期字符串不能为空");
        }
        dateStr = dateStr.trim();

        // 1. 尝试使用预定义格式解析
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                TemporalAccessor parsed = formatter.parse(dateStr);
                return toLocalDate(parsed);
            } catch (DateTimeParseException ignored) {
                // 继续尝试下一个格式
            }
        }

        // 2. 尝试解析为数字时间戳
        LocalDate ld = parseNumericToLocalDate(dateStr);
        if (ld != null) {
            return ld;
        }

        throw new IllegalArgumentException("日期字符串格式错误: " + dateStr);
    }

    /**
     * 将 TemporalAccessor 转换为 LocalDate
     */
    private static LocalDate toLocalDate(TemporalAccessor parsed) {
        if (parsed.isSupported(ChronoField.INSTANT_SECONDS)) {
            // 带时区信息（如 CST 格式），转换为 Instant 再通过系统时区转 LocalDate
            Instant instant = Instant.from(parsed);
            return instant.atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (parsed.isSupported(ChronoField.EPOCH_DAY)) {
            // 包含日期部分（可能是 LocalDate 或 LocalDateTime）
            if (parsed.isSupported(ChronoField.NANO_OF_DAY)) {
                // 是 LocalDateTime，只取日期部分
                return LocalDateTime.from(parsed).toLocalDate();
            } else {
                return LocalDate.from(parsed);
            }
        } else {
            throw new IllegalArgumentException("无法解析为 LocalDate");
        }
    }

    /**
     * 尝试将字符串解析为数字时间戳并转换为 LocalDate
     */
    private static LocalDate parseNumericToLocalDate(String dateStr) {
        // 快速判断是否可能为数字（允许负号、小数点）
        boolean isNumeric = true;
        boolean hasDot = false;
        for (int i = 0; i < dateStr.length(); i++) {
            char c = dateStr.charAt(i);
            if (c == '-') {
                if (i != 0) { // 负号只能在首位
                    isNumeric = false;
                    break;
                }
            } else if (c == '.') {
                if (hasDot) { // 多个小数点非法
                    isNumeric = false;
                    break;
                }
                hasDot = true;
            } else if (c < '0' || c > '9') {
                isNumeric = false;
                break;
            }
        }
        if (!isNumeric) {
            return null;
        }

        try {
            long millis;
            if (hasDot) {
                millis = (long) Double.parseDouble(dateStr);
            } else {
                millis = Long.parseLong(dateStr);
            }
            return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * String format to LocalDateTime with pattern
     * </p>
     *
     * <p><b>@param</b> <b>localDateTime</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>pattern</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2019/10/6 9:56</p>
     * <p><b>{@code @return:}</b>{@link LocalDateTime}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String pattern) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期字符串不能为空");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateStr.trim(), dtf);
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalTime to NanoOfDay
     * </p>
     *
     * <p><b>@param</b> <b>localTime</b>
     * {@link LocalTime}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/3/28 9:52</p>
     * <p><b>{@code @return:}</b>{@link long}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static long parseNanoOfDay(@NonNull LocalTime localTime) {
        return localTime.toNanoOfDay();
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDate to epochDay
     * </p>
     *
     * <p><b>@param</b> <b>localDate</b>
     * {@link LocalDate}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/28 9:52</p>
     * <p><b>{@code @return:}</b>{@link long}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static long parseEpochDay(@NonNull LocalDate localDate) {
        return localDate.toEpochDay();
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDateTime parse to Timestamp
     * </p>
     *
     * <p><b>@param</b> <b>localDateTime</b>
     * {@link LocalDateTime}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/28 9:53</p>
     * <p><b>{@code @return:}</b>{@link long}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static long parseTimestamp(@NonNull LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseTimestamp(localDateTime, zoneId);
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDateTime parse to Timestamp
     * </p>
     *
     * <p><b>@param</b> <b>localDateTime</b>
     * {@link LocalDateTime}
     * </p>
     *
     * <p><b>@param</b> <b>zoneId</b>
     * {@link ZoneId}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/28 9:53</p>
     * <p><b>{@code @return:}</b>{@link long}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static long parseTimestamp(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(localDateTime);
        return localDateTime.toInstant(zoneOffset).toEpochMilli();
    }


    /**
     * <p><b>{@code @description:}</b>
     * nanoOfDay to LocalTime
     * </p>
     *
     * <p><b>@param</b> <b>nanoOfDay</b>
     * {@link long}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 9:54</p>
     * <p><b>{@code @return:}</b>{@link LocalTime}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalTime parseLocalTime(long nanoOfDay) {
        return LocalTime.ofNanoOfDay(nanoOfDay);
    }

    /**
     * <p><b>{@code @description:}</b>
     * timestamp to LocalDate
     * </p>
     *
     * <p><b>@param</b> <b>timestamp</b>
     * {@link long}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 10:35</p>
     * <p><b>{@code @return:}</b>{@link LocalDate}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDate parseLocalDate(long timestamp) {
        return parseLocalDateTime(timestamp).toLocalDate();
    }

    /**
     * <p><b>{@code @description:}</b>
     * epochDay to LocalDate
     * </p>
     *
     * <p><b>@param</b> <b>epochDay</b>
     * {@link long}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 9:54</p>
     * <p><b>{@code @return:}</b>{@link LocalDate}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDate parseLocalDate(int epochDay) {
        return LocalDate.ofEpochDay(epochDay);
    }

    /**
     * <p><b>{@code @description:}</b>
     * Year+DayOfYear to LocalDate
     * </p>
     *
     * <p><b>@param</b> <b>year</b>
     * {@link int}
     * </p>
     *
     * <p><b>@param</b> <b>dayOfYear</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 10:23</p>
     * <p><b>{@code @return:}</b>{@link LocalDate}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDate parseLocalDate(@Range(from = -5000, to = 5000) int year,
                                             @Range(from = 0, to = 366) int dayOfYear) {
        return LocalDate.ofYearDay(year, dayOfYear);
    }


    /**
     * <p><b>{@code @description:}</b>
     * Timestamp parse to LocalDateTime
     * </p>
     *
     * <p><b>@param</b> <b>timestamp</b>
     * {@link long}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 9:55</p>
     * <p><b>{@code @return:}</b>{@link LocalDateTime}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDateTime parseLocalDateTime(long timestamp) {
        return DateUtils.parseLocalDateTime(timestamp, ZoneId.systemDefault());
    }
    
    /**
     * <p><b>{@code @description:}</b>
     * Timestamp parse to LocalDateTime
     * </p>
     *
     * <p><b>@param</b> <b>timestamp</b>
     * {@link long}
     * </p>
     *
     * <p><b>@param</b> <b>zoneId</b>
     * {@link ZoneId}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 9:55</p>
     * <p><b>{@code @return:}</b>{@link LocalDateTime}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDateTime parseLocalDateTime(long timestamp, ZoneId zoneId) {
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDate parse to Date
     * </p>
     *
     * <p><b>@param</b> <b>localDate</b>
     * {@link LocalDate}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 9:55</p>
     * <p><b>{@code @return:}</b>{@link Date}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static Date parseDate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseDate(localDate, zoneId);
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDate parse to Date
     * </p>
     *
     * <p><b>@param</b> <b>localDate</b>
     * {@link LocalDate}
     * </p>
     *
     * <p><b>@param</b> <b>zoneId</b>
     * {@link ZoneId}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 9:55</p>
     * <p><b>{@code @return:}</b>{@link Date}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static Date parseDate(LocalDate localDate, ZoneId zoneId) {
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return Date.from(localDate.atStartOfDay(zoneId).toInstant());
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDateTime parse to Date
     * </p>
     *
     * <p><b>@param</b> <b>localDateTime</b>
     * {@link LocalDateTime}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 9:55</p>
     * <p><b>{@code @return:}</b>{@link Date}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static Date parseDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseDate(localDateTime, zoneId);
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDateTime parse to Date
     * </p>
     *
     * <p><b>@param</b> <b>localDateTime</b>
     * {@link LocalDateTime}
     * </p>
     *
     * <p><b>@param</b> <b>zoneId</b>
     * {@link ZoneId}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/28 9:55</p>
     * <p><b>{@code @return:}</b>{@link Date}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static Date parseDate(LocalDateTime localDateTime, ZoneId zoneId) {
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    /**
     * <p><b>{@code @description:}</b>
     * Date parse to LocalDate
     * </p>
     *
     * <p><b>@param</b> <b>date</b>
     * {@link Date}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/13 9:55</p>
     * <p><b>{@code @return:}</b>{@link LocalDate}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDate parseLocalDate(Date date) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseLocalDate(date, zoneId);
    }

    /**
     * <p><b>{@code @description:}</b>
     * Date parse to LocalDate
     * </p>
     *
     * <p><b>@param</b> <b>date</b>
     * {@link Date}
     * </p>
     *
     * <p><b>@param</b> <b>zoneId</b>
     * {@link ZoneId}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/13 9:55</p>
     * <p><b>{@code @return:}</b>{@link LocalDate}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDate parseLocalDate(@NonNull Date date, ZoneId zoneId) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return date.toInstant().atZone(zoneId).toLocalDate();
    }

    /**
     * <p><b>{@code @description:}</b>
     * Date parse to LocalDateTime
     * </p>
     *
     * <p><b>@param</b> <b>date</b>
     * {@link Date}
     * </p>
     *
     * <p><b>{@code @date:}</b>2019/10/6 9:56</p>
     * <p><b>{@code @return:}</b>{@link LocalDateTime}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDateTime parseLocalDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseLocalDateTime(date, zoneId);
    }

    /**
     * <p><b>{@code @description:}</b>
     * Date parse to LocalDateTime
     * </p>
     *
     * <p><b>@param</b> <b>date</b>
     * {@link Date}
     * </p>
     *
     * <p><b>@param</b> <b>zoneId</b>
     * {@link ZoneId}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/28 9:56</p>
     * <p><b>{@code @return:}</b>{@link LocalDateTime}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDateTime parseLocalDateTime(Date date, ZoneId zoneId) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return date.toInstant().atZone(zoneId).toLocalDateTime();
    }


    /**
     * <p><b>{@code @description:}</b>
     * String format to LocalDateTime with pattern
     * </p>
     *
     * <p><b>@param</b> <b>localDateTime</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>pattern</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>zoneId</b>
     * {@link ZoneId}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/13 9:56</p>
     * <p><b>{@code @return:}</b>{@link LocalDateTime}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String pattern, ZoneId zoneId) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期字符串不能为空");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime dateTime = LocalDateTime.parse(dateStr.trim(), dtf);
        return LocalDateTime.ofInstant(dateTime.toInstant(zoneId.getRules().getOffset(dateTime)), zoneId);
    }


    /**
     * <p><b>{@code @description:}</b>
     * String format to LocalDate with pattern
     * </p>
     *
     * <p><b>@param</b> <b>dateStr</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>pattern</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2019/10/6 9:56</p>
     * <p><b>{@code @return:}</b>{@link LocalDate}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static LocalDate parseLocalDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期字符串不能为空");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr.trim(), dtf);
    }

    /**
     * <p><b>{@code @description:}</b>
     * String format Date with pattern
     * </p>
     *
     * <p><b>@param</b> <b>dateStr</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>pattern</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2023/5/13 9:57</p>
     * <p><b>{@code @return:}</b>{@link Date}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    @SneakyThrows
    public static Date formatDate(String dateStr, String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.parse(dateStr);
    }
    
    /**
     * <p><b>{@code @description:}</b>
     * LocalDateTime format String with pattern
     * </p>
     *
     * <p><b>@param</b> <b>localDateTime</b>
     * {@link LocalDateTime}
     * </p>
     *
     * <p><b>@param</b> <b>pattern</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2019/10/6 9:57</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDateTime format String
     * </p>
     *
     * <p><b>@param</b> <b>localDateTime</b>
     * {@link LocalDateTime}
     * </p>
     *
     * <p><b>{@code @date:}</b>2023/5/13 9:57</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, YYYYMMDDHHMMSS);
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDate format String with pattern
     * </p>
     *
     * <p><b>@param</b> <b>localDate</b>
     * {@link LocalDate}
     * </p>
     *
     * <p><b>@param</b> <b>pattern</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2019/10/6 9:57</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static String formatLocalDate(LocalDate localDate, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(dateTimeFormatter);
    }

    /**
     * <p><b>{@code @description:}</b>
     * LocalDate format String
     * </p>
     *
     * <p><b>@param</b> <b>localDate</b>
     * {@link LocalDate}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/6 9:57</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static String formatLocalDate(LocalDate localDate) {
        return formatLocalDate(localDate, YYYYMMDD);
    }

    /**
     * <p><b>{@code @description:}</b>
     * Time format to String with default pattern
     * </p>
     *
     * <p><b>@param</b> <b>localTime</b>
     * {@link LocalTime}
     * </p>
     *
     * <p><b>{@code @date:}</b>2021/7/28 9:57</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static String formatLocalTime(LocalTime localTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(HHMMSS);
        return localTime.format(dateTimeFormatter);
    }

    /**
     * <p><b>{@code @description:}</b>
     * Date format String with default pattern
     * </p>
     *
     * <p><b>@param</b> <b>date</b>
     * {@link Date}
     * </p>
     *
     * <p><b>{@code @date:}</b>2023/5/13 9:57</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static String formatDate(Date date){
        return formatDate(date, YYYYMMDDHHMMSS);
    }
    @Deprecated
    public static String formatLocalTime(Date date){
        return formatDate(date, YYYYMMDDHHMMSS);
    }

    /**
     * <p><b>{@code @description:}</b>
     * Date format String with pattern
     * </p>
     *
     * <p><b>@param</b> <b>date</b>
     * {@link Date}
     * </p>
     *
     * <p><b>@param</b> <b>pattern</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2019/10/6 9:57</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static String formatDate(Date date, String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }
    @Deprecated
    public static String formatLocalTime(Date date, String pattern){
        return formatDate(date, pattern);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 友好时间~~中文
     * </p>
     *
     * <p><b>@param</b> <b>dateTime</b>
     * {@link LocalDateTime}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/4/21 13:15</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String friendCNLocalDateTime(LocalDateTime dateTime){
        LocalDateTime curDateTime = LocalDateTime.now();
        long timestamp = DateUtils.parseTimestamp(dateTime);
        long curTimestamp = DateUtils.parseTimestamp(curDateTime);
        long offSeconds = (curTimestamp - timestamp) / 1000;
        String endSuffix;
        if(offSeconds > 0){
            endSuffix = "之前";
        }else if(offSeconds < 0){
            endSuffix = "之后";
        }else {
            return "此时此刻";
        }
        offSeconds = Math.abs(offSeconds);
        if(offSeconds < 60){
            return getUnit2Number(offSeconds) + "秒" + endSuffix;
        }
        long offMinutes = offSeconds / 60;
        if(offMinutes < 60){
            long vgSeconds = offSeconds % 60;
            if(vgSeconds == 0){
                return getUnit2Number(offMinutes) + "分钟" + endSuffix;
            }
            return getUnit2Number(offMinutes) + "分" + getUnit2Number(vgSeconds) + "秒"  + endSuffix;
        }
        long offHours = offMinutes / 60;
        if(offHours < 24){
            long vgMinutes = offMinutes % 60;
            if(vgMinutes == 0){
                return getUnit2Number(offHours) + "小时" + endSuffix;
            }
            return getUnit2Number(offHours) + "小时" + getUnit2Number(vgMinutes) + "分" + endSuffix;
        }
        long offDay = offHours / 24;
        if(offDay < 31){
            long vgHours = offHours % 24;
            if(vgHours == 0){
                return getUnit2Number(offDay) + "天" + endSuffix;
            }
            return getUnit2Number(offDay) + "天" + getUnit2Number(vgHours) + "小时"  + endSuffix;
        }
        return DateUtils.formatLocalDateTime(dateTime, "YY/MM/dd HH:mm:ss");
    }

    // ==================== 日期加减（返回 Date） ====================

    public static Date plusDays(Date date, int days) {
        return modify(date, d -> d.plusDays(days));
    }

    public static Date minusDays(Date date, int days) {
        return plusDays(date, -days);
    }

    public static Date plusHours(Date date, int hours) {
        return modify(date, d -> d.plusHours(hours));
    }

    public static Date minusHours(Date date, int hours) {
        return plusHours(date, -hours);
    }

    public static Date plusMinutes(Date date, int minutes) {
        return modify(date, d -> d.plusMinutes(minutes));
    }

    public static Date minusMinutes(Date date, int minutes) {
        return plusMinutes(date, -minutes);
    }

    public static Date plusSeconds(Date date, int seconds) {
        return modify(date, d -> d.plusSeconds(seconds));
    }

    public static Date minusSeconds(Date date, int seconds) {
        return plusSeconds(date, -seconds);
    }

    public static Date plusMonths(Date date, int months) {
        return modify(date, d -> d.plusMonths(months));
    }

    public static Date minusMonths(Date date, int months) {
        return plusMonths(date, -months);
    }

    public static Date plusYears(Date date, int years) {
        return modify(date, d -> d.plusYears(years));
    }

    public static Date minusYears(Date date, int years) {
        return plusYears(date, -years);
    }

    public static Date plusWeeks(Date date, int weeks) {
        return modify(date, d -> d.plusWeeks(weeks));
    }

    public static Date minusWeeks(Date date, int weeks) {
        return plusWeeks(date, -weeks);
    }

    // ==================== 私有辅助方法 ====================

    @FunctionalInterface
    private interface LocalDateTimeOperator {
        LocalDateTime apply(LocalDateTime dateTime);
    }

    /**
     * 修改时间
     */
    private static Date modify(Date date, LocalDateTimeOperator operator) {
        Objects.requireNonNull(date, "date must not be null");
        LocalDateTime ldt = parseLocalDateTime(date);
        LocalDateTime result = operator.apply(ldt);
        return parseDate(result);
    }

    /**
     * 获取数字的2位
     */
    private static String getUnit2Number(long n){
        long un = Math.abs(n);
        if(un < 10){
            return "0" + un;
        }
        return "" + un;
    }

    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String HHMMSS = "HH:mm:ss";
    public static final String YYYYMMDD = "yyyy-MM-dd";


    private static final List<DateTimeFormatter> FORMATTERS = new ArrayList<>(){{
        // 按优先级添加，最可能匹配的放前面
        add(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)); // CST
        add(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        add(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        add(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        add(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        add(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }};

}
