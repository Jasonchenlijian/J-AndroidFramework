package com.clj.jaf.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class JTimeUtil {

    public static SimpleDateFormat yearmonthFormat = new SimpleDateFormat("yyyy-MM");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat fulTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat fulTimeFormat_Minute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat hourTimeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat monthTimeFormat = new SimpleDateFormat("MM-dd HH:mm");

    /**
     * 比较两个String日期的大小
     * 返回：两天是否是同一天
     */
    public static boolean compare_date(String DATE1, String DATE2) {

        try {
            Date date1 = dateFormat.parse(DATE1);
            Date date2 = dateFormat.parse(DATE2);
            return date1.equals(date2);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * 从时间戳获取年月日
     */
    public static String getYearMonDay(long time) {
        Date date = new Date(time);
        String strTime = dateFormat.format(date);
        date = null;
        return strTime;
    }

    /**
     * 从时间戳获取年月日时分
     */
    public static String getYearMonDayHourMinute(long time) {
        Date date = new Date(time);
        String strTime = fulTimeFormat_Minute.format(date);
        date = null;
        return strTime;
    }

    /**
     * 从时间戳获取月日时分
     */
    public static String getMonDayHourMinute(long time) {
        Date date = new Date(time);
        String strTime = monthTimeFormat.format(date);
        date = null;
        return strTime;
    }

    /**
     * 从时间戳获取时分
     */
    public static String getHourMinute(long time) {
        Date date = new Date(time);
        String strTime = hourTimeFormat.format(date);
        date = null;
        return strTime;
    }

    /**
     * String转时间戳（通用）
     */
    public static String getTime(String user_time, String format) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    /**
     * 时间戳转成date形式的String
     */
    public static String date2string(long s, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(s);
        return sdf.format(date);
    }


    /**
     * 判断是否为闰年
     */
    public static boolean isLeapYear(int year) {
        if (year % 100 == 0) {
            return year % 400 == 0;
        }
        return year % 4 == 0;
    }

    /**
     * 返回星期
     */
    public static String getWeek(Date date) {
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    public static long[] getMinMaxByDay(long dayTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dayTime);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        long minValue = calendar.getTimeInMillis();
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        long maxValue = calendar.getTimeInMillis();
        calendar = null;
        return new long[]{minValue, maxValue};
    }

    public static String getHourTimeString(long calltime) {
        String info = "";
        Date callTime = new Date(calltime);
        info = hourTimeFormat.format(callTime);
        return info;
    }

    public static String getFullTime(long time) {
        Date date = new Date(time);
        String strTime = fulTimeFormat.format(date);
        date = null;
        return strTime;
    }

}
