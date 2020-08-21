package com.jjs.zero.chartlibrary.chart;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
 * 血压
 */
public class ManyRangeManyValueChart extends ManyRangeChart {

    private List<ValueAndDate>[] mValueAndDatess;
    private Path[] linePaths;
    private ValueAndDate[] valueMaxs;
    private ValueAndDate[] valueMins;
    private float max;
    private float min;
    private Comparator<ValueAndDate> mComparator = new ValueComparator();

    private List<RectF> rectMax = new ArrayList<>();
    private List<RectF> rectMin = new ArrayList<>();

    private int index = 0;

    public ManyRangeManyValueChart(Context context) {
        super(context);
    }

    public ManyRangeManyValueChart(Context context, @Nullable AttributeSet attrs) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        //  画xy轴
//        drawXYAXIS(canvas, width, height);

        // 范围
        if (ranges != null) {
            float rangesBaseLine = (height - AXIS_TEXT_WIDTH) / 6;
            float rangesBaseSpan = (height - AXIS_TEXT_WIDTH) * 2 / 3 / ranges.length;

            for (int i = 0; i < ranges.length; i++) {
                Ranges range = ranges[i];
                drawRange(canvas, width, rangesBaseLine, rangesBaseLine + rangesBaseSpan,
                        String.valueOf((int) range.maxValue), i == ranges.length - 1 ? String.valueOf((int) range.minValue) : null, range.rangeDrawable, range.rangeString, range.rangeStringColor);

                mAxisPaint.setColor(range.rangeStringColor);
                mAxisPaint.setStrokeWidth(6f);
                canvas.drawLine(AXIS_TEXT_WIDTH,rangesBaseLine,AXIS_TEXT_WIDTH,rangesBaseLine + rangesBaseSpan,mAxisPaint);
                Log.i("zero","rangesBaseLin===========e :"+rangesBaseLine+"     index:"+i);
                rangesBaseLine += rangesBaseSpan;
            }
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

        if (clickedPointIndex >= 0) {
            drawVerticalLineClickedText(canvas, width, height, mValueAndDatess[0], mClickTextFormatter);
        }

    }


    @Override
    protected float getPointY(int height, float value) {
        float baseLine = (height - AXIS_TEXT_WIDTH) / 6;
        float rangesBaseSpan = (height - AXIS_TEXT_WIDTH) * 2 / 3 / ranges.length;
        for (Ranges range : ranges) {
            if (value >= range.minValue && value <= range.maxValue) {
                return getPointYInner(baseLine, baseLine + rangesBaseSpan, range.maxValue, range.minValue, value);
            }
            baseLine += rangesBaseSpan;
        }
        if (value > ranges[0].maxValue) {
            return getPointYInner((height - AXIS_TEXT_WIDTH) / 12, (height - AXIS_TEXT_WIDTH) / 6, max, ranges[0].maxValue, value);
        }
        return getPointYInner((height - AXIS_TEXT_WIDTH) * 5 / 6, (height - AXIS_TEXT_WIDTH) * 11 / 12, ranges[ranges.length - 1].maxValue, min, value);
    }


    @Override
    protected void drawBubble(Canvas canvas, float value, float pointX, float pointY, boolean isMaxValue, Paint mMaxMinPaint) {
        String valueText = "";
        if (isMaxValue) {
            valueText = "最高"+value;
        } else {
            valueText = "最低"+value;
        }
        float maxValue = ranges[index].maxValue;
        float minValue = ranges[index].minValue;
        Rect rect = getTextBounds(valueText);
        int width = rect.width()/2;
        int height = rect.height();
        Path path = new Path();
        int offset = 12;

        float paddingLeft = 15f;
        if (index==0) {
            mMaxMinPaint.setColor(Color.parseColor("#00C7C1"));
        } else {
            mMaxMinPaint.setColor(Color.parseColor("#56C53B"));
        }

        if (isMaxValue) {
            if (maxValue>value) {
                RectF rect1 = new RectF(pointX-width-paddingLeft,pointY-18-height-20,pointX+width+paddingLeft,pointY-18);
                canvas.drawRoundRect(rect1,16,16,mMaxMinPaint);
                path.moveTo(pointX,pointY-6);
                path.lineTo(pointX-10,pointY-18);
                path.lineTo(pointX+10,pointY-18);
                path.close();
                canvas.drawPath(path,mMaxMinPaint);
                mMaxMinPaint.setColor(Color.WHITE);
                canvas.drawText(valueText, pointX, pointY - (height+20)/2-offset, mMaxMinPaint);
            } else {
//                mMaxMinPaint.setColor(Color.parseColor("#FF6161"));
                canvas.drawRoundRect(pointX-width-paddingLeft,pointY+18,pointX+width+paddingLeft,pointY+18+height+20,16,16,mMaxMinPaint);
                path.moveTo(pointX,pointY+6);
                path.lineTo(pointX-10,pointY+18);
                path.lineTo(pointX+10,pointY+18);
                path.close();
                canvas.drawPath(path,mMaxMinPaint);
                mMaxMinPaint.setColor(Color.WHITE);
                canvas.drawText(valueText, pointX, pointY + 12 +(height+20)/2 + offset, mMaxMinPaint);
            }
        } else {
            if (minValue>value) {
//                mMaxMinPaint.setColor(Color.parseColor("#FFB300"));
                canvas.drawRoundRect(pointX-width-paddingLeft,pointY-18-height-20,pointX+width+paddingLeft,pointY-18,16,16,mMaxMinPaint);
                path.moveTo(pointX,pointY-6);
                path.lineTo(pointX-10,pointY-18);
                path.lineTo(pointX+10,pointY-18);
                path.close();
                canvas.drawPath(path,mMaxMinPaint);
                mMaxMinPaint.setColor(Color.WHITE);
                canvas.drawText(valueText, pointX, pointY - (height+20)/2 - offset, mMaxMinPaint);
            } else {
//                mMaxMinPaint.setColor(Color.parseColor("#B07CE4"));//ECD9FF
                canvas.drawRoundRect(pointX-width-paddingLeft,pointY+18,pointX+width+paddingLeft,pointY+18+height+20,16,16,mMaxMinPaint);
                path.moveTo(pointX,pointY+6);
                path.lineTo(pointX-10,pointY+18);
                path.lineTo(pointX+10,pointY+18);
                path.close();
                canvas.drawPath(path,mMaxMinPaint);
                mMaxMinPaint.setColor(Color.WHITE);
                canvas.drawText(valueText, pointX, pointY + 12 + (height+20)/2 + offset, mMaxMinPaint);
            }
        }
    }

    public static class ManyValueRanges extends Ranges {

        public int lineColor;

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
