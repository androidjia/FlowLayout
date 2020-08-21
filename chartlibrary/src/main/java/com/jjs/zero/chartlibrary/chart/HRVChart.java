package com.jjs.zero.chartlibrary.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/6/28
 * @Details: <心率变异波形图和压力分析波形图>
 */
public class HRVChart extends ManyValueChart{
    private Context mContext;
    private String yTitleUnit = "";
    private boolean isYSpace = true;//y轴标题间隔
    private List<Integer>[] valuess;
    private Integer max = 0;
    private Integer min = 0;
    private float mTop;
    public HRVChart(Context context) {
        super(context);
    }

    public HRVChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mTop = dip2px(mContext,30f);
    }


    public void setValues(boolean isYSpace,List<Integer>... values) {
        this.isYSpace = isYSpace;
        this.valuess = values;
        if (values != null && values.length>0){
            int len = values.length;
            Integer[] maxArray = new Integer[len];
            Integer[] minArray = new Integer[len];
            linePaths = new Path[len];
            for (int i = 0; i < len; i++) {
                maxArray[i] = Collections.max(values[i]);
                minArray[i] = Collections.min(values[i]);
                linePaths[i] = new Path();
            }

            int item = 10;
            if (isYSpace){
                item = 20;
            }

            int position = 1;

            if (maxArray != null && maxArray.length>1) {
                max = Collections.max(Arrays.asList(maxArray));
                min = Collections.min(Arrays.asList(minArray));
                position = (max-min)/item;
                if (min%10 != 0) {
                    min = min/10*10;
                }
                if ((max-min)%item != 0) {
                    position += 1;
                }
                max = min + item*position;
            } else {
                if (minArray != null && minArray.length ==1) {
                    min = minArray[0];
                    max = maxArray[0];
                    if (min==max) {
                        if (min<item) {
                            min = 0;
                            max = item;
                        } else {
                            min = min/10*10;
                            max = min+item;
                            this.isYSpace = false;
                        }
                    } else {
                        position = (max-min)/item;
                        if (min%10 != 0) {
                            min = min/10*10;
                        }
                        if ((max-min)%item != 0) {
                            position += 1;
                        }
                        max = min + item*position;
                    }

                }
            }


            if (yTitles != null) {
                yTitles.clear();
            } else {
                yTitles = new ArrayList<>();
            }

            for (int i=0;i<=position;i++){
                yTitles.add((min+item*i)+"");
            }

        }
        invalidate();
    }

