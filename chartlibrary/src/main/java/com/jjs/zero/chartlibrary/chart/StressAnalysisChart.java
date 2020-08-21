package com.jjs.zero.chartlibrary.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/6/28
 * @Details: <压力分析>
 */
public class StressAnalysisChart extends ManyValueChart{
    private Context mContext;
    private String yTitleUnit = "";
    private boolean isYSpace = false;//y轴标题间隔
    public StressAnalysisChart(Context context) {
        super(context);
    }

    public StressAnalysisChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setYSpace(boolean YSpace) {
        isYSpace = YSpace;
    }

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
            float rangesBaseSpan =  (height - AXIS_TEXT_WIDTH) / yTitles.size();
            for (int i = 0; i < yTitles.size(); i++) {
                drawYScale(canvas,yTitles.get(i),rangesBaseLine,width,i);
                rangesBaseLine -= rangesBaseSpan;
            }
            if (yTitleUnit != null && yTitleUnit.length()>0) {
                drawText(canvas,yTitleUnit,rangesBaseLine+rangesBaseSpan-40);
            }
            dashPath.close();
        }

        float startPointX = AXIS_TEXT_WIDTH * 2;
        float endPointX = width - AXIS_TEXT_WIDTH;
        canvas.drawText("min", (startPointX+endPointX)/2, height - 40, mDatePaint);

        if (mValueAndDatess != null&& mValueAndDatess.length>0) {
            for (int i = 0; i < mValueAndDatess.length; i++) {
                ManyValueRanges range = (ManyValueRanges) ranges[i];
                drawLineAndPoint(canvas, width, height, mValueAndDatess[i], linePaths[i], range.lineColor);
                linePaths[i].close();
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

    protected void drawLineAndPoint(Canvas canvas, int width, int height, List<ValueAndDate> valueAndDates, Path path, int pathColor) {
        if (valueAndDates != null && !valueAndDates.isEmpty()) {
            float startPointX = AXIS_TEXT_WIDTH * 2;
            float pointSpan = width - AXIS_TEXT_WIDTH * 3;
            if (valueAndDates.size() > 1) {
                pointSpan = (width - AXIS_TEXT_WIDTH * 3) / (valueAndDates.size() - 1);
            }
            path.reset();

            mPointPaint.setColor(pathColor);
            mLinePaint.setColor(pathColor);
            int size = valueAndDates.size();
            float startX = 0f;
            float startY = 0f;
            float endX =0f;
            float endY =0f;

            for (int i = 0; i < size; i++) {
                float pointX = startPointX + pointSpan * i;

                ValueAndDate valueAndDate = valueAndDates.get(i);
                float value = valueAndDate.value;
                float pointY = getPointY(height, value);

                if (i+1 < size) {
                    startX = pointX;
                    startY = pointY;
                    endX = startPointX + pointSpan * (i+1);
                    ValueAndDate valueAndDate2 = valueAndDates.get(i+1);
                    float value2 = valueAndDate2.value;
                    endY = getPointY(height, value2);

                    float ptX2 = (startX + endX)/2;
                    float ptY2 = startY;
                    float ptX3 = (startX + endX)/2;;
                    float ptY3 = endY;
                    path.moveTo(startX, startY);
                    path.cubicTo(ptX2,ptY2,ptX3,ptY3,endX,endY);
                    canvas.drawPath(path,mLinePaint);
                } else {
                    if(i==0){
                        canvas.drawCircle(startPointX+pointSpan/2,getPointY(height, valueAndDates.get(0).value),dip2px(mContext,2f),mPointPaint);
                    }
                }

            }

        }
    }

    @Override
    protected float getPointY(int height, float value) {
        float baseLine = (height - AXIS_TEXT_WIDTH) / yTitles.size();
        float max = 0f;
        float min = 0f;
        for (Ranges range : ranges) {
            max = range.maxValue;
            min = range.minValue;
            break;
        }

        if (value <= max) {
            return  baseLine + baseLine*(yTitles.size()-1)*((max-value)/(max-min));//
        }
        return (1- (value-max)/max) * baseLine;
    }

    private int dip2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().density;
        return ( int ) (var1 * var2 + 0.5F);
    }
}
