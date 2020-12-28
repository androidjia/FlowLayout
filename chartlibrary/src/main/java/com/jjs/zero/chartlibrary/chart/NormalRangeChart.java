package com.jjs.zero.chartlibrary.chart;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.jjs.zero.chartlibrary.R;


/**
 * Created by shawpaul on 2019/2/18
 * 血糖
 */
public class NormalRangeChart extends BaseChart {

    private float mMaxRangeValue;
    private float mMinRangeValue;
    private String mNormalString;
    private Drawable mNormalBg;
    private int mNormalStringColor;

    public NormalRangeChart(Context context) {
        super(context);
    }

    public NormalRangeChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NormalRangeChart);
        mMaxRangeValue = typedArray.getFloat(R.styleable.NormalRangeChart_maxValue, 10f);
        mMinRangeValue = typedArray.getFloat(R.styleable.NormalRangeChart_minValue, 5f);
        mNormalString = typedArray.getString(R.styleable.NormalRangeChart_normalString);
        mNormalBg = typedArray.getDrawable(R.styleable.NormalRangeChart_normalBg);
        mNormalStringColor = typedArray.getColor(R.styleable.NormalRangeChart_normalStringColor, Color.GREEN);

        typedArray.recycle();

    }

    public void setNormalValueRange(float maxValue, float minValue, String normalString) {
        mMaxRangeValue = maxValue;
        mMinRangeValue = minValue;
        mNormalString = normalString;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        //  画xy轴
//        drawXYAXIS(canvas, width, height);

        // 正常范围
        drawRange(canvas, width, (height - AXIS_TEXT_WIDTH) / 6, (height - AXIS_TEXT_WIDTH) * 5 / 6, String.valueOf(mMaxRangeValue), String.valueOf(mMinRangeValue),
                mNormalBg, mNormalString, mNormalStringColor);

        mAxisPaint.setColor(mNormalStringColor);
        mAxisPaint.setStrokeWidth(6f);
        canvas.drawLine(AXIS_TEXT_WIDTH,(height - AXIS_TEXT_WIDTH) / 6,AXIS_TEXT_WIDTH,(height - AXIS_TEXT_WIDTH) * 5 / 6,mAxisPaint);

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
    protected float getMaxValues() {
        return mMaxRangeValue;
    }

    @Override
    protected float getMinValues() {
        return mMinRangeValue;
    }

    @Override
    protected float getPointY(int viewHeight, float value) {
        if (value >= mMinRangeValue && value <= mMaxRangeValue) {
            float normalBaseLine = (viewHeight - AXIS_TEXT_WIDTH) / 6;
            return getPointYInner(normalBaseLine, (viewHeight - AXIS_TEXT_WIDTH) * 5 / 6, mMaxRangeValue, mMinRangeValue, value);
        } else if (value < mMinRangeValue) {
            float minBaseLine = (viewHeight - AXIS_TEXT_WIDTH) * 5 / 6;
            return getPointYInner(minBaseLine, (viewHeight - AXIS_TEXT_WIDTH) * 11 / 12, mMinRangeValue, mValueMin == null ? value : mValueMin.value, value);
        } else {
            float maxBaseLine = (viewHeight - AXIS_TEXT_WIDTH) * 1 / 12;
            return getPointYInner(maxBaseLine, (viewHeight - AXIS_TEXT_WIDTH) / 6, mValueMax.value, mMaxRangeValue, value);
        }

    }


}
