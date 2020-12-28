package com.jjs.zero.chartlibrary.chart;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.TextUtils;
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
 * bpm 心率
 */
public class ManyValueBpmChart extends ManyRangeChart {
    private float mTop;
    private Context mContext;
    private List<ValueAndDate>[] mValueAndDatess;
    private Path[] linePaths;
    private ValueAndDate[] valueMaxs;
    private ValueAndDate[] valueMins;
    private Integer max = 0;
    private Integer min = 0;
    private Comparator<ValueAndDate> mComparator = new ValueComparator();
    private Integer[] values;

    private String yUnit;
    private int index = 0;

    private List<String> yTitles = new ArrayList<>();
    private List<String> xTitles;
    private List<String> defaultYTitles = new ArrayList<>();
    public ManyValueBpmChart(Context context) {
        super(context);
    }

    public ManyValueBpmChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mTop = dip2px(mContext,30f);

        mLinePaint.setTextSize(sp2px(mContext,14f));
        mLinePaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setValues(Integer... values) {
        this.values = values;
        yTitles.clear();
        if (values != null && values.length>0) {
            max = Collections.max(Arrays.asList(values));
            min = Collections.min(Arrays.asList(values));
            linePaths = new Path[values.length];
            for (int i = 0; i < linePaths.length; i++) {
                linePaths[i] = new Path();
            }
            if (max > 0) {
                if (max%10 != 0) {
                    max = (max/10+1)*10;
                }
                if (min%10 != 0) {
                    min = min/10*10;
                }
                yTitles.add("0");
                yTitles.add(min+"");
                yTitles.add((min+max)/2+"");
                yTitles.add(max+"");
            } else {
                yTitles.addAll(defaultYTitles);
            }

        } else {
            yTitles.addAll(defaultYTitles);
        }
        invalidate();
    }

    public void setYTitles(List<String> yTitles) {
        this.yTitles = yTitles;
        if (this.defaultYTitles.size()!=0) this.defaultYTitles.clear();
        this.defaultYTitles.addAll(yTitles);
    }
    public void setXTitles(List<String> xTitles) {
        this.xTitles = xTitles;
    }

    public void setYUnit(String yUnit) {
        this.yUnit = yUnit;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        //  画xy轴
//        drawXYAXIS(canvas, width, height);

        // 范围
        if (yTitles != null) {
            mAxisPaint.setTextSize(sp2px(mContext,10f));
            mAxisPaint.setColor(Color.parseColor("#A19C8B"));
            float rangesBaseLine =  height-AXIS_TEXT_WIDTH;
            float rangesBaseSpan =  (height - AXIS_TEXT_WIDTH-mTop) / (yTitles.size()-1);

            for (int i = 0; i < yTitles.size(); i++) {
                if (i!=0) {
                    drawYScale(canvas,yTitles.get(i),rangesBaseLine,width,i);
                }
                rangesBaseLine -= rangesBaseSpan;
            }
            if (!TextUtils.isEmpty(yUnit)) {
                Rect bounds = new Rect();
                mAxisPaint.getTextBounds(yUnit,0,yUnit.length(),bounds);
                Paint.FontMetrics fontMetrics = mAxisPaint.getFontMetrics();
                float heights = fontMetrics.bottom - fontMetrics.top;
                canvas.drawText(yUnit, AXIS_TEXT_WIDTH, mTop/2+heights - mAxisPaint.descent(), mAxisPaint);
            }
        }

        //x轴
        mDatePaint.setTextSize(sp2px(mContext,11f));
        mDatePaint.setTextAlign(Paint.Align.CENTER);
        if (xTitles != null && xTitles.size()>0) {
            float startPointX = AXIS_TEXT_WIDTH;
//            float endPointX = width - AXIS_TEXT_WIDTH;
            float pointSpan = 0f;
            if (xTitles !=null && xTitles.size()>1) {
                pointSpan = (width - AXIS_TEXT_WIDTH) / (xTitles.size());
            }
            mDatePaint.setColor(Color.parseColor("#444444"));
            for (int i = 0; i < xTitles.size(); i++) {
                    canvas.drawText(xTitles.get(i), startPointX+pointSpan*i+pointSpan/2, height - 40, mDatePaint);
            }
        }

        mLinePaint.setStyle(Paint.Style.FILL);
        mMaxMinPaint.setTextSize(sp2px(mContext,12f));
        if (values != null&& values.length>0) {
            float startPointX = AXIS_TEXT_WIDTH;
            float pointSpan = (width - AXIS_TEXT_WIDTH)/(xTitles.size());
            for (int i = 0; i < values.length; i++) {
                index = i;
                if (i<ranges.length) {
                    ManyValueRanges range = (ManyValueRanges) ranges[i];
                    if (values[i]>0) {
                        drawLineAndPoint(canvas, height,startPointX,pointSpan,values[i],linePaths[i],range);
                    } else {
                        mLinePaint.setShader(null);
                        drawEmpty(canvas,startPointX + pointSpan * i + 3*i,startPointX+pointSpan*i+pointSpan+3*i,height-AXIS_TEXT_WIDTH,range.lineColor);
                    }
                }
                linePaths[i].close();
            }
        } else {
            if (ranges != null && ranges.length>0) {
                float pointSpan = (width - AXIS_TEXT_WIDTH)/(xTitles.size());

                for (int i = 0; i < ranges.length; i++) {
                    ManyValueRanges range = (ManyValueRanges) ranges[i];
                    float st = AXIS_TEXT_WIDTH + pointSpan * i + 3*i;
                    drawEmpty(canvas,st,st+pointSpan,height-AXIS_TEXT_WIDTH,range.lineColor);
                }
            }

        }

        if (mClickTextFormatter!=null) {
            if (clickedPointIndex >= 0) {
                drawVerticalLineClickedText(canvas, width, height, mValueAndDatess[0], mClickTextFormatter);
            }
        }
    }

