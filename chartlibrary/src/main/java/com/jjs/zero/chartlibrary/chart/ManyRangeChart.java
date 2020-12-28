package com.jjs.zero.chartlibrary.chart;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * Created by shawpaul on 2019/2/18
 * 血氧
 */
public class ManyRangeChart extends BaseChart {

    protected Ranges[] ranges;

    public ManyRangeChart(Context context) {
        super(context);
    }

    public ManyRangeChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRange(Ranges... ranges) {
        this.ranges = ranges;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        //  画xy轴
//        drawXYAXIS(canvas, width, height);

        // 范围
        if (ranges != null) {
            float rangesBaseLine = (height - AXIS_TEXT_WIDTH) / 6;
            float rangesBaseSpan = (height - AXIS_TEXT_WIDTH) * 5 / 6 / ranges.length;
            for (Ranges range : ranges) {
                drawRange(canvas, width, rangesBaseLine, rangesBaseLine + rangesBaseSpan,
                        String.valueOf((int) range.maxValue), null, range.rangeDrawable, range.rangeString, range.rangeStringColor);

                mAxisPaint.setColor(range.rangeStringColor);
                mAxisPaint.setStrokeWidth(6f);
                canvas.drawLine(AXIS_TEXT_WIDTH,rangesBaseLine,AXIS_TEXT_WIDTH,rangesBaseLine + rangesBaseSpan,mAxisPaint);

                rangesBaseLine += rangesBaseSpan;
            }
        }

        //日期
        drawStartAndEndDate(canvas, width, height, mValueAndDates);

        //画点和线
        drawLineAndPoint(canvas, width, height);

        if (mClickTextFormatter != null) {
            if (clickedPointIndex >= 0) {
                drawVerticalLineClickedText(canvas, width, height, mValueAndDates, mClickTextFormatter);
            }
        }


    }


    @Override
    protected float getPointY(int height, float value) {
        float baseLine = (height - AXIS_TEXT_WIDTH) / 6;
        float rangesBaseSpan = (height - AXIS_TEXT_WIDTH) * 5 / 6 / ranges.length;
        for (Ranges range : ranges) {
            if (value >= range.minValue && value <= range.maxValue) {
                return getPointYInner(baseLine, baseLine + rangesBaseSpan, range.maxValue, range.minValue, value);
                //                return baseLine + (range.maxValue - value) / (range.maxValue - range.minValue) * rangesBaseSpan;
            }
            baseLine += rangesBaseSpan;
        }
        return baseLine;
    }


    @Override
    protected void drawBubble(Canvas canvas, float value, float pointX, float pointY, boolean isMaxValue, Paint mMaxMinPaint) {
        String valueText = "";
        if (isMaxValue) {
            valueText = "最高"+value;
        } else {
            valueText = "最低"+value;
        }

        Rect rect = getTextBounds(valueText);
        int width = rect.width()/2;
        int height = rect.height();
        Path path = new Path();
        int offset = 12;
        String valueColor = getColorByValue(value);
        mMaxMinPaint.setColor(Color.parseColor(valueColor));
        if (isMaxValue) {
            if (100>value) {
                canvas.drawRoundRect(pointX-width-30,pointY-18-height-20,pointX+width+30,pointY-18,16,16,mMaxMinPaint);
                path.moveTo(pointX,pointY-6);
                path.lineTo(pointX-10,pointY-18);
                path.lineTo(pointX+10,pointY-18);
                path.close();
                canvas.drawPath(path,mMaxMinPaint);
                mMaxMinPaint.setColor(Color.WHITE);
                canvas.drawText(valueText, pointX, pointY - (height+20)/2-offset, mMaxMinPaint);
            } else {
//                mMaxMinPaint.setColor(Color.parseColor("#FF6161"));
                canvas.drawRoundRect(pointX-width-30,pointY+18,pointX+width+30,pointY+18+height+20,16,16,mMaxMinPaint);
                path.moveTo(pointX,pointY+6);
                path.lineTo(pointX-10,pointY+18);
                path.lineTo(pointX+10,pointY+18);
                path.close();
                canvas.drawPath(path,mMaxMinPaint);
                mMaxMinPaint.setColor(Color.WHITE);
                canvas.drawText(valueText, pointX, pointY + 12 +(height+20)/2 + offset, mMaxMinPaint);
            }
        } else {
            if (80>value) {
//                mMaxMinPaint.setColor(Color.parseColor("#FFB300"));
                canvas.drawRoundRect(pointX-width-30,pointY-18-height-20,pointX+width+30,pointY-18,16,16,mMaxMinPaint);
                path.moveTo(pointX,pointY-6);
                path.lineTo(pointX-10,pointY-18);
                path.lineTo(pointX+10,pointY-18);
                path.close();
                canvas.drawPath(path,mMaxMinPaint);
                mMaxMinPaint.setColor(Color.WHITE);
                canvas.drawText(valueText, pointX, pointY - (height+20)/2 - offset, mMaxMinPaint);
            } else {
//                mMaxMinPaint.setColor(Color.parseColor("#B07CE4"));//ECD9FF
                canvas.drawRoundRect(pointX-width-30,pointY+18,pointX+width+30,pointY+18+height+20,16,16,mMaxMinPaint);
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

    private String getColorByValue (float value) {
        if (value>94) {
           return  "#00CC29";
        }
        if (value >90) {
            return "#A0C600";
        }

        if (value >80) {
            return "#FFAA00";
        }
        return "#FF4600";
    }

    public static class Ranges {
        public float maxValue;
        public float minValue;
        public Drawable rangeDrawable;
        public String rangeString;//纵向标题
        public int rangeStringColor;

        public Ranges(){}

        public Ranges(Context context, float maxValue, float minValue, int rangeDrawable, String rangeString, String rangeStringColor) {
            this.maxValue = maxValue;
            this.minValue = minValue;
            this.rangeDrawable = context.getResources().getDrawable(rangeDrawable);
            this.rangeString = rangeString;
            this.rangeStringColor = Color.parseColor(rangeStringColor);
        }
    }
}
