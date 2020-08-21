package com.jjs.zero.ecgchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/5/13
 * @Details: <功能描述>
 */
public class LineChart extends View {
    protected int mViewMargin;//顶部和底部的距离
    protected int linType = 1;//1弧线 2 直线

    protected int mXTitleSize;//x轴字体大小
    protected int mYTitleSize;//y轴字体大小
    protected int mXTitleColor = Color.parseColor("#D8BBA3");
    protected int mYTitleColor = Color.parseColor("#888888");
    protected int mXLineColor = Color.parseColor("#E2E2E2");; //x轴线

    protected List<String> mYList;//y轴
    protected List<String> mXList;

    protected int mShadowColor;//阴影颜色
    protected boolean isShowShadow;//是否显示阴影色
    protected boolean isShowXLineDash;//是否显示X虚线
    protected boolean isShowYLine;//是否显示Y线
    protected Context mContext;
    /**
     * 网格线的高度
     */
    protected int mHeight;
    /**
     * 网格线距离左边的距离
     */
    protected float mMarginLeft;
    protected List<Point> mListPoint;
    protected Paint mPaint;
    protected Paint mPaintXLine;
    protected Path mPathX;
    protected Paint mPaintCycle;


    public LineChart(Context context) {
        this(context,null);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mXLineColor = Color.parseColor("#E2E2E2");
        mXTitleColor = Color.parseColor("#D8BBA3");
        mYTitleColor = Color.parseColor("#888888");
        mShadowColor = Color.BLUE;
        mXTitleSize = 16;
        mYTitleSize = (int)dpToPx(context,16);
        mViewMargin = 60;
        isShowShadow = false;
        isShowXLineDash = true;
        isShowYLine = false;

        mXList = new ArrayList<>();
        mYList = new ArrayList<>();
        mListPoint = new ArrayList<>();

        init();
    }

    private void init() {

        /**
         * 设置画笔的属性
         */
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mXLineColor);
        mPaint.setTextSize(mXTitleSize);
        mPaint.setAntiAlias(true);                    //取消锯齿
        mPaint.setStyle(Paint.Style.STROKE);          //设置画笔为空心
        mPaint.setStrokeWidth(2);


        mMarginLeft = (mViewMargin * 2);              //设置左边的偏移距离

        mPathX = new Path();
        mPaintXLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintXLine.setColor(mXLineColor);
        mPaintXLine.setTextSize(mXTitleSize);
        mPaintXLine.setAntiAlias(true);                    //取消锯齿
        mPaintXLine.setStyle(Paint.Style.STROKE);          //设置画笔为空心
        mPaintXLine.setStrokeWidth(2);

        mPaintCycle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCycle.setColor(mXLineColor);
        mPaintCycle.setAntiAlias(true);                    //取消锯齿
        mPaintCycle.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mHeight == 0) {
            mHeight = getHeight() - mViewMargin * 2;
        }

        drawLine(canvas);//绘制网格
        drawXScale(canvas);//绘制x轴刻度
        drawYScale(canvas);//绘制y轴刻度

        setData();
        if (mListPoint != null && mListPoint.size()>0) {
            drawLineView(canvas);//根据数据绘制折线
        }
        if (isShowShadow) {//是否显示阴影
            drawShadow(canvas);
        }

    }

    public void setData() {
        mListPoint.clear();
        mListPoint.addAll(getPointList());
    }

    public void setScaleData(List<String> xList, List<String> yList) {
        this.mXList = xList;
        this.mYList = yList;
    }



    private List<Point> getPointList() {
        List<Point> mList = new ArrayList<>();
        float width = getWidth() - mMarginLeft;
        float height = getHeight() - mViewMargin * 2;
        for (int i = 0; i < mXList.size(); i++) {
            Point point = new Point();

            if (i >3) {
                point.y = mViewMargin + height * (i-3)/7*2;
            } else {
                point.y = mViewMargin + height * i/7*2;
            }
            point.x = mMarginLeft + width * i/7;

            mList.add(point);
        }
        return mList;
    }


    /**
     * 绘制数据
     * @param canvas
     */
    protected void drawLineView(Canvas canvas) {
        if (linType == 1) {
            drawScrollLine(canvas);
        } else {
            drawStraightLine(canvas);
        }
    }

    /**
     * 画阴影
     */
    protected void drawShadow(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mShadowColor);
        mPathX.reset();
        if (linType == 1) {
            Point pStart = new Point();
            Point pEnd = new Point();
//            Path path = new Path();

            for (int i = 0; i < 5; i++) {
                pStart = mListPoint.get(i);
                pEnd = mListPoint.get(i + 1);
                Point point3 = new Point();
                Point point4 = new Point();
                float wd = (pStart.x + pEnd.x) / 2;
                point3.x = wd;
                point3.y = pStart.y;
                point4.x = wd;
                point4.y = pEnd.y;
                mPathX.moveTo(pStart.x, pStart.y);
                mPathX.cubicTo(point3.x, point3.y, point4.x, point4.y, pEnd.x, pEnd.y);
                mPathX.lineTo(pEnd.x, getHeight() - mViewMargin);
                mPathX.lineTo(pStart.x, getHeight() - mViewMargin);
            }

        } else {
//            Path path = new Path();

            mPathX.moveTo(mListPoint.get(0).x, mListPoint.get(0).y);
            for (int i = 1; i < mXList.size(); i++) {
                mPathX.lineTo(mListPoint.get(i).x, mListPoint.get(i).y);
            }
            /**
             * 链接最后两个点
             */
            int index = mListPoint.size() - 1;
            mPathX.lineTo(mListPoint.get(index).x, getHeight() - mViewMargin);
            mPathX.lineTo(mListPoint.get(0).x, getHeight() - mViewMargin);
//            mPathX.close();
//            canvas.drawPath(mPathX, mPaint);
        }
        mPathX.close();
        canvas.drawPath(mPathX, mPaint);
    }

    /**
     * 绘制网格线
     */
    protected void drawLine(Canvas canvas) {
        /**
         * 左边第一条竖线
         */
        canvas.drawLine(mMarginLeft, mViewMargin, mMarginLeft, getHeight() - mViewMargin, mPaintXLine);
        /**
         * 底部线条
         */
        canvas.drawLine(mMarginLeft, mViewMargin + getHeight() - mViewMargin * 2, getWidth() - mViewMargin,
                mViewMargin + getHeight() - mViewMargin * 2, mPaintXLine);
        /**
         * 5条水平的横线
         */
        if (isShowXLineDash) {
            mPaintXLine.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0));
        }
        int len = mYList.size()-1;
        for (int i = len-1; i >=0; i--) {
            mPathX.reset();
            mPathX.moveTo(mMarginLeft,mViewMargin + i * ((getHeight() - mViewMargin * 2) / len));
            mPathX.lineTo(getWidth() - mViewMargin,mViewMargin + i * ((getHeight() - mViewMargin * 2) / len));
            canvas.drawPath(mPathX,mPaintXLine);

//            canvas.drawLine(mMarginLeft, mViewMargin + i * ((getHeight() - mViewMargin * 2) / len), getWidth() - mViewMargin,
//                    mViewMargin + i * ((getHeight() - mViewMargin * 2) / len), mPaintXLine);
        }
