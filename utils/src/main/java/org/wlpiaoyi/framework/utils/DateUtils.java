package org.wlpiaoyi.framework.utils;


import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Range;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p><b>{@code @description:}</b>  时间工具</p>
 * <p><b>{@code @date:}</b>         2019/10/6 9:51</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class DateUtils {



    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String HHMMSS = "HH:mm:ss";
    public static final String YYYYMMDD = "yyyy-MM-dd";


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
     */
    @SneakyThrows
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期字符串不能为空");
        }
        dateStr = dateStr.trim();
        SimpleDateFormat sdf;
        if (CST_PATTERN.matcher(dateStr).matches()) {
            sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        } else if (SLASH_DATE_TIME.matcher(dateStr).matches()) {
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        } else if (SLASH_DATE_TIME_NO_SEC.matcher(dateStr).matches()) {
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        } else if (LINE_DATE_TIME.matcher(dateStr).matches()) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else if (LINE_DATE_TIME_NO_SEC.matcher(dateStr).matches()) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        } else if (COMPACT_DATE_TIME.matcher(dateStr).matches()) {
            sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        } else if (COMPACT_DATE_TIME_NO_SEC.matcher(dateStr).matches()) {
            sdf = new SimpleDateFormat("yyyyMMddHHmm");
        } else if (dateStr.length() == 8) {
            // 纯日期 20260210
            sdf = new SimpleDateFormat("yyyyMMdd");
        } else throw new IllegalArgumentException("日期字符串格式错误");
        return sdf.parse(dateStr);
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
        return DateUtils.formatDate(localDateTime, zoneId);
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
    public static Date formatDate(LocalDateTime localDateTime, ZoneId zoneId) {
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
     */
    public static LocalDateTime parseLocalDateTime(String localDateTime) {
        return parseLocalDateTime(localDateTime, YYYYMMDDHHMMSS);
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
    public static LocalDateTime parseLocalDateTime(String localDateTime, String pattern) {
        if (ValueUtils.isBlank(localDateTime)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(localDateTime, dateTimeFormatter);
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
    public static LocalDateTime parseLocalDateTime(String localDateTime, String pattern, ZoneId zoneId) {
        if (ValueUtils.isBlank(localDateTime)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime dateTime = LocalDateTime.parse(localDateTime, dateTimeFormatter);

        return LocalDateTime.ofInstant(dateTime.toInstant(zoneId.getRules().getOffset(dateTime)), zoneId);
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
    public static LocalDate parseLocalDate(String localDate) {
        return parseLocalDate(localDate, YYYYMMDD);
    }


    /**
     * <p><b>{@code @description:}</b>
     * String format to LocalDate with pattern
     * </p>
     *
     * <p><b>@param</b> <b>localDate</b>
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
    public static LocalDate parseLocalDate(String localDate, String pattern) {
        if (ValueUtils.isBlank(localDate)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(localDate, dateTimeFormatter);
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

    // 定义各种日期格式的正则表达式
    private static final Pattern CST_PATTERN =
            Pattern.compile("[A-Za-z]{3} [A-Za-z]{3} \\d{2} \\d{2}:\\d{2}:\\d{2} [A-Z]{3} \\d{4}");
    private static final Pattern SLASH_DATE_TIME =
            Pattern.compile("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}");
    private static final Pattern SLASH_DATE_TIME_NO_SEC =
            Pattern.compile("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}");
    private static final Pattern LINE_DATE_TIME =
            Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    private static final Pattern LINE_DATE_TIME_NO_SEC =
            Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
    private static final Pattern COMPACT_DATE_TIME =
            Pattern.compile("\\d{14}");
    private static final Pattern COMPACT_DATE_TIME_NO_SEC =
            Pattern.compile("\\d{12}");

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

//    public static void main(String[] args) {
//        Date date = new Date();
//        String dateStr = date.toString();
//        Date d = DateUtils.parseDate(dateStr);
//        System.out.println();
//    }

}