//    public void setYSpace(boolean YSpace) {
//        isYSpace = YSpace;
//    }

    public void setYTitleUnit(String YTitleUnit) {
        this.yTitleUnit = YTitleUnit;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        // 范围
        if (yTitles != null) {
            mAxisPaint.setColor(Color.parseColor("#888888"));
            float rangesBaseLine =  height-AXIS_TEXT_WIDTH;
            float rangesBaseSpan =  (height - AXIS_TEXT_WIDTH-mTop) / (yTitles.size()-1);
            for (int i = 0; i < yTitles.size(); i++) {
                drawYScale(canvas,yTitles.get(i),rangesBaseLine,width,i);
                rangesBaseLine -= rangesBaseSpan;
            }
            if (yTitleUnit != null && yTitleUnit.length()>0) {
                drawText(canvas,yTitleUnit,mTop/2);
            }
            dashPath.close();
        }

        float startPointX = AXIS_TEXT_WIDTH * 2;
        float endPointX = width - AXIS_TEXT_WIDTH;
        canvas.drawText("min", (startPointX+endPointX)/2, height - 40, mDatePaint);

        if (valuess != null&& valuess.length>0) {

            for (int i = 0; i < valuess.length; i++) {
                List<Integer> values = valuess[i];
                HRVRanges range = (HRVRanges) ranges[i];
                if (values != null && values.size()>0) {
                    drawLineAndPoint(canvas, width, height,values, linePaths[i], range.lineColor);
                }
                linePaths[i].close();
            }
        }

//        if (mClickTextFormatter!=null) {
//            if (clickedPointIndex >= 0) {
//                drawVerticalLineClickedText(canvas, width, height, mValueAndDatess[0], mClickTextFormatter);
//            }
//        }
    }
    /**
     * 绘制y轴刻度
     */
    private void drawYScale(Canvas canvas, String yTitle, float rangesBaseLine,float width,int index) {
        if (isYSpace) {
            if (index %2 ==0) {
                drawText(canvas,yTitle,rangesBaseLine);
            }
        } else {
            drawText(canvas,yTitle,rangesBaseLine);
        }

//        if (index != 0) {
            dashPath.reset();
            dashPath.moveTo(AXIS_TEXT_WIDTH,rangesBaseLine);
            dashPath.lineTo(width,rangesBaseLine);
            canvas.drawPath(dashPath,dashPaint);
//        }
    }


    protected void drawText(Canvas canvas, String yTitle, float rangesBaseLine) {
        Rect bounds = new Rect();
        mAxisPaint.getTextBounds(yTitle,0,yTitle.length(),bounds);
        Paint.FontMetrics fontMetrics = mAxisPaint.getFontMetrics();
        float height = fontMetrics.bottom - fontMetrics.top;
        canvas.drawText(yTitle, AXIS_TEXT_WIDTH, rangesBaseLine+height/2 - mAxisPaint.descent(), mAxisPaint);
    }

    protected void drawLineAndPoint(Canvas canvas, int width, int height, List<Integer> values, Path path, int pathColor) {

            float startPointX = AXIS_TEXT_WIDTH * 2;
            float pointSpan = width - AXIS_TEXT_WIDTH * 3;
            int len = values.size();
            if (len > 1) {
                pointSpan = (width - AXIS_TEXT_WIDTH * 3) / (len - 1);
            }
            path.reset();

            mPointPaint.setColor(pathColor);
            mLinePaint.setColor(pathColor);
            float startX = 0f;
            float startY = 0f;
            float endX =0f;
            float endY =0f;

            for (int i = 0; i < len; i++) {
                float pointX = startPointX + pointSpan * i;
                float pointY = getPointY(height, values.get(i));

                if (i+1 < len) {
                    startX = pointX;
                    startY = pointY;
                    endX = startPointX + pointSpan * (i+1);
                    endY = getPointY(height, values.get(i+1));

                    float ptX2 = (startX + endX)/2;
                    float ptY2 = startY;
                    float ptX3 = (startX + endX)/2;;
                    float ptY3 = endY;
                    path.moveTo(startX, startY);
                    path.cubicTo(ptX2,ptY2,ptX3,ptY3,endX,endY);
                    canvas.drawPath(path,mLinePaint);
                } else {
                    if(i==0){
                        canvas.drawCircle(startPointX+pointSpan/2,getPointY(height, values.get(0)),dip2px(mContext,2f),mPointPaint);
                    }
                }

            }
    }

    @Override
    protected float getPointY(int height, float value) {
//        float baseLine = (height - AXIS_TEXT_WIDTH) / yTitles.size();

        float rangesBaseLine =  height-AXIS_TEXT_WIDTH;
        float baseLine = (height - AXIS_TEXT_WIDTH-mTop) / (yTitles.size()-1);

//          return  baseLine + baseLine*(yTitles.size()-1)*(1-value/max);
//        return  rangesBaseLine-baseLine -(value-min)/(max-min)*(rangesBaseLine-baseLine-mTop);
        return  rangesBaseLine -(value-min)/(max-min)*(rangesBaseLine-mTop);


//        if (value <= max) {
//            return  baseLine + baseLine*(yTitles.size()-1)*((max-value)/(max-min));//
//        }
//        return (1- (value-max)/max) * baseLine;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public static class HRVRanges extends Ranges {
        public int lineColor;

        public HRVRanges(String lineColor) {
            this.lineColor = Color.parseColor(lineColor);

        }

    }

    private int dip2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return ( int ) (var1 * var2 + 0.5F);
    }
}
