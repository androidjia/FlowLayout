package com.jjs.zero.utillibrary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/23
 * @Details: <日期管理类>
 *     一般情况下使用，
 *     当遇到多线程时候使用ThreadLocal 或者使用Joda-Time类库
 */
public class DateUtils {

    public static final String STRPATTEN = "yyyy-MM-dd HH:mm:ss";
    public static final String STRPATTENDayLine = "yyyy/MM/dd";
    public static final String STRPATTENDayTimeLine = "yyyy/MM/dd HH:mm:ss";
    public static final String STRPHOUR = "HH:mm:ss";

    /**
     *
     * @param str
     * @param formate
     * @return
     */
    public static Long strToTime(String str,String formate) {
        try {
            Date date =  new SimpleDateFormat(formate).parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String timeToStr(Long time) {
        return timeToStr(time,STRPATTEN);
    }

    public static String timeToStr(Long time,String formate) {
        if (time!=null && time >0 ) {
            return timeToStr(new Date(time),formate);
        } else {
            return "";
        }
    }

    public static String timeToStr(Date date,String formate) {
        if (date != null) {
            return new SimpleDateFormat(formate).format(date);
        }
        return "";
    }

    /**
     * 获取 单个 年月日时分秒等
     * @param time
     * @param type
     * @return
     */
    public static int getCalendar(Long time,int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(type);
    }

    /*
     *计算time2减去time1的差值 差值只设置 几天 几个小时 或 几分钟
     * 根据差值返回多长之间前或多长时间后
     * */
    public static String getDistanceTime(long time1, long time2) {
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        return getDistanceTime(diff);
    }

    public static String getDistanceTime(long diff) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) return day + "天"+hour + "小时"+min + "分钟" + sec + "秒";
        if (hour != 0) return hour + "小时"+ min + "分钟" + sec + "秒";
        if (min != 0) return min + "分钟" + sec + "秒";
        if (sec != 0) return sec + "秒" ;
        return "0秒";
    }


    /**
     * 获取年龄
     * @param birthdayTime
     * @return
     */
    public static String getAgeByTime(long birthdayTime) {
        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH) + 1;
        int dayNow = calendar.get(Calendar.DATE);
        int birthdayYear = getCalendar(birthdayTime,Calendar.YEAR);
        int birthdayMonth = getCalendar(birthdayTime,Calendar.MONTH)+1;
        int birthdayDay = getCalendar(birthdayTime,Calendar.DATE);

        int yearMinus = yearNow - birthdayYear;
        int monthMinus = monthNow - birthdayMonth;
        int dayMinus = dayNow - birthdayDay;
        int age = yearMinus;
        if (yearMinus<=0) {
            return 0+"";
        }

        if (monthMinus<0) {
            age -= 1;
        }else if (monthMinus == 0) {
            if (dayMinus < 0) {
                age -= 1;
            }
        }
        return age+"";
    }


}
