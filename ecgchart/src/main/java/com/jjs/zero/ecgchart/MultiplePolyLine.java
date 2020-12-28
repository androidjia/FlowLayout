package com.jjs.zero.ecgchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/5/14
 * @Details: <功能描述>
 */
public class MultiplePolyLine extends LineChart {
    public MultiplePolyLine(Context context) {
        this(context,null);
    }

    public MultiplePolyLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MultiplePolyLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void drawXScale(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mXTitleColor);
        int len = mXList.size();
        for (int i = 0; i < len; i++) {
            if (i == 0) {
                canvas.drawText(mXList.get(i), mMarginLeft - dpToPx(getContext(), 6),
                        getHeight() - mViewMargin + dpToPx(getContext(), 10), mPaint);
            }
            if (i != 0 && i != len) {
                canvas.drawText(mXList.get(i), mMarginLeft + i * ((getWidth()-mMarginLeft) / len),
                        getHeight() - mViewMargin + dpToPx(getContext(), 10), mPaint);
            }
            if (i == len) {
                canvas.drawText(mXList.get(i), mMarginLeft + i * ((getWidth()-mMarginLeft) / len),
                        getHeight() - mViewMargin + dpToPx(getContext(), 10), mPaint);
            }
        }
    }
}
