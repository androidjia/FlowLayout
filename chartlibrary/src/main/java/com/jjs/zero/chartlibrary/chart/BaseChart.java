package com.jjs.zero.chartlibrary.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jjs.zero.chartlibrary.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shawpaul on 2019/2/20
 */
public abstract class BaseChart extends View {

    protected static final float CLICKABLE_RANGE = 40f;
    protected  float AXIS_TEXT_WIDTH = 80f;
    protected float AXIS_BOTTOM = 40;
    protected static final int AXIS_COLOR = Color.parseColor("#BAB6DB");
    protected static final int MAX_TEXT_COLOR = Color.parseColor("#E45C59");
    protected static final int MIN_TEXT_COLOR = Color.parseColor("#6FAF6F");
    protected Paint mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected float mRangeValueTextHeight;
    protected Paint mDatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected Paint mRangeStringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected float mRangeStringHeight;

    protected Paint mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected Paint mVerticalLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected float mClickedStringHeight;
    protected Paint mClickedBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected Path linePath = new Path();
    protected Path dashPath = new Path();
    protected Paint dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected int mLineColor;

    protected Paint mMaxMinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected List<ValueAndDate> mValueAndDates;

    protected ValueAndDate mValueMax;
    protected ValueAndDate mValueMin;

    protected int clickedPointIndex = -1;
    protected ClickTextFormatter mClickTextFormatter;


    public BaseChart(Context context) {
        super(context);
    }

    public BaseChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        mAxisPaint.setColor(AXIS_COLOR);
        mAxisPaint.setColor(Color.parseColor("#444444"));
        mAxisPaint.setTextSize(16f);
        mAxisPaint.setTextAlign(Paint.Align.RIGHT);
        mRangeValueTextHeight = mAxisPaint.descent() - mAxisPaint.ascent();

        mDatePaint.setColor(AXIS_COLOR);
        mDatePaint.setTextSize(16f);
        mDatePaint.setTextAlign(Paint.Align.CENTER);

        mRangeStringPaint.setTextSize(16f);
        mRangeStringHeight = mRangeStringPaint.descent() - mRangeStringPaint.ascent();

