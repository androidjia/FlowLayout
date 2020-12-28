package com.jjs.zero.chartlibrary.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/6/29
 * @Details: <散点图>
 */
public class ScatterDiagramChart extends View {
    private List<String> mYList = new ArrayList<>();//y轴
    private List<String> mXList= new ArrayList<>();
    private String mYTitleUnit = "RRn＋1(ms)";//单位
    private String mXTitleUnit = "RRn(ms)";//单位

    private float mMarginLeft = 60f;
    private float mMarginTop = 40f;
    private float mMarginBottom = 60f;
    private float mMarginRight = 40f;

    private Paint mPaintText;
    private Paint mPaintLine;
    private Paint mPaintCycle;
    private Context mContext;

    private int width;
    private int height;

    private ScatterDiagram[] scatterDiagramArray;
    private Integer[] values;
    private Integer max;
    private Integer min;

    public ScatterDiagramChart(Context context) {
        this(context,null);
    }

    public ScatterDiagramChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScatterDiagramChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        /**
         * 设置画笔的属性
         */

        mMarginLeft = dip2px(mContext,70f);
        mMarginTop = dip2px(mContext,40f);
        mMarginBottom = dip2px(mContext,60f);
        mMarginRight = dip2px(mContext,40f);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.parseColor("#999999"));
        mPaintText.setTextSize(sp2px(mContext,11f));
        mPaintText.setAntiAlias(true);                    //取消锯齿
        mPaintText.setStyle(Paint.Style.FILL);          //设置画笔为空心

        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setColor(Color.parseColor("#BEBEBE"));
        mPaintLine.setAntiAlias(true);                    //取消锯齿
        mPaintLine.setStyle(Paint.Style.FILL);
        mPaintLine.setStrokeWidth(dip2px(mContext,1.5f));

        mPaintCycle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCycle.setColor(Color.parseColor("#BEBEBE"));
        mPaintCycle.setAntiAlias(true);                    //取消锯齿
        mPaintCycle.setStyle(Paint.Style.FILL);

    }


    public void setValue(Integer... values) {
        this.values = values;
        mXList.clear();
        mYList.clear();
        if (values != null && values.length>1) {
            max = Collections.max(Arrays.asList(values));
            min = Collections.min(Arrays.asList(values));
            min = 0;
            int t = (max-min)/400;
            int p = (max-min)%400;
            if (p>0) t = t+1;
            max = min+t*400;
            for (int i=0;i<=t;i++) {
                int value = min+i*400;
                mXList.add(value+"");
            }
            mYList.addAll(mXList);
        }else {
            mXList.add("0");
            mXList.add("400");
            mXList.add("800");
            mXList.add("1200");
            mYList.addAll(mXList);
        }
        invalidate();
    }