//        if (isShowXLineDash) mPaint.setPathEffect(null);
        mPathX.close();
        /**
         * 右边最后一条竖线
         */
//        canvas.drawLine(getWidth() - mViewMargin, mViewMargin,
//                getWidth() - mViewMargin, mViewMargin + ((getHeight() - mViewMargin * 2)), mPaint);
    }

    /**
     * 绘制y轴刻度
     */
    protected void drawYScale(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mYTitleColor);
        int len  = mYList.size();
        Rect bounds = new Rect();
        for (int i = 0; i < len; i++) {
            String str = mYList.get(i);
            mPaint.getTextBounds(str,0,str.length(),bounds);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float height = fontMetrics.bottom - fontMetrics.top;
            if (i == 0) {
                canvas.drawText(str, mViewMargin,
                        mViewMargin+height/3, mPaint);
            }
            if (i != 0 && i != (len-1)) {
                canvas.drawText(str, mViewMargin,
                        mViewMargin+height/3 + i * ((getHeight() - mViewMargin * 2) / (len-1)), mPaint);
            }
            if (i == (len-1)) {
                canvas.drawText(str, mViewMargin,
                        mViewMargin+height/3 + i * ((getHeight() - mViewMargin * 2) / (len-1)), mPaint);
            }
        }
    }

    /**
     * 绘制x轴刻度
     */
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

    /**
     * 绘制折线图
     */
    protected void drawStraightLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mXTitleColor);
        mPaint.setStrokeWidth(3);
        Path path = new Path();
        path.moveTo(mListPoint.get(0).x, mListPoint.get(0).y);
        for (int i = 1; i < mXList.size(); i++) {
            path.lineTo(mListPoint.get(i).x, mListPoint.get(i).y);
        }
        canvas.drawPath(path, mPaint);
    }
    /**
     * 绘制曲线图
     */
    private void drawScrollLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mXTitleColor);
        mPaint.setStrokeWidth(3);
        Point pStart = new Point();
        Point pEnd = new Point();
        Path path = new Path();
        for (int i = 0; i < mXList.size()-1; i++) {
            pStart = mListPoint.get(i);
            pEnd = mListPoint.get(i + 1);
            Point point3 = new Point();
            Point point4 = new Point();
            float wd = (pStart.x + pEnd.x) / 2;
            point3.x = wd;
            point3.y = pStart.y;
            point4.x = wd;
            point4.y = pEnd.y;
            path.moveTo(pStart.x, pStart.y);
            path.cubicTo(point3.x, point3.y, point4.x, point4.y, pEnd.x, pEnd.y);
            canvas.drawPath(path, mPaint);
            canvas.drawCircle(pStart.x,pStart.y,6,mPaintCycle);//绘制圆点

        }
        //绘制最后一个点
        canvas.drawCircle(mListPoint.get(mListPoint.size()-1).x,mListPoint.get(mListPoint.size()-1).y,6,mPaintCycle);
    }


    /**
     * 根据手机分辨率将px 转为 dp
     */
    protected float dpToPx(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue * scale + 0.5f);
    }

    protected class Point {
        public float x;
        public float y;

        public Point(){}

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
