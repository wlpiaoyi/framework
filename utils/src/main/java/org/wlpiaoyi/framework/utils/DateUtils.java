package org.wlpiaoyi.framework.utils;


import lombok.NonNull;

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
     * LocalTime to NanoOfDay
     * @param localTime
     * @return
     */
    public static long toNanoOfDay(@NonNull LocalTime localTime) {
        return localTime.toNanoOfDay();
    }
    /**
     * LocalDate to epochDay
     * @param localDate
     * @return
     */
    public static long toEpochDay(@NonNull LocalDate localDate) {
        return localDate.toEpochDay();
    }

    /**
     * LocalDateTime get to Timestamp
     * @param localDateTime
     * @return
     */
    public static long toTimestamp(@NonNull LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.toTimestamp(localDateTime, zoneId);
    }

    /**
     * LocalDateTime get to Timestamp
     * @param localDateTime
     * @param zoneId
     * @return
     */
    public static long toTimestamp(@NonNull LocalDateTime localDateTime, ZoneId zoneId) {
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
     * nanoOfDay to LocalTime
     * @param nanoOfDay
     * @return
     */
    public static LocalTime toLocalTime(long nanoOfDay) {
        return LocalTime.ofNanoOfDay(nanoOfDay);
    }

    /**
     * epochDay to LocalDate
     * @param epochDay
     * @return
     */
    public static LocalDate toLocalDate(long epochDay) {
        return LocalDate.ofEpochDay(epochDay);
    }

    /**
     * Timestamp get to LocalDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return DateUtils.toLocalDateTime(timestamp, ZoneId.systemDefault());
    }

    /**
     * Timestamp get to LocalDateTime
     * @param timestamp
     * @param zoneId
     * @return
     */
    public static LocalDateTime toLocalDateTime(long timestamp, ZoneId zoneId) {
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
    }

    /**
     * LocalDate get to Date
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.localDateToDate(localDate, zoneId);
    }

    /**
     * LocalDate get to Date
     * @param localDate
     * @param zoneId
     * @return
     */
    public static Date localDateToDate(LocalDate localDate, ZoneId zoneId) {
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return Date.from(localDate.atStartOfDay(zoneId).toInstant());
    }

    /**
     * LocalDateTime get to Date
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.localDateTimeToDate(localDateTime, zoneId);
    }

    /**
     * LocalDateTime get to Date
     * @param localDateTime
     * @param zoneId
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime, ZoneId zoneId) {
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    /**
     * Date get to LocalDate
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.dateToLocalDate(date, zoneId);
    }

    /**
     * Date get to LocalDate
     * @param date
     * @param zoneId
     * @return
     */
    public static LocalDate dateToLocalDate(@NonNull Date date, ZoneId zoneId) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return date.toInstant().atZone(zoneId).toLocalDate();
    }

    /**
     * Date get to LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        ZoneId zoneId = ZoneId.systemDefault();
        return DateUtils.dateToLocalDateTime(date, zoneId);
    }

    /**
     * Date get to LocalDateTime
     * @param date
     * @param zoneId
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date, ZoneId zoneId) {
        if (date == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(zoneId == null){
            zoneId = ZoneId.systemDefault();
        }
        return date.toInstant().atZone(zoneId).toLocalDateTime();
    }


    /**
     * String parse to LocalDateTime
     * @param localDateTime
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String localDateTime) {
        return parseLocalDateTime(localDateTime, YYYYMMDDHHMMSS);
    }

    /**
     * String parse to LocalDateTime with pattern
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String localDateTime, String pattern) {
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
     * @param zoneId
     * @return
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
     * String parse to LocalDate
     * @param localDate
     * @return
     */
    public static LocalDate parseLocalDate(String localDate) {
        return parseLocalDate(localDate, YYYYMMDD);
    }

    /**
     * String parse to LocalDate with pattern
     * @param localDate
     * @param pattern
     * @return
     */
    public static LocalDate parseLocalDate(String localDate, String pattern) {
        if (ValueUtils.isBlank(localDate)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(localDate, dateTimeFormatter);
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
     *
     * @param localTime
     * @return
     */
    public static String formatLocalTime(LocalTime localTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(HHMMSS);
        return localTime.format(dateTimeFormatter);
    }

    /**
     * Date formate String with pattern
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }





}
