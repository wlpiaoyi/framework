package org.wlpiaoyi.framework.utils;


import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具
 */
public class DateTimeUtils {

    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String HHMMSS = "HH:mm:ss";
    public static final String YYYYMMDD = "yyyy-MM-dd";

    /**
     * LocalDateTime get to Timestamp
     * @param localDateTime
     * @return
     */
    public static long getTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     * Timestamp get to LocalDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime getLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * LocalDate parse to Date
     * @param localDate
     * @return
     */
    public static Date parsetoDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime parse to Date
     * @param localDateTime
     * @return
     */
    public static Date parsetoDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone( ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date parse to LocalDatevbv
     * @param date
     * @return
     */
    public static LocalDate parsetoLocalDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date parse to LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime parsetoLocalDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * String parse to LocalDateTime with pattern
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static LocalDateTime parsetoLocalDateTime(String localDateTime, String pattern) {
        if (StringUtils.isBlank(localDateTime)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(localDateTime, dateTimeFormatter);
    }

    /**
     * String parse to LocalDateTime
     * @param localDateTime
     * @return
     */
    public static LocalDateTime parsetoLocalDateTime(String localDateTime) {
        return parsetoLocalDateTime(localDateTime, YYYYMMDDHHMMSS);
    }


    /**
     * String parse to LocalDate with pattern
     * @param localDate
     * @param pattern
     * @return
     */
    public static LocalDate parsetoLocalDate(String localDate, String pattern) {
        if (StringUtils.isBlank(localDate)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(localDate, dateTimeFormatter);
    }

    /**
     * String parse to LocalDate
     * @param localDate
     * @return
     */
    public static LocalDate parsetoLocalDate(String localDate) {
        return parsetoLocalDate(localDate, YYYYMMDD);
    }

    /**
     * LocalDateTime formate String with pattern
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * LocalDateTime formate String
     * @param localDateTime
     * @return
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, YYYYMMDDHHMMSS);
    }

    /**
     * LocalDate formate String with pattern
     * @param localDate
     * @param pattern
     * @return
     */
    public static String formatLocalDate(LocalDate localDate, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(dateTimeFormatter);
    }

    /**
     * LocalDate formate String
     * @param localDate
     * @return
     */
    public static String formatLocalDateTime(LocalDate localDate) {
        return formatLocalDate(localDate, YYYYMMDD);
    }

    /**
     * Date formate String with pattern
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return  dateFormat.format(date);
    }





}
