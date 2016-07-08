package com.qiandai.pay.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 文件名：DateUtils.java 日期处理相关工具类
 * @date 2016-7-7
 * @author mashengli
 */
public class DateUtils {

    /**定义常量**/
    public static final String DATE_JFP_STR="yyyyMM";
    public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_SMALL_STR = "yyyy-MM-dd";
    public static final String DATE_KEY_STR = "yyyyMMddHHmmss";

    /**
     * 使用预设格式提取字符串日期
     * @param strDate 日期字符串
     * @return
     */
    public static Date parse(String strDate) {
        return parse(strDate,DATE_FULL_STR);
    }

    /**
     * 使用用户格式提取字符串日期
     * @param dateStr 日期字符串
     * @param pattern 日期格式
     * @return
     */
    public static Date parse(String dateStr, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 两个日期类型时间比较
     * @param date
     * @return
     */
    public static int compareDateWithNow(Date date){
        Date now = new Date();
        return date.compareTo(now);
    }

    /**
     * 两个时间比较(时间戳比较)
     * @param date
     * @return
     */
    public static int compareDateWithNow(long date) {
        long now = dateToUnixTimestamp();
        if (date > now) {
            return 1;
        } else if (date < now) {
            return -1;
        } else {
            return 0;
        }
    }


    /**
     * 获取系统当前时间
     * @return
     */
    public static String getNowTime() {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FULL_STR);
        return df.format(new Date());
    }

    /**
     * 获取系统当前时间
     * @return
     */
    public static String getNowTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 获取系统当前计费期
     * @return
     */
    public static String getJFPTime() {
        SimpleDateFormat df = new SimpleDateFormat(DATE_JFP_STR);
        return df.format(new Date());
    }

    /**
     * 将指定的日期转换成Unix时间戳
     * @param date 需要转换的日期 yyyy-MM-dd HH:mm:ss
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp(String date) {
        long timestamp = 0;
        try {
            timestamp = new SimpleDateFormat(DATE_FULL_STR).parse(date).getTime();
        } catch (ParseException ignored) {
        }
        return timestamp;
    }

    /**
     * 将指定的日期转换成Unix时间戳
     * @param date 需要转换的日期 yyyy-MM-dd
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp(String date, String dateFormat) {
        long timestamp = 0;
        try {
            timestamp = new SimpleDateFormat(dateFormat).parse(date).getTime();
        } catch (ParseException ignored) {
        }
        return timestamp;
    }

    /**
     * 将当前日期转换成Unix时间戳
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp() {
        return new Date().getTime();
    }


    /**
     * 将Unix时间戳转换成日期
     * @param timestamp 时间戳
     * @return String 日期字符串
     */
    public static String unixTimestampToDate(long timestamp) {
        SimpleDateFormat sd = new SimpleDateFormat(DATE_FULL_STR);
        sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sd.format(new Date(timestamp));
    }

    /**
     * 对日期进行天数加减
     * @param date 要加减的日期
     * @param amount 加的天数（负数表示减）
     * @return
     */
    public static Date addDay(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }

    /**
     * 对日期进行月份加减
     * @param date 要加减的日期
     * @param amount 加的月数（负数表示减）
     * @return
     */
    public static Date addMonth(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, amount);
        return calendar.getTime();
    }

    /**
     * 对日期进行年份加减
     * @param date 要加减的日期
     * @param amount 加的年数（负数表示减）
     * @return
     */
    public static Date addYear(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, amount);
        return calendar.getTime();
    }

    /**
     * 计算日期相差天数(不计时分秒)
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 返回负数表示前边日期大于后边日期
     */
    public static int getIntervalDays(Date startDate, Date endDate) throws NullPointerException{
        if (null == startDate || null == endDate) {
            throw new NullPointerException("日期为空");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_SMALL_STR);
        try {
            startDate = sdf.parse(sdf.format(startDate));
            endDate = sdf.parse(sdf.format(endDate));
            return (int) ((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
        } catch (ParseException e) {
            throw new NullPointerException();
        }
    }

}
