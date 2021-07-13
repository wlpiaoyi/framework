package org.wlpiaoyi.framework.utils;


import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具
 */
public class DateUtils {

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
        return DateUtils.getLocalDateTime(timestamp, ZoneId.systemDefault());
    }

    /**
     * Timestamp get to LocalDateTime
     * @param timestamp
     * @param zone
     * @return
     */
    public static LocalDateTime getLocalDateTime(long timestamp, ZoneId zone) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zone);
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
     * LocalDate parse to Date
     * @param localDate
     * @param zone
     * @return
     */
    public static Date parsetoDate(LocalDate localDate, ZoneId zone) {
        return Date.from(localDate.atStartOfDay(zone).toInstant());
    }

    /**
     * LocalDateTime parse to Date
     * @param localDateTime
     * @return
     */
    public static Date parsetoDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date parse to LocalDate
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
     * Date parse to LocalDate
     * @param date
     * @param zone
     * @return
     */
    public static LocalDate parsetoLocalDate(Date date, ZoneId zone) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return date.toInstant().atZone(zone).toLocalDate();
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
     * Date parse to LocalDateTime
     * @param date
     * @param zone
     * @return
     */
    public static LocalDateTime parsetoLocalDateTime(Date date, ZoneId zone) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return date.toInstant().atZone(zone).toLocalDateTime();
    }

    /**
     * String parse to LocalDateTime with pattern
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static LocalDateTime parsetoLocalDateTime(String localDateTime, String pattern) {
        if (ValueUtils.isBlank(localDateTime)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(localDateTime, dateTimeFormatter);
    }


    /**
     * String parse to LocalDateTime with pattern
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static LocalDateTime parsetoLocalDateTime(String localDateTime, String pattern, ZoneId zone) {
        if (ValueUtils.isBlank(localDateTime)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime dateTime = LocalDateTime.parse(localDateTime, dateTimeFormatter);

        return LocalDateTime.ofInstant(dateTime.toInstant(ZoneOffset.ofTotalSeconds(0)), zone);
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
    public static String formatLocalDate(LocalDate localDate) {
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
