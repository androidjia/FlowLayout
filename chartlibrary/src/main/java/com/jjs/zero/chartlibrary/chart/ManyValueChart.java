package com.jjs.zero.chartlibrary.chart;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shawpaul on 2019/2/18
 * 风险趋势
 */
public class ManyValueChart extends ManyRangeChart {

    protected List<ValueAndDate>[] mValueAndDatess;
    protected Path[] linePaths;
    private ValueAndDate[] valueMaxs;
    private ValueAndDate[] valueMins;
    private float max;
    private float min;
    private Comparator<ValueAndDate> mComparator = new ValueComparator();



    private int index = 0;

    protected List<String> yTitles;

    public ManyValueChart(Context context) {
        super(context);
    }

    public ManyValueChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setValuesAndDates(List<ValueAndDate>... valueAndDatess) {
        mValueAndDatess = valueAndDatess;
        if (valueAndDatess != null && valueAndDatess.length>0) {
            valueMaxs = new ValueAndDate[valueAndDatess.length];
            for (int i = 0; i < valueMaxs.length; i++) {
                valueMaxs[i] = Collections.max(valueAndDatess[i], mComparator);
            }

            valueMins = new ValueAndDate[valueAndDatess.length];
            for (int i = 0; i < valueMins.length; i++) {
                valueMins[i] = Collections.min(valueAndDatess[i], mComparator);
            }

            max = Collections.max(Arrays.asList(valueMaxs), mComparator).value;
            min = Collections.min(Arrays.asList(valueMins), mComparator).value;


            linePaths = new Path[valueAndDatess.length];
            for (int i = 0; i < linePaths.length; i++) {
                linePaths[i] = new Path();
            }
        }
        invalidate();
    }

    public void setYTitles(List<String> yTitles) {
        this.yTitles = yTitles;
    }

    public void setLeftPadding(float values) {
        setAXIS_TEXT_WIDTH(values);
    }
    @Override
    protected void setAXIS_TEXT_WIDTH(float value) {
        super.setAXIS_TEXT_WIDTH(value);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        //  画xy轴
        drawXYAXIS(canvas, width, height);
        // 范围
        if (yTitles != null) {
//            dashPaint.setTextSize(mXTitleSize);

            mAxisPaint.setColor(Color.parseColor("#888888"));
            float rangesBaseLine =  height-AXIS_TEXT_WIDTH;
            float rangesBaseSpan =  (height - AXIS_TEXT_WIDTH) / yTitles.size();
            for (int i = 0; i < yTitles.size(); i++) {
                drawYScale(canvas,yTitles.get(i),rangesBaseLine,width,i);
                rangesBaseLine -= rangesBaseSpan;
            }
            dashPath.close();
        }

        //日期
        drawStartAndEndDate(canvas, width, height, mValueAndDatess[0]);

        if (mValueAndDatess != null&& mValueAndDatess.length>0) {
            for (int i = 0; i < mValueAndDatess.length; i++) {
                index = i;
                ManyValueRanges range = (ManyValueRanges) ranges[i];
                drawLineAndPoint(canvas, width, height, mValueAndDatess[i], valueMaxs[i], valueMins[i], linePaths[i], range.lineColor);
            }

        }

        if (mClickTextFormatter!=null) {
            if (clickedPointIndex >= 0) {
                drawVerticalLineClickedText(canvas, width, height, mValueAndDatess[0], mClickTextFormatter);
            }
        }


    }

    /**
     * 绘制y轴刻度
     */
    private void drawYScale(Canvas canvas, String yTitle, float rangesBaseLine,float width,int index) {
        Rect bounds = new Rect();
        mAxisPaint.getTextBounds(yTitle,0,yTitle.length(),bounds);
        Paint.FontMetrics fontMetrics = mAxisPaint.getFontMetrics();
        float height = fontMetrics.bottom - fontMetrics.top;
        Log.i("zero","height:=========="+height);
        canvas.drawText(yTitle, AXIS_TEXT_WIDTH, rangesBaseLine+height/2 - mAxisPaint.descent(), mAxisPaint);
        if (index != 0) {
            dashPath.reset();
            dashPath.moveTo(AXIS_TEXT_WIDTH,rangesBaseLine);
            dashPath.lineTo(width,rangesBaseLine);
            canvas.drawPath(dashPath,dashPaint);
        }
    }
    @Override
    protected float getPointY(int height, float value) {
        float baseLine = (height - AXIS_TEXT_WIDTH) / yTitles.size();
        float max = 0f;
        float min = 0f;
        float y = 0f;
        for (Ranges range : ranges) {
            max = range.maxValue;
            min = range.minValue;
            break;
        }

        if (value <= max) {
          return  baseLine + baseLine*(yTitles.size()-1)*(1-value/max);
        }
        return (1- (value-max))/20 * baseLine;

//        float rangesBaseSpan = (height - AXIS_TEXT_WIDTH) * 2 / 3 / ranges.length;
//        float rangesBaseSpan = (height - AXIS_TEXT_WIDTH) / yTitles.size();
//        for (Ranges range : ranges) {
//            if (value >= range.minValue && value <= range.maxValue) {
//                return getPointYInner(baseLine, baseLine + rangesBaseSpan, range.maxValue, range.minValue, value);
//            }
//            baseLine += rangesBaseSpan;
//        }
//        if (value > ranges[0].maxValue) {
//            return getPointYInner((height - AXIS_TEXT_WIDTH) / 12, (height - AXIS_TEXT_WIDTH) / 6, max, ranges[0].maxValue, value);
//        }
//        return getPointYInner((height - AXIS_TEXT_WIDTH) * 5 / 6, (height - AXIS_TEXT_WIDTH) * 11 / 12, ranges[ranges.length - 1].maxValue, min, value);
    }


    @Override
    protected void drawBubble(Canvas canvas, float value, float pointX, float pointY, boolean isMaxValue, Paint mMaxMinPaint) {
    }

    public static class ManyValueRanges extends Ranges {
        public int lineColor;
        public ManyValueRanges(float maxValue, float minValue,String lineColor) {
            super();
            this.maxValue = maxValue;
            this.minValue = minValue;
            this.lineColor = Color.parseColor(lineColor);
        }
        public ManyValueRanges(Context context, float maxValue, float minValue, int rangeDrawable, String rangeString, String rangeStringColor, String lineColor) {
            super(context, maxValue, minValue, rangeDrawable, rangeString, rangeStringColor);
            this.lineColor = Color.parseColor(lineColor);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onTouchEventInner(event, mValueAndDatess[0] == null ? 0 : mValueAndDatess[0].size());
    }

}
