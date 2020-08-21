package com.jjs.zero.ecgchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/5/15
 * @Details: <血氧图表>
 */
public class SpoLineChart extends LineChart {
    public SpoLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void drawLine(Canvas canvas) {
        // 40dp right;
        /**
         * 左边第一条竖线
         */
        mPathX.reset();
        mPaintXLine.setColor(Color.parseColor("#FF4600"));
        mPaintXLine.setStrokeWidth(dpToPx(mContext,2));

        mPathX.moveTo(mMarginLeft,getHeight() - mViewMargin);
        mPathX.lineTo(mMarginLeft,(float) (getHeight()-mViewMargin - 0.8*mHeight));
        mPathX.close();
        canvas.drawPath(mPathX,mPaintXLine);

        mPathX.reset();
        mPaintXLine.setColor(Color.parseColor("#FFAA00"));
        mPathX.moveTo(mMarginLeft,(float) (getHeight()-mViewMargin - 0.8*mHeight));
        mPathX.lineTo(mMarginLeft,(float) (getHeight()-mViewMargin - 0.9*mHeight));
        mPathX.close();
        canvas.drawPath(mPathX,mPaintXLine);

        mPathX.reset();
        mPaintXLine.setColor(Color.parseColor("#A0C600"));
        mPathX.moveTo(mMarginLeft,(float) (getHeight()-mViewMargin - 0.9*mHeight));
        mPathX.lineTo(mMarginLeft,(float) (getHeight()-mViewMargin - 0.94*mHeight));
        mPathX.close();
        canvas.drawPath(mPathX,mPaintXLine);

        mPathX.reset();
        mPaintXLine.setColor(Color.parseColor("#00CC29"));
        mPathX.moveTo(mMarginLeft,(float) (getHeight()-mViewMargin - 0.94*mHeight));
        mPathX.lineTo(mMarginLeft,(float) (getHeight()-mViewMargin - mHeight));
        mPathX.close();
        canvas.drawPath(mPathX,mPaintXLine);

        mPaintXLine.setAlpha(20);
        mPaintXLine.setStyle(Paint.Style.FILL);

        mPaintXLine.setColor(Color.parseColor("#00CC29"));
        canvas.drawRect(mMarginLeft,(float) (getHeight()-mViewMargin - 0.94*mHeight),getWidth() - mViewMargin,(float) (getHeight()-mViewMargin - mHeight),mPaintXLine);

        mPaintXLine.setColor(Color.parseColor("#A0C600"));
        canvas.drawRect(mMarginLeft,(float) (getHeight()-mViewMargin - 0.94*mHeight),getWidth() - mViewMargin,(float) (getHeight()-mViewMargin - 0.9*mHeight),mPaintXLine);

        mPaintXLine.setColor(Color.parseColor("#FFAA00"));
        canvas.drawRect(mMarginLeft,(float) (getHeight()-mViewMargin - 0.9*mHeight),getWidth() - mViewMargin,(float) (getHeight()-mViewMargin - 0.8*mHeight),mPaintXLine);

        mPaintXLine.setColor(Color.parseColor("#ffff4600"));
        canvas.drawRect(mMarginLeft,(float) (getHeight()-mViewMargin - 0.8*mHeight),getWidth() - mViewMargin,getHeight() - mViewMargin,mPaintXLine);


    }
}
