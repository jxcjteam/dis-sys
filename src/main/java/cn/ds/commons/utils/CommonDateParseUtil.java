package cn.ds.commons.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * CommonDateParseUtil
 *
 * @author zj
 * @date 2016/1/29
 */
public class CommonDateParseUtil extends org.apache.commons.lang3.time.DateFormatUtils{

    public static final String ENG_DATE_FORMAT_YYYY = "EEE MMM dd HH:mm:ss z yyyy";
    public static final String ENG_DATE_FROMAT = "EEE, d MMM yyyy HH:mm:ss z";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String M_D = "M月d日";
    public static final String YYYY = "yyyy";
    public static final String MM = "MM";
    public static final String DD = "dd";
    public static final String HHMMSS = "HH:mm:ss";
    public static final String HHMM = "HH:mm";
    public static final String DEFAULT_DATE_PATTERN = YYYY_MM_DD_HH_MM_SS;
    /**
     * 格式化日期对象
     * @param date
     * @return
     */
    public static Date date2date(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String str = sdf.format(date);
        try {
            date = sdf.parse(str);
        } catch (Exception e) {
            return null;
        }
        return date;
    }

    /**
     * 时间对象转换成字符串
     * @param date
     * @return
     */
    public static String date2string(Date date, String formatStr) {
        String strDate;
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        strDate = sdf.format(date);
        return strDate;
    }

    /**
     * sql时间对象转换成字符串
     * @param timestamp
     * @param formatStr
     * @return
     */
    public static String timestamp2string(Timestamp timestamp, String formatStr) {
        String strDate;
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        strDate = sdf.format(timestamp);
        return strDate;
    }

    /**
     * 字符串转换成时间对象
     * @param dateString
     * @param formatStr
     * @return
     */
    public static Date string2date(String dateString, String formatStr) {
        Date formateDate = null;
        DateFormat format = new SimpleDateFormat(formatStr);
        try {
            formateDate = format.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        return formateDate;
    }

    /**
     * Date类型转换为Timestamp类型
     * @param date
     * @return
     */
    public static Timestamp date2timestamp(Date date) {
        if (date == null)
            return null;
        return new Timestamp(date.getTime());
    }

    /**
     * 获得当前年份
     * @return
     */
    public static String getNowYear() {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY);
        return sdf.format(new Date());
    }

    /**
     * 获得当前月份
     * @return
     */
    public static String getNowMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat(MM);
        return sdf.format(new Date());
    }

    /**
     * 获得当前日期中的日
     * @return
     */
    public static String getNowDay(){
        SimpleDateFormat sdf = new SimpleDateFormat(DD);
        return sdf.format(new Date());
    }

    /**
     * 指定时间距离当前时间的中文信息
     * @param time
     * @return
     */
    public static String getLnow(long time) {
        Calendar cal = Calendar.getInstance();
        long timel = cal.getTimeInMillis() - time;
        if (timel / 1000 < 60) {
            return "1分钟以内";
        } else if (timel / 1000 / 60 < 60) {
            return timel / 1000 / 60 + "分钟前";
        } else if (timel / 1000 / 60 / 60 < 24) {
            return timel / 1000 / 60 / 60 + "小时前";
        } else {
            return timel / 1000 / 60 / 60 / 24 + "天前";
        }
    }

    /**
     * @param time
     * @return
     * @Description: long类型转换成日期
     */
    public static String longToStringDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        return sd.format(date);
    }

    /**
     * long类型转换成字符串的日期格式
     *
     * @param time    毫秒级
     * @param pattern DateUtil的几个日期格式
     * @return pattern指定的格式时间字符串
     * @Description: long类型转换成日期
     */
    public static String longToStringDate(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat(pattern);
        return sd.format(date);
    }

    public static String formatDate(Date date) {
        return format(date, DEFAULT_DATE_PATTERN);
    }

    public static String formatDate(Date date, String pattern) {
        return format(date, pattern);
    }

}