        int[] lineColor = new int[]{R.attr.chartLineColor};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, lineColor);
        mLineColor = typedArray.getColor(0, Color.WHITE);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(2f);
        typedArray.recycle();

        mVerticalLinePaint.setColor(Color.parseColor("#F3F7FF"));
        mVerticalLinePaint.setTextSize(16f);
        mClickedStringHeight = mVerticalLinePaint.descent() - mVerticalLinePaint.ascent();


        dashPaint.setAntiAlias(true);                    //取消锯齿
        dashPaint.setStyle(Paint.Style.STROKE);          //设置画笔为空心
        dashPaint.setStrokeWidth(2);
        dashPaint.setColor(Color.parseColor("#E2E2E2"));//
        dashPaint.setPathEffect(new DashPathEffect(new float[]{8, 8}, 0));

        mClickedBgPaint.setColor(Color.parseColor("#80373143"));

        mMaxMinPaint.setTextAlign(Paint.Align.CENTER);
        mMaxMinPaint.setTextSize(18f);
    }

    public void setValueAndDates(List<ValueAndDate> valueAndDates) {
        mValueAndDates = valueAndDates;
        if (valueAndDates!=null && valueAndDates.size()>0) {
            ValueComparator valueComparator = new ValueComparator();
            mValueMax = Collections.max(valueAndDates, valueComparator);
            mValueMin = Collections.min(valueAndDates, valueComparator);
        }

        invalidate();
    }

    public List<ValueAndDate> getValueAndDates() {
        return mValueAndDates;
    }

    protected void drawXYAXIS(Canvas canvas, int width, int height) {
        canvas.drawLine(AXIS_TEXT_WIDTH, height - AXIS_TEXT_WIDTH, width, height - AXIS_TEXT_WIDTH, mAxisPaint);//x轴
//        canvas.drawLine(AXIS_TEXT_WIDTH, height - AXIS_BOTTOM, AXIS_BOTTOM, 0, mAxisPaint);
    }

    protected void drawRange(Canvas canvas, int width, float startY, float endY, String upValue, String downValue, Drawable rangeBg, String rangeText, int rangeTextColor) {
        mAxisPaint.setColor(Color.parseColor("#444444"));
        canvas.drawText(upValue, AXIS_TEXT_WIDTH - 4, startY + mRangeValueTextHeight / 2 - mAxisPaint.descent(), mAxisPaint);
        if (!TextUtils.isEmpty(downValue)) {
            canvas.drawText(downValue, AXIS_TEXT_WIDTH - 4, endY + mRangeValueTextHeight / 2 - mAxisPaint.descent(), mAxisPaint);
        }

        rangeBg.setBounds((int) AXIS_TEXT_WIDTH, (int) startY, width, (int) endY);
        rangeBg.draw(canvas);

        mRangeStringPaint.setColor(rangeTextColor);
        char[] chars = rangeText.toCharArray();
        float normalStringHeightTotal = chars.length * mRangeStringHeight;
        float start = startY + (endY - startY - normalStringHeightTotal) / 2 + mRangeStringHeight;
        for (char aChar : chars) {
            canvas.drawText(String.valueOf(aChar), AXIS_TEXT_WIDTH - 4*5, start, mRangeStringPaint);
            start += mRangeStringHeight;
        }
    }


    protected void setAXIS_TEXT_WIDTH(float value){
        this.AXIS_TEXT_WIDTH = value;
    }

    protected void setAXIS_BOTTOM(float value) {
        this.AXIS_BOTTOM = value;
    }

    protected void drawLineAndPoint(Canvas canvas, int width, int height) {
        drawLineAndPoint(canvas, width, height, mValueAndDates, mValueMax, mValueMin, linePath, mLineColor);
    }

    protected void drawLineAndPoint(Canvas canvas, int width, int height, List<ValueAndDate> valueAndDates, ValueAndDate valueMax, ValueAndDate valueMin, Path path, int pathColor) {
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
                Log.d("Chart", "mValueAndDates.get(i).value:" + value);
                canvas.drawCircle(pointX, pointY, 4, mPointPaint);
                if (valueAndDate == valueMax) {
                    //最大值

                    //绘制图片框
                    String valueStr = "最高" + valueAndDate.valueString;
                    drawBubble(canvas,value,pointX,pointY,true, mMaxMinPaint);

//                    mMaxMinPaint.setColor(MAX_TEXT_COLOR);
//                    canvas.drawText(valueStr, pointX, pointY - 12, mMaxMinPaint);
                } else if (valueAndDate == valueMin) {

                    drawBubble(canvas,value,pointX,pointY,false,mMaxMinPaint);

                    //最小值
//                    mMaxMinPaint.setColor(MIN_TEXT_COLOR);
//                    canvas.drawText("最低" + valueAndDate.valueString, pointX, pointY + 30 - mMaxMinPaint.descent() - mMaxMinPaint.ascent(), mMaxMinPaint);
                }

//                if (i == 0) {
//                    path.moveTo(startPointX, pointY);
//                    canvas.drawPath(path, mLinePaint);
//                } else {
//                    path.lineTo(pointX, pointY);

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
                    }

//                }
            }


