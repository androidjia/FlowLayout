package com.jjs.zero.chartlibrary.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.jjs.zero.chartlibrary.R;


/**
 * Created by shawpaul on 2019/2/21
 */
public class LineIndicator extends View {

    private int mLineColor;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LineIndicator(Context context) {
        super(context);
    }

    public LineIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int[] lineColor = new int[]{R.attr.chartLineColor};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, lineColor);
        mLineColor = typedArray.getColor(0, Color.WHITE);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        mPaint.setColor(mLineColor);
        canvas.drawLine(0, height * 0.5f, width, height * 0.5f, mPaint);
        canvas.drawCircle(width * 0.5f, height * 0.5f, 4, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(width * 0.5f, height * 0.5f, 3, mPaint);


    }
}
