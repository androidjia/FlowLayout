package com.jjs.zero.chartlibrary.chart;


import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by shawpaul on 2019/2/19
 */
public class ValueAndDate {

    public float value;
    public String valueString;
    public String date;
    public String monthDay;
    public String year;

    public ValueAndDate(float value, Date date) {
        this(value, String.valueOf(value), date);
    }

    public ValueAndDate(float value, String valueString, Date date) {
        this.valueString = valueString;
        this.value = value;
        this.date = new DateTime(date.getTime()).toString("yyyy-MM-dd HH:mm");
        this.year = new DateTime(date.getTime()).toString("yyyy");
        this.monthDay = new DateTime(date.getTime()).toString("MM-dd");
    }
}