//
//    public void setXYList(List<String> mXYList) {
//        this.mXList = mXYList;
//        this.mYList = mXYList;
//    }

    public void setYTitleUnit(String yTitleUnit) {
        this.mYTitleUnit = yTitleUnit;
    }

    public void setXTitleUnit(String xTitleUnit) {
        this.mXTitleUnit = xTitleUnit;
    }

    public void setMarginLeft(float mMarginLeft) {
        this.mMarginLeft = dip2px(mContext,mMarginLeft);
    }

    public void setMarginTop(float mMarginTop) {
        this.mMarginTop = dip2px(mContext,mMarginTop);
    }

    public void setMarginBottom(float mMarginBottom) {
        this.mMarginBottom = dip2px(mContext,mMarginBottom);
    }

    public void setMarginRight(float mMarginRight) {
        this.mMarginRight = dip2px(mContext,mMarginRight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();

        drawXYLine(canvas);
        mPaintLine.setStrokeWidth(dip2px(mContext,1f));
        if (mXList != null && mXList.size()>0 && mYList != null && mYList.size()>0) {
            float spanX = (width-mMarginLeft-mMarginRight)/(mXList.size()-1);
            float spanY = (height-mMarginBottom-mMarginTop)/(mYList.size()-1);
            mPaintLine.setColor(Color.parseColor("#EFEFEF"));
            drawLine(canvas,spanX,spanY);
            drawXYText(canvas,spanX,spanY);
        }
        //绘制点
        if (values!=null && values.length>1) {
            int len = values.length;
            for (int i = 0; i < len; i++) {
                float pointX = getPointX(values[i]);
                if (i+1 < len){
                    float pointY = getPointY(values[i+1]);
                    canvas.drawCircle(pointX,pointY,dip2px(mContext,4),mPaintCycle);
                }
            }
        }
    }


    private float getPointY(Integer value) {
        float y = max/(height-mMarginTop-mMarginBottom)*value+mMarginTop;
        return y;
    }

    private float getPointX(Integer value) {
        float x = max/(width-mMarginLeft-mMarginRight)*value+mMarginLeft;
        return x;
    }

    private void drawXYText(Canvas canvas,float spanX,float spanY) {
        int len = mXList.size();
        float baseLines = height-mMarginBottom;
        mPaintText.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < len; i++) {
            String title = mXList.get(i);
            canvas.drawText(title, mMarginLeft+spanX*i, baseLines+dip2px(mContext,15), mPaintText);
        }
        if (!TextUtils.isEmpty(mXTitleUnit)) {
            float centX = (width-mMarginLeft-mMarginRight)/2+ mMarginLeft;
            canvas.drawText(mXTitleUnit, centX, height - mMarginBottom/2+dip2px(mContext,10), mPaintText);
        }

        int lenY = mYList.size();
        mPaintText.setTextAlign(Paint.Align.RIGHT);
        float x = mMarginLeft-dip2px(mContext,5f);
        float heights = 0f;
        for (int i = 0; i < lenY; i++) {
            String titleY = mYList.get(i);
            Rect bounds = new Rect();
            mPaintText.getTextBounds(titleY,0,titleY.length(),bounds);
            Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
            heights = fontMetrics.bottom - fontMetrics.top;
            canvas.drawText(titleY, x, baseLines+heights/2 - mPaintText.descent(), mPaintText);
            baseLines -= spanY;
        }
        if (!TextUtils.isEmpty(mYTitleUnit)) {
            canvas.drawText(mYTitleUnit,x,mMarginTop/2+heights/2 - mPaintText.descent(),mPaintText);
        }

    }

    /**
     * 绘制网格
     * @param canvas
     * @param spanX
     * @param spanY
     */
    private void drawLine(Canvas canvas,float spanX,float spanY) {
        int lenX = mXList.size();
        for (int i = 1; i < lenX; i++) {
            float y = height-mMarginBottom - i*spanY;
            canvas.drawLine(mMarginLeft,y,width-mMarginRight,y,mPaintLine);//x轴
        }
        int lenY = mYList.size();
        for (int i = 1; i < lenY; i++) {
            float x = mMarginLeft+i*spanX;
            canvas.drawLine(x,height-mMarginBottom,x,mMarginTop,mPaintLine);//y轴
        }
    }


    /**
     * 绘制xy轴
     * @param canvas
     */
    private void drawXYLine(Canvas canvas) {
        canvas.drawLine(mMarginLeft,height-mMarginBottom,width-mMarginRight,height-mMarginBottom,mPaintLine);//x轴
        canvas.drawLine(mMarginLeft,height-mMarginBottom,mMarginLeft,mMarginTop,mPaintLine);//y轴
    }



    private int sp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return ( int ) (var1 * var2 + 0.5F);
    }

    //    private int dp2px(float value) {
//        final float scale = Resources.getSystem().getDisplayMetrics().densityDpi;
//        return ( int ) (value * (scale / 160) + 0.5f);
//    }
    private int dip2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return ( int ) (var1 * var2 + 0.5F);
    }


    public static class ScatterDiagram {
        public int color;
        public List<Point> pointList;

        public ScatterDiagram(List<Point> pointList,int color) {
            this.pointList = pointList;
            this.color = color;
        }

        public static class Point{
            public float x;
            public float y;

            public Point(float x, float y) {
                this.x = x;
                this.y = y;
            }
        }
    }
}
