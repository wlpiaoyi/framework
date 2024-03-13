package org.wlpiaoyi.framework.utils;


import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Range;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>{@code @description:}</b>  时间工具</p>
 * <p><b>{@code @date:}</b>         2019/10/6 9:51</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class DateUtils {


//    public static final String PATTERN_STR = "^(\\d{2,4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2})$";

    public static final String HOUR_FORMAT_TAG = "_{H}_";
    public static final String MINUTE_FORMAT_TAG = "_{m}_";
    public static final String SECOND_FORMAT_TAG = "_{s}_";

    public static final Map<String, String> PATTERN_FORMAT_TIME = new HashMap(){{
        put("HH" + HOUR_FORMAT_TAG +"mm" + MINUTE_FORMAT_TAG +"ss" + SECOND_FORMAT_TAG,"\\d{2}" + HOUR_FORMAT_TAG +"\\d{2}" + MINUTE_FORMAT_TAG +"\\d{2}" + SECOND_FORMAT_TAG);
        put("H" + HOUR_FORMAT_TAG +"m" + MINUTE_FORMAT_TAG +"ss" + SECOND_FORMAT_TAG,"\\d{1,2}" + HOUR_FORMAT_TAG +"\\d{1,2}" + MINUTE_FORMAT_TAG +"\\d{1,2}" + SECOND_FORMAT_TAG);
        put("HH" + HOUR_FORMAT_TAG +"mm" + MINUTE_FORMAT_TAG,"\\d{2}" + HOUR_FORMAT_TAG +"\\d{2}" + MINUTE_FORMAT_TAG);
        put("H" + HOUR_FORMAT_TAG +"m" + MINUTE_FORMAT_TAG,"\\d{1,2}" + HOUR_FORMAT_TAG +"\\d{1,2}" + MINUTE_FORMAT_TAG);
    }};

    public static final String YEAR_FORMAT_TAG = "_{y}_";
    public static final String MONTH_FORMAT_TAG = "_{M}_";
    public static final String DAY_FORMAT_TAG = "_{d}_";
    public static final Map<String, String> PATTERN_FORMAT_DATE = new HashMap(){{
        put("yyyy" + YEAR_FORMAT_TAG + "MM" + MONTH_FORMAT_TAG + "dd" + DAY_FORMAT_TAG,"\\d{4}" + YEAR_FORMAT_TAG + "\\d{2}" + MONTH_FORMAT_TAG + "\\d{2}" + DAY_FORMAT_TAG);
        put("yyyy" + YEAR_FORMAT_TAG + "MM" + MONTH_FORMAT_TAG ,"\\d{4}" + YEAR_FORMAT_TAG + "\\d{2}" + MONTH_FORMAT_TAG);
        put("yy" + YEAR_FORMAT_TAG + "M" + MONTH_FORMAT_TAG + "d" + DAY_FORMAT_TAG,"\\d{2}" + YEAR_FORMAT_TAG + "\\d{1,2}" + MONTH_FORMAT_TAG + "\\d{1,2}" + DAY_FORMAT_TAG);
        put("yy" + YEAR_FORMAT_TAG + "M" + MONTH_FORMAT_TAG ,"\\d{2}" + YEAR_FORMAT_TAG + "\\d{1,2}" + MONTH_FORMAT_TAG);
    }};

    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String HHMMSS = "HH:mm:ss";
    public static final String YYYYMMDD = "yyyy-MM-dd";


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
    public static long parseToNanoOfDay(@NonNull LocalTime localTime) {
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
    public static long parseToEpochDay(@NonNull LocalDate localDate) {
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
    public static long parseToTimestamp(@NonNull LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseToTimestamp(localDateTime, zoneId);
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
    public static long parseToTimestamp(LocalDateTime localDateTime, ZoneId zoneId) {
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
    public static LocalTime parseToLocalTime(long nanoOfDay) {
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
    public static LocalDate parseToLocalDate(long timestamp) {
        return parseToLocalDateTime(timestamp).toLocalDate();
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
    public static LocalDate parseToLocalDate(int epochDay) {
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
    public static LocalDate parseToLocalDate(@Range(from = -5000, to = 5000) int year,
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
    public static LocalDateTime parseToLocalDateTime(long timestamp) {
        return DateUtils.parseToLocalDateTime(timestamp, ZoneId.systemDefault());
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
    public static LocalDateTime parseToLocalDateTime(long timestamp, ZoneId zoneId) {
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
    public static Date parseToDate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseToDate(localDate, zoneId);
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
    public static Date parseToDate(LocalDate localDate, ZoneId zoneId) {
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
    public static Date parseToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseToDate(localDateTime, zoneId);
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
    public static Date parseToDate(LocalDateTime localDateTime, ZoneId zoneId) {
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
    public static LocalDate parseToLocalDate(Date date) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseToLocalDate(date, zoneId);
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
    public static LocalDate parseToLocalDate(@NonNull Date date, ZoneId zoneId) {
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
    public static LocalDateTime parseToLocalDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.parseToLocalDateTime(date, zoneId);
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
    public static LocalDateTime parseToLocalDateTime(Date date, ZoneId zoneId) {
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
    public static LocalDateTime formatToLoaTolDateTime(String localDateTime) {
        return formatToLoaTolDateTime(localDateTime, YYYYMMDDHHMMSS);
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
    public static LocalDateTime formatToLoaTolDateTime(String localDateTime, String pattern) {
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
    public static LocalDateTime formatToLoaTolDateTime(String localDateTime, String pattern, ZoneId zoneId) {
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
    public static LocalDate formatLocalDate(String localDate) {
        return formatLocalDate(localDate, YYYYMMDD);
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
    public static LocalDate formatLocalDate(String localDate, String pattern) {
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
     * <p><b>{@code @date:}</b>2023/5/13 9:56</p>
     * <p><b>{@code @return:}</b>{@link Date}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    @SneakyThrows
    public static Date formatToDate(String dateStr){
        return formatToDate(dateStr, YYYYMMDDHHMMSS);
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
    public static Date formatToDate(String dateStr, String pattern){
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

    public static void main(String[] args) {
        System.out.println();
    }

}