    /**
     * 空数据绘制
     * @param canvas
     * @param st
     * @param et
     */
    private void drawEmpty(Canvas canvas,float st, float et, float top,int linColor) {
        mLinePaint.setColor(linColor);
        canvas.drawRect(st,top-dip2px(mContext,4f),et,top-dip2px(mContext,2f),mLinePaint);
        canvas.drawText("-- --", (st+et)/2, top-dip2px(mContext,7f), mLinePaint);
    }

    protected void drawLineAndPoint(Canvas canvas, int height,float startPointX, float pointSpan, Integer value, Path path, ManyValueRanges ranges){
//            float startPointX = AXIS_TEXT_WIDTH * 2;
//            float pointSpan = width - AXIS_TEXT_WIDTH * 3;
//            if (values.length > 1) {
//                pointSpan = (width - AXIS_TEXT_WIDTH * 3)/(values.length);
//            }
        if (value != null) {
            path.reset();
//            mLinePaint.setColor(ranges.lineColor);
//            if (valueAndDates != null && valueAndDates.size()>=1) {
                float startX = startPointX + pointSpan * index + 3*index;
                float startY = height-AXIS_TEXT_WIDTH;
                float endX =startX+pointSpan;
                float endY =startY;

                float pointX = startX+pointSpan/2;
                float pointY = getPointY(height, value);

                float ptX2 = (startX + pointX)/2;
                float ptY2 = startY;
                float ptX3 = (startX + pointX)/2;
                float ptY3 = pointY;

                float ptX4 = (endX + pointX)/2;
                float ptY4 = pointY;
                float ptX5 = (endX + pointX)/2;
                float ptY5 = endY;

                LinearGradient linearGradient = new LinearGradient(startX,startY,pointX,pointY,ranges.endColor,ranges.startColor, Shader.TileMode.MIRROR);
                mLinePaint.setShader(linearGradient);

                path.moveTo(startX, startY);
                path.cubicTo(ptX2,ptY2,ptX3,ptY3,pointX,pointY);
                path.cubicTo(ptX4,ptY4,ptX5,ptY5,endX,endY);
//                path.lineTo(endX, endY);
//                path.lineTo(startX, startY);
                canvas.drawPath(path,mLinePaint);

                mMaxMinPaint.setColor(ranges.lineColor);
                canvas.drawText(value+"", pointX, pointY - 12, mMaxMinPaint);
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
//        Log.i("zero","height:=========="+height);
        canvas.drawText(yTitle, AXIS_TEXT_WIDTH, rangesBaseLine+height/2 - mAxisPaint.descent(), mAxisPaint);
        //绘制y轴虚线
//        if (index != 0) {
//            dashPath.reset();
//            dashPath.moveTo(AXIS_TEXT_WIDTH,rangesBaseLine);
//            dashPath.lineTo(width,rangesBaseLine);
//            canvas.drawPath(dashPath,dashPaint);
//        }
    }

    @Override
    protected float getPointY(int height, float value) {
        float rangesBaseLine =  height-AXIS_TEXT_WIDTH;
        float baseLine = (height - AXIS_TEXT_WIDTH-mTop) / (yTitles.size()-1);

//          return  baseLine + baseLine*(yTitles.size()-1)*(1-value/max);
          return  rangesBaseLine-baseLine -(value-min)/(max-min)*(rangesBaseLine-baseLine-mTop);
//        return (1- (value-max))/20 * baseLine;
    }

    @Override
    protected void drawBubble(Canvas canvas, float value, float pointX, float pointY, boolean isMaxValue, Paint mMaxMinPaint) {
    }

    public static class ManyValueRanges extends Ranges {
        public int lineColor;
        public int startColor;
        public int endColor;
        public float value;

        public ManyValueRanges(String lineColor,String startColor,String endColor) {
            this.lineColor = Color.parseColor(lineColor);
            this.startColor = Color.parseColor(startColor);
            this.endColor = Color.parseColor(endColor);
        }

        public ManyValueRanges(Context context, float maxValue, float minValue, int rangeDrawable, String rangeString, String rangeStringColor, String lineColor) {
            super(context, maxValue, minValue, rangeDrawable, rangeString, rangeStringColor);
            this.lineColor = Color.parseColor(lineColor);
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return onTouchEventInner(event, mValueAndDatess[0] == null ? 0 : mValueAndDatess[0].size());
        return false;
    }


    private int sp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return ( int ) (var1 * var2 + 0.5F);
    }

    private int dip2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return ( int ) (var1 * var2 + 0.5F);
    }
}