//            mPointPaint.setColor(Color.WHITE);
//            for (int i = 0; i < size; i++) {
//                float value = valueAndDates.get(i).value;
//                canvas.drawCircle(startPointX + pointSpan * i, getPointY(height, value), 3, mPointPaint);
//            }
        }
    }


    /**
     * 绘制气泡
     * @param canvas
     * @param value
     * @param pointX
     * @param pointY
     * @param isMaxValue
     */
    protected void drawBubble(Canvas canvas, float value, float pointX, float pointY, boolean isMaxValue,Paint mMaxMinPaint) {
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
        if (isMaxValue) {
            if (getMaxValues()>value) {
                mMaxMinPaint.setColor(Color.parseColor("#B07CE4"));
                canvas.drawRoundRect(pointX-width-30,pointY-18-height-20,pointX+width+30,pointY-18,16,16,mMaxMinPaint);

                path.moveTo(pointX,pointY-6);
                path.lineTo(pointX-10,pointY-18);
                path.lineTo(pointX+10,pointY-18);
                path.close();
                canvas.drawPath(path,mMaxMinPaint);
                mMaxMinPaint.setColor(Color.WHITE);
                canvas.drawText(valueText, pointX, pointY - (height+20)/2-offset, mMaxMinPaint);
            } else {
                mMaxMinPaint.setColor(Color.parseColor("#FF6161"));
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
            if (getMinValues()>value) {
                mMaxMinPaint.setColor(Color.parseColor("#FFB300"));
                canvas.drawRoundRect(pointX-width-30,pointY-18-height-20,pointX+width+30,pointY-18,16,16,mMaxMinPaint);
                path.moveTo(pointX,pointY-6);
                path.lineTo(pointX-10,pointY-18);
                path.lineTo(pointX+10,pointY-18);
                path.close();
                canvas.drawPath(path,mMaxMinPaint);
                mMaxMinPaint.setColor(Color.WHITE);
                canvas.drawText(valueText, pointX, pointY - (height+20)/2 - offset, mMaxMinPaint);
            } else {
                mMaxMinPaint.setColor(Color.parseColor("#B07CE4"));//ECD9FF
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

    protected float getMaxValues() {
        return mValueMax.value;
    }

    protected float getMinValues() {
        return mValueMin.value;
    }
    /**
     * 获取最大最小值宽高
     * @param text
     * @return
     */
    protected Rect getTextBounds(@NonNull String text) {
        Rect rect = new Rect();
        mMaxMinPaint.getTextBounds(text,0,text.length()-1,rect);
        return rect;
    }

    protected void drawStartAndEndDate(Canvas canvas, int width, int height, List<ValueAndDate> valueAndDates) {
        float startPointX = AXIS_TEXT_WIDTH * 2;
        float endPointX = width - AXIS_TEXT_WIDTH;
        float pointSpan = 0f;
        if (valueAndDates !=null && valueAndDates.size()>1) {
            pointSpan = (width - AXIS_TEXT_WIDTH * 3) / (valueAndDates.size() - 1);
        }
        List<String> yearList = new ArrayList<>();
        if (valueAndDates != null && valueAndDates.size()>0) {
            for (int i = 0; i < valueAndDates.size(); i++) {
                ValueAndDate startValue = valueAndDates.get(i);
                canvas.drawText(startValue.monthDay, startPointX+pointSpan*i, height - 40, mDatePaint);
                String years = startValue.year;
                if (!yearList.contains(years)) {
                    canvas.drawText(years,startPointX+pointSpan*i,height-10,mDatePaint);
                    yearList.add(years);
                } else {
                    dashPath.reset();
                    dashPath.moveTo(startPointX+pointSpan*i-12,height-15);
                    dashPath.lineTo(startPointX+pointSpan*i+12,height-15);
                    canvas.drawPath(dashPath,dashPaint);
                }
                if (i>0) {
                    dashPath.reset();
                    dashPath.moveTo(startPointX+pointSpan*(i-1)+20,height-15);
                    dashPath.lineTo(startPointX+pointSpan*i-20,height-15);
                    canvas.drawPath(dashPath,dashPaint);
                }

            }
//            ValueAndDate startValue = valueAndDates.get(0);
//            canvas.drawText(startValue.dayWithYear, startPointX, height - 40, mDatePaint);

//            if (valueAndDates.size() > 1) {
//                ValueAndDate endValue = valueAndDates.get(valueAndDates.size() - 1);
//                canvas.drawText(endValue.dayWithYear, endPointX, height - 40, mDatePaint);
//            }
        }
    }

    protected void drawVerticalLineClickedText(Canvas canvas, int viewWidth, int viewHeight, List<ValueAndDate> valueAndDates, ClickTextFormatter clickTextFormatter) {
        //上下左右各空10,圆角矩形离点8*8

        if (valueAndDates != null && !valueAndDates.isEmpty()) {
            float startPointX = AXIS_TEXT_WIDTH * 2;
            float pointSpan = viewWidth - AXIS_TEXT_WIDTH * 3;
            if (valueAndDates.size() > 1) {
                pointSpan = (viewWidth - AXIS_TEXT_WIDTH * 3) / (valueAndDates.size() - 1);
            }

            int size = valueAndDates.size();
            for (int i = 0; i < size; i++) {

                if (clickedPointIndex == i) {
                    float pointX = startPointX + pointSpan * i;
                    float pointY = getPointY(viewHeight, valueAndDates.get(i).value);

                    //垂直的白线
                    canvas.drawLine(pointX, 0, pointX, viewHeight - AXIS_TEXT_WIDTH, mVerticalLinePaint);

                    String[] string = clickTextFormatter.getString(clickedPointIndex, size - clickedPointIndex);
                    float rectHeight = string.length * mClickedStringHeight + 24f;
                    float rectWidth = 0f;
                    for (String s : string) {
                        float width = mVerticalLinePaint.measureText(s);
                        if (width > rectWidth) {
                            rectWidth = width;
                        }
                    }
                    rectWidth += 20f;
                    float startX = pointX + 8f;
                    if (startX + rectWidth > viewWidth) {
                        startX = pointX - 8f - rectWidth;
                    }
                    float startY = pointY + 8f;
                    canvas.drawRoundRect(startX, startY, startX + rectWidth, startY + rectHeight, 8f, 8f, mClickedBgPaint);

                    float textStartY = startY + 10f - mVerticalLinePaint.ascent();
                    for (String s : string) {
                        canvas.drawText(s, startX + 10f, textStartY, mVerticalLinePaint);
                        textStartY += mClickedStringHeight;
                    }
                }


            }

        }


    }

    public void clearClickedPoint() {
        clickedPointIndex = -1;
    }

    protected abstract float getPointY(int viewHeight, float value);

    protected float getPointYInner(float startY, float endY, float maxValue, float minValue, float value) {
        return startY + (maxValue - value) * (endY - startY) / (maxValue - minValue);
    }

    protected static class ValueComparator implements Comparator<ValueAndDate> {

        @Override
        public int compare(ValueAndDate o1, ValueAndDate o2) {
            if (o1.value > o2.value) {
                return 1;
            } else if (o1.value == o2.value) {
                return 0;
            }
            return -1;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onTouchEventInner(event, mValueAndDates == null ? 0 : mValueAndDates.size());
    }

    protected boolean onTouchEventInner(MotionEvent event, int valueSize) {
        if (valueSize <= 0) {
            return false;
        }
        final float x = event.getX();
        final float y = event.getY();
        int action = event.getAction();
        int width = getWidth();
        int height = getHeight();
        float pointSpan = width - AXIS_TEXT_WIDTH * 3;
        if (valueSize > 1) {
            pointSpan = (width - AXIS_TEXT_WIDTH * 3) / (valueSize - 1);
        }
        if (pointSpan <= CLICKABLE_RANGE) {
            //太密集就不处理了
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (x > AXIS_TEXT_WIDTH && y < height - AXIS_TEXT_WIDTH) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                float startPointX = AXIS_TEXT_WIDTH * 2;
                for (int i = 0; i < valueSize; i++) {
                    float pointX = startPointX + pointSpan * i;
                    if (x < pointX + CLICKABLE_RANGE / 2 && x > pointX - CLICKABLE_RANGE / 2) {
                        clickedPointIndex = i;
                        postInvalidate();
                    }


                }
                break;
        }
        return false;
    }

    public void setClickTextFormatter(ClickTextFormatter clickTextFormatter) {
        mClickTextFormatter = clickTextFormatter;
    }

    public interface ClickTextFormatter {
        String[] getString(int index, int lastIndex);
    }
}
