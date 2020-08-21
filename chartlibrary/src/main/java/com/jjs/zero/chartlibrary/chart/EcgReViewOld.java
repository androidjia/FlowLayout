package com.jjs.zero.chartlibrary.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/6/29
 * @Details: <心电图回看>
 */
public class EcgReViewOld extends View {
    private static final String STRPHOUR = "HH:mm";

    private float mMarginLeft = 0f;
    private float mMarginTop = 0f;
    private float mMarginBottom = 0f;
    private float mMarginRight = 0f;

    private Paint mPaintText;
    private Paint mPaintLine;
    private Paint mPaintShadow;
    private Paint mPaintStatus;
    private Paint mPaintPress;
    private Paint mPaintBubble;
    private Context mContext;
    private Path mPath;
    private Path mPathBg;

    private int width;
    private int height;

    private Long startTime;
    private Long endTime;


    private List<Integer> statusList;//状态数据
    private List<ReViewBean> heartRateList;//心率数据
    private List<ReViewBean> tempList;//温度数据
    private List<String> statusColor = new ArrayList<>();//状态颜色
    private List<String> statusStatus = new ArrayList<>();//状态

    private float heartRateHeight;//心率高度
    private float statusHeight;//状态高度
    private float tempHeight;//温度高度
//    private float tempTop;
//    private float heartRateTop;
    private float tempMax;
    private float tempMin;
    private float heartRateMax;
    private float heartRateMin;


    private float mTouchX = 0f;
    private float mTouchEndX = 0f;
    private float mTouchY = 0f;
    private float mDiffX = 0f;

    //ecg数据
    private short[] currentData;//心电数据
//    private short[] lastData;
//    private short[] nextData;
//    private float baseNumber;//基数
    private float baseCenter;//基线
    private int totalNum;//心电数据总长度
//    private int duration;//时长

    private float lastPressLine = 0l;//上次进度位置
    private int lastDataPosition = 0;//上次数据位置

    private int baseOneScreenCount;//一屏点数


    public EcgReViewOld(Context context) {
        this(context,null);
    }

    public EcgReViewOld(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EcgReViewOld(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        /**
         * 设置画笔的属性
         */

        mMarginLeft = dip2px(mContext,30f);
//        mMarginTop = dip2px(mContext,40f);
        mMarginBottom = dip2px(mContext,17f);
        mMarginRight = dip2px(mContext,10f);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setAntiAlias(true);                    //取消锯齿
        mPaintText.setColor(Color.parseColor("#A19C8B"));
        mPaintText.setTextSize(sp2px(mContext,9f));
        mPaintText.setStyle(Paint.Style.FILL);          //设置画笔为空心

        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setAntiAlias(true);                    //取消锯齿
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(dip2px(mContext,1f));

        mPaintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintShadow.setColor(Color.parseColor("#BEBEBE"));
        mPaintShadow.setAntiAlias(true);                    //取消锯齿
        mPaintShadow.setStyle(Paint.Style.FILL);
//        mPaintShadow.setStrokeWidth(dip2px(mContext,1.5f));

        mPaintStatus = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStatus.setAntiAlias(true);                    //取消锯齿
        mPaintStatus.setStyle(Paint.Style.FILL);

        //进度条绘制
        mPaintPress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPress.setAntiAlias(true);                    //取消锯齿
        mPaintPress.setStyle(Paint.Style.FILL);
        mPaintPress.setStrokeWidth(dip2px(mContext,1f));
        //绘制气泡
        mPaintBubble = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBubble.setAntiAlias(true);                    //取消锯齿
        mPaintBubble.setStyle(Paint.Style.FILL);
        mPaintBubble.setStrokeWidth(dip2px(mContext,1f));
        mPaintBubble.setTextSize(sp2px(mContext,14f));
        mPaintBubble.setTextAlign(Paint.Align.CENTER);

        mPath = new Path();
        mPathBg = new Path();

        statusColor.add("#ffffff");//0 white
        statusColor.add("#62E191");//1 休息
        statusColor.add("#1AC8FF");//2 行走
        statusColor.add("#4375FF");//3 慢跑
        statusColor.add("#975BFF");//4 慢跑
        statusStatus.add("");//0 white
        statusStatus.add("休息");//1 休息
        statusStatus.add("行走");//2 行走
        statusStatus.add("慢跑");//3 慢跑
        statusStatus.add("慢跑");//4 慢跑

        heartRateHeight = tempHeight = dip2px(mContext,55f);
        statusHeight = dip2px(mContext,45f);//状态条高度
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
        invalidate();
    }

    public void setHeartRateList(List<ReViewBean> heartRateList) {
        this.heartRateList = heartRateList;
        heartRateMax = (float)((ReViewBean)Collections.max(heartRateList)).value;
        heartRateMin = (float)((ReViewBean)Collections.min(heartRateList)).value;
        invalidate();
    }

    public void setTempList(List<ReViewBean> tempList) {
        this.tempList = tempList;
        tempMax = (float)((ReViewBean)Collections.max(tempList)).value;
        tempMin = (float)((ReViewBean)Collections.min(tempList)).value;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        width = getWidth();
        height = getHeight();

        startTime = 1593396393000l;
//        endTime = 1593417993698l;
        endTime = 1593396753000l;

        float yTitleSpan = dip2px(mContext,3f);
        float xTitleSpan = dip2px(mContext,4f);

//         tempTop = height - mMarginBottom - dip2px(mContext,48f)-tempHeight;
//         heartRateTop = tempTop - dip2px(mContext,8f)-tempHeight;

        //绘制x轴
        drawTextX(canvas,xTitleSpan);

        //绘制状态条
        drawStatus(canvas,yTitleSpan);
        mPaintLine.setColor(Color.parseColor("#004C64"));

        //绘制温度
        String bgColor = "#F2FDFF";
        float tempBottom = height - mMarginBottom - dip2px(mContext,48f);
        float tempTop = tempBottom-tempHeight;
        drawTempAndShadow(canvas,"42","32",tempTop,tempBottom,yTitleSpan,bgColor,new int[]{Color.parseColor("#FF00A8E5"),Color.parseColor("#2400A8E5")},tempList);
        drawLineAndbg(canvas,tempList,tempTop,tempBottom,bgColor,true);

        //绘制心率
        mPaintLine.setColor(Color.parseColor("#6E5700"));
        bgColor = "#FFFCF1";
        tempBottom = tempTop - dip2px(mContext,8f);
        tempTop = tempBottom-heartRateHeight;
        drawTempAndShadow(canvas,"200","0",tempTop,tempBottom,yTitleSpan,bgColor,new int[]{Color.parseColor("#FFE6B600"),Color.parseColor("#33E6B600")},heartRateList);
        drawLineAndbg(canvas,heartRateList,tempTop,tempBottom,bgColor,false);


        //绘制灰色进度条
        float mRight = width-mMarginRight;
        mPaintPress.setColor(Color.parseColor("#0A000000"));
        float pressTop = tempTop-dip2px(mContext,18f);
        float pressBottom = tempTop-dip2px(mContext,14f);
        canvas.drawRoundRect(mMarginLeft,pressTop,mRight,pressBottom,dip2px(mContext,2f),dip2px(mContext,2f),mPaintPress);

        float ecgBottom = tempTop-dip2px(mContext,25f);
        float itemHeight = ecgBottom/30;//30个小格 6个大格
        float ecgWidth = mRight-mMarginLeft;
        int wSize = (int)(ecgWidth/itemHeight);
        int hSize = (int)(ecgBottom/itemHeight);
        //绘制网格
        drawGridLine(canvas,mRight,ecgBottom,itemHeight,wSize,hSize);

        baseOneScreenCount = (int)(width-mMarginLeft-mMarginRight);//通过像素点绘制回看
        totalNum = baseOneScreenCount*4;
        currentData = new short[baseOneScreenCount];
        baseCenter = itemHeight*hSize/2;//基线中心线

        //绘制心电和进度
        drawEcg(canvas,pressTop,pressBottom,mRight,ecgWidth);

    }

    /**
     * 绘制心电图
     * @param canvas
     */
    private void drawEcg(Canvas canvas,float top,float bottom,float right,float ecgWidth) {
//        float mRight = width-mMarginRight;
        float ecgBottom = top-dip2px(mContext,8f);
//        float ecgWidth = mRight-mMarginLeft;
//        float itemHeight = ecgBottom/30;//30个小格 6个大格
//        baseOneScreenCount = (int)(ecgWidth/itemHeight)+1;
//        baseOneScreenCount = (int)ecgWidth;//通过像素点绘制回看
//        int wSize = (int)(ecgWidth/itemHeight);
//        int hSize = (int)(ecgBottom/itemHeight);
//        baseCenter = itemHeight*hSize/2;//基线中心线
        //绘制网格
//        drawGridLine(canvas,mRight,ecgBottom,itemHeight,wSize,hSize);
        //绘制中心线
//        mPaintPress.setColor(Color.BLUE);
//        canvas.drawLine(mMarginLeft,baseCenter,mRight,baseCenter,mPaintPress);
        //绘制灰色进度条
//        mPaintPress.setColor(Color.parseColor("#0A000000"));
//        canvas.drawRoundRect(mMarginLeft,top,mRight,bottom,dip2px(mContext,2f),dip2px(mContext,2f),mPaintPress);

        float diff = mTouchX -  mTouchEndX;
        if (mTouchEndX > mMarginLeft&& mTouchEndX < right) {
            if (mTouchY<=ecgBottom) {//移动心电图
                int numb = lastDataPosition+(int)diff ;
                if (numb >= baseOneScreenCount) {
                    numb = baseOneScreenCount;
                }else if (numb <= 0) {
                    numb = 0;
                }
                if((totalNum-numb)<baseOneScreenCount && totalNum>baseOneScreenCount) numb = totalNum-baseOneScreenCount;
                drawEcgLine(canvas,numb);//绘制心电图

                if (diff<0 && lastPressLine <= mMarginLeft) return;
                if (diff >0 && lastPressLine >= right) return;

                if (totalNum>0) {
                    float diffPerLen = diff / totalNum * ecgWidth;
                    float currentPressLine = lastPressLine + diffPerLen;
                    if (currentPressLine>right) {
                        currentPressLine = right;
                    } else if (currentPressLine<mMarginLeft) {
                        currentPressLine = mMarginLeft;
                    }

                    drawPress(canvas,top,bottom,currentPressLine);
                    lastPressLine = currentPressLine;

                    // TODO: 2020/7/10 绘制气泡





                }

            } else {//移动进度条
                if (totalNum>0) {
                    int position = (int)((mTouchEndX-mMarginLeft)/ecgWidth)*totalNum;//获取数据起始位置
                    drawEcgLine(canvas,position);//绘制心电图

                    drawPress(canvas,top,bottom,mTouchEndX);//绘制进度条
                    lastPressLine = mTouchEndX;


                    //绘制状态气泡
                    float baseLineWidth = width-mMarginRight-mMarginLeft;
                    float lenTime = (endTime-startTime)/1000/30;
                    float span = baseLineWidth/lenTime;//每个时间段的长度

                    if (statusList!= null && statusList.size()>0) {
                        int positionStatus = (int)((mTouchEndX-mMarginLeft)/baseLineWidth*statusList.size());

                        if (positionStatus>=statusList.size()) {
                            positionStatus = statusList.size()-1;
                        }

                        float pointY =height-dip2px(mContext,27);
                        int status = statusList.get(positionStatus);//运动状态
                        if (status != 0) {
                            drawBubble(canvas,statusStatus.get(status),mTouchEndX,pointY,Color.parseColor(statusColor.get(statusList.get(positionStatus))));
                        }
                    }

                    if (tempList != null && tempList.size()>0) {
                        drawBubbleTempAndHeartRate(canvas,"℃",span,Color.parseColor("#67D267"),tempList,true);
                    }

                    if (heartRateList != null && heartRateList.size()>0) {
                        drawBubbleTempAndHeartRate(canvas,"bpm",span,Color.parseColor("#F59C7A"),heartRateList,false);
                    }

                }

            }
        }
    }


    /**
     * 绘制心电和温度气泡
     * @param canvas
     * @param unit       单位
     * @param span       时间间隔
     * @param color
     * @param data
     * @param isTemp     是否温度
     */
    private void drawBubbleTempAndHeartRate(Canvas canvas,String unit,float span,int color,List<ReViewBean> data,boolean isTemp) {
        int positionStatus = (int)((mTouchEndX-mMarginLeft)/span);
        long currentTime = startTime+30*1000*positionStatus;
        float values = 0f;
        float y = 0f;
        if (positionStatus==(data.size()-1)||positionStatus == 0) {
            values = (float) data.get(positionStatus).value;
        } else {
            for (ReViewBean bean:data) {
                if (bean.time == currentTime) {
                    values = (float)bean.value;
                    break;
                }
            }
        }

        if (values<=0.0f) return;
        String valueStr = "";
        if (isTemp) {
            y = getPointYTemp(values);
            valueStr = String.format("%.2f",values)+unit;
        } else {
            y = getPointYHeartRate(values);
            valueStr = (int)values + unit;
        }

        drawBubble(canvas,valueStr,mTouchEndX,y,color);

    }

    /**
     * 绘制气泡
     * @param canvas
     * @param x
     * @param y
     * @param color
     */
    private void  drawBubble(Canvas canvas,String value,float x,float y,int color){
        int w = dip2px(mContext,64)/2;
        int h = dip2px(mContext,24);
        int th = dip2px(mContext,4);
        int radius = dip2px(mContext,12f);
        mPath.reset();

        mPaintBubble.setColor(color);
        canvas.drawRoundRect(x-w,y-h-th,x+w,y-th,radius,radius,mPaintBubble);

        mPath.moveTo(x,y);
        mPath.lineTo(x-dip2px(mContext,3),y-th);
        mPath.lineTo(x+dip2px(mContext,3),y-th);
        mPath.close();
        canvas.drawPath(mPath,mPaintBubble);
        mPaintBubble.setColor(Color.WHITE);
        canvas.drawText(value, x, y-h/2, mPaintBubble);

    }
    /**
     * 获取最大最小值宽高
     * @param text
     * @return
     */
    protected Rect getTextBounds(@NonNull String text) {
        Rect rect = new Rect();
        mPaintBubble.getTextBounds(text,0,text.length()-1,rect);
        return rect;
    }

    /**
     * 绘制心电波形
     * @param canvas
     * @param position   开始起始位置
     */
    private void  drawEcgLine(Canvas canvas,int position) {

        mPath.reset();
        // TODO: 2020/7/9 获取数据 通过起始位置和长度来拿数据 position 长度 baseOneScreenCount

//        mPaintLine.setColor(ecgLineColor);
        if (currentData!=null && currentData.length>0) {
            int len = currentData.length;
            mPath.moveTo(mMarginLeft,getPointEcgY(currentData[0]));
            for (int i = 1; i < len; i++) {
                mPath.lineTo(mMarginLeft+i,getPointEcgY(currentData[i]));
            }
            mPath.close();
            canvas.drawPath(mPath,mPaintLine);
        }

        lastDataPosition = position;
    }

    /**
     * 绘制网格
     * @param canvas
     * @param mRight      x轴结束位置
     * @param ecgBottom   y轴结束位置
     * @param itemHeight  网格高度
     * @param wSize       横向线条数量
     * @param hSize       纵向线条数量
     */
    private void drawGridLine(Canvas canvas, float mRight,float ecgBottom, float itemHeight,int wSize,int hSize) {
        int ecgLineColor = Color.parseColor("#F9B8B8");
        //绘制ecg网格
        mPaintPress.setColor(ecgLineColor);
        mPaintPress.setStrokeWidth(dip2px(mContext,0.2f));
        mPaintLine.setColor(ecgLineColor);

        //绘制竖线
        for (int i = 0; i <= wSize; i++) {
            if (i%5 == 0) {
                canvas.drawLine(mMarginLeft+i*itemHeight,0,mMarginLeft+i*itemHeight,ecgBottom,mPaintLine);
            } else {
                canvas.drawLine(mMarginLeft+i*itemHeight,0,mMarginLeft+i*itemHeight,ecgBottom,mPaintPress);
            }
        }
        //绘制横线
        for (int i = 0; i <= hSize; i++) {
            if (i%5 == 0) {
                canvas.drawLine(mMarginLeft,i*itemHeight,mRight,i*itemHeight,mPaintLine);
            } else {
                canvas.drawLine(mMarginLeft,i*itemHeight,mRight,i*itemHeight,mPaintPress);
            }
        }

    }

    /**
     * 获取心电y轴
     * @param value
     * @return
     */
    private float getPointEcgY(float value) {
        float y = baseCenter;
        // TODO: 2020/7/9 根据规则将y点进行改造

        return y;
    }

    /**
     * 绘制进度条
     * @param canvas
     * @param top
     * @param bottom
     * @param pointX
     */
    private void drawPress(Canvas canvas, float top, float bottom, float pointX) {
        float cyclePointY = (top + bottom) / 2;
        mPaintPress.setColor(Color.parseColor("#00C0C5"));
        //绘制进度条
        canvas.drawRoundRect(mMarginLeft, top, pointX, bottom, dip2px(mContext, 2f), dip2px(mContext, 2f), mPaintPress);
        //绘制竖线
        mPaintLine.setColor(Color.parseColor("#444444"));
        canvas.drawLine(pointX, cyclePointY, pointX, height - dip2px(mContext, 30), mPaintLine);
        //绘制进度圆形按钮
        mPaintPress.setColor(Color.WHITE);
        canvas.drawCircle(pointX, cyclePointY, dip2px(mContext, 7.5f), mPaintPress);
        mPaintPress.setColor(Color.parseColor("#00C0C5"));
        canvas.drawCircle(pointX, cyclePointY, dip2px(mContext, 4f), mPaintPress);
    }

    /**
     * 绘制温度心率背景和刻度
     * @param canvas
     * @param yTopTitle
     * @param yBottomTitle
     * @param topY
     * @param bottomY
     * @param yTitleSpan
     * @param bgColor          背景色
     * @param colors           渐变颜色
     * @param data             数据
     */
    private void drawTempAndShadow(Canvas canvas,String yTopTitle,String yBottomTitle,float topY,float bottomY, float yTitleSpan,String bgColor,int[] colors,List<ReViewBean> data) {

        if (data == null || data.size()==0) {
            mPaintStatus.setColor(Color.parseColor(bgColor));
            canvas.drawRect(mMarginLeft,topY,width-mMarginRight,bottomY,mPaintStatus);
        } else {
            LinearGradient linearGradient = new LinearGradient(0, 0, 0, bottomY, colors,null, Shader.TileMode.MIRROR);
            mPaintShadow.setShader(linearGradient);
            canvas.drawRect(mMarginLeft,topY,width-mMarginRight,bottomY,mPaintShadow);
        }

        float itemSpan = tempHeight/4;
        drawTextY(canvas,yTopTitle,mMarginLeft-yTitleSpan,topY+itemSpan);
        drawTextY(canvas,yBottomTitle,mMarginLeft-yTitleSpan,bottomY-itemSpan);

    }


    /**
     * 绘制线条和背景遮罩层
     * @param canvas
     * @param data
     * @param top
     * @param bottom
     * @param color
     * @param isTemp
     */
    private void drawLineAndbg(Canvas canvas,List<ReViewBean> data,float top, float bottom,String color,boolean isTemp) {
        mPath.reset();
        mPathBg.reset();

        if (data != null && data.size() >= 1) {
//            float lenTime = (endTime-startTime)/1000/30;
            float baseLineWidth = width-mMarginRight-mMarginLeft;
            float startPointX = mMarginLeft;
            float duration = endTime - startTime;
//            float pointSpan = (width-mMarginRight-mMarginLeft)/(data.size()-1);
            int len = data.size();

            float startX = 0f;
            float startY = 0f;
            float endX =0f;
            float endY =0f;
            Long startPointTime;

            mPathBg.moveTo(startPointX,top);
            for (int i = 0; i < len; i++) {
                startPointTime = data.get(i).time;
//                int cuTime = (int)(startPointTime-startTime)/1000/30;
                float pointX = startPointX + baseLineWidth * (startPointTime-startTime)/duration;
//                float pointX = startPointX + pointSpan * i;
                ReViewBean reViewBean = data.get(i);
                float pointY = isTemp?getPointYTemp((float)reViewBean.value):getPointYHeartRate((float) reViewBean.value);
                if (i+1 < len) {
                    if (startPointTime+30*1000 != data.get(i+1).time) {
                        mPathBg.lineTo(endX,bottom);
                        mPathBg.lineTo(startPointX + baseLineWidth * (data.get(i+1).time-startTime)/duration,bottom);
                        continue;
                    }
                    startX = pointX;
                    startY = pointY;
//                    endX = startPointX + pointSpan * (i+1);
//                    int currentLen = (int)(data.get(i+1).time-startTime)/1000/30;
                    endX = startPointX + baseLineWidth * (data.get(i+1).time-startTime)/duration;
                    endY = isTemp?getPointYTemp((float) data.get(i+1).value):getPointYHeartRate((float) data.get(i+1).value);

                    float ptX2 = (startX + endX)/2;
                    float ptY2 = startY;
                    float ptX3 = (startX + endX)/2;;
                    float ptY3 = endY;
                    mPath.moveTo(startX, startY);
                    mPath.cubicTo(ptX2,ptY2,ptX3,ptY3,endX,endY);
//                    mPath.lineTo(endX,bottom);
//                    mPath.lineTo(startX,bottom);
                    canvas.drawPath(mPath,mPaintLine);

                    mPathBg.lineTo(startX, startY);
                    mPathBg.cubicTo(ptX2,ptY2,ptX3,ptY3,endX,endY);

                }
            }
            mPath.close();
            long time = data.get(len-1).time;
            if (time < endTime) {
                mPathBg.lineTo(startPointX + baseLineWidth * (time-startTime)/duration,bottom);
                mPathBg.lineTo(width-mMarginRight,bottom);
            }

            mPathBg.lineTo(width-mMarginRight,top);
            mPaintStatus.setColor(Color.parseColor(color));
            mPathBg.close();
            canvas.drawPath(mPathBg,mPaintStatus);



        }
    }

    /**
     * 获取温度y点
     * @param value
     * @return
     */
    private float getPointYTemp(float value) {
        float max = 42f;
        float min = 32f;
        float bottom = height-dip2px(mContext,47f)-mMarginBottom;
        float top = bottom - tempHeight;
        float result = 0l;

        if (value > max) {
            result =  getPointYInner(top,top+tempHeight/4,tempMax,max,value);
        } else if (value >= min) {
            result = getPointYInner(top+tempHeight/4,bottom-tempHeight/4,max,min,value);
        } else {
            result =  getPointYInner(bottom-tempHeight/4,bottom,min,tempMin,value);
        }
        return result;
    }

    /**
     * 获取心率y点
     * @param value
     * @return
     */
    private float getPointYHeartRate(float value) {
        float max = 200f;
        float min = 0f;
        float bottom = height- dip2px(mContext,110f)-mMarginBottom;
        float top = bottom - heartRateHeight;
        float result = 0l;

        if (value > max) {
            result =  getPointYInner(top,top+heartRateHeight/4,heartRateMax,max,value);
        } else if (value >= min) {
            result = getPointYInner(top+heartRateHeight/4,bottom-heartRateHeight/4,max,min,value);
        } else {
            result =  getPointYInner(bottom-heartRateHeight/4,bottom,min,heartRateMin,value);
        }
        return result;
    }

    /**
     * 根据比例获取y点
     * @param startY
     * @param endY
     * @param maxValue
     * @param minValue
     * @param value
     * @return
     */
    private float getPointYInner(float startY, float endY, float maxValue, float minValue, float value) {
        return startY + (maxValue - value) * (endY - startY) / (maxValue - minValue);
    }

    /**
     * 绘制状态条
     * @param canvas
     */
    private void drawStatus(Canvas canvas,float ySpan) {

        String statusTitle = "状态";
        drawTextY(canvas,statusTitle,mMarginLeft-ySpan,height-dip2px(mContext,22));
        //绘制
        if (statusList != null && statusList.size()>0) {
            float baseLineWidth = width-mMarginRight-mMarginLeft;
            float lenTime = (endTime-startTime)/1000/30;
            float span = baseLineWidth/lenTime;
            mPaintStatus.setColor(Color.parseColor(statusColor.get(1)));
            float top =height-dip2px(mContext,27);
            float bottom = height-mMarginBottom;
//            RectF rectF = new RectF(mMarginLeft,top,width-mMarginRight,bottom);
//            canvas.drawRoundRect(rectF,dip2px(mContext,5),dip2px(mContext,5),mPaintStatus);

            //根据时间来设置不同的状态条
            int len = statusList.size();
//            Long duration = endTime-startTime;
            float startX = mMarginLeft;
            float endX = 0f;
            int lastStatusType = statusList.get(0);//默认
            int num = 0;
            for (int i = 1; i < len; i++) {

                int status = statusList.get(i);
                num++;
                if (status == lastStatusType) {
                    if (i<len-1) {
                        continue;
                    }
                }
                mPaintStatus.setColor(Color.parseColor(statusColor.get(lastStatusType)));
                endX = startX + num*span;
                canvas.drawRect(startX,top,endX,bottom,mPaintStatus);
                lastStatusType = status;
                startX = endX;
                num = 0;
            }
        }
    }

    /**
     * 绘制y轴数据
     * @param canvas
     * @param statusTitle
     * @param pointX
     * @param pointY
     */
    private void drawTextY(Canvas canvas, String statusTitle, float pointX,float pointY) {
        Rect bounds = new Rect();
        mPaintText.getTextBounds(statusTitle,0,statusTitle.length(),bounds);
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        float heights = fontMetrics.bottom - fontMetrics.top;
        canvas.drawText(statusTitle, pointX, pointY+heights/2 - mPaintText.descent(), mPaintText);
    }

    /**
     * 绘制x轴时间
     * @param canvas
     */
    private void drawTextX(Canvas canvas,float ySpan){
        mPaintText.setTextAlign(Paint.Align.LEFT);
        int spanXCount = 4;//x轴间隔
        float spanX = (width-mMarginLeft-mMarginRight)/spanXCount;
        Long itemTime = (endTime - startTime)/spanXCount;
        float yBase = height-(mMarginBottom-ySpan)/2;
        Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
        float heights = fontMetrics.bottom - fontMetrics.top;
        String startTimeStr = "";
        for (int i = 0; i <= spanXCount; i++) {
            if (i == spanXCount) {
                mPaintText.setTextAlign(Paint.Align.RIGHT);
            }
            startTimeStr = timeToStr(startTime+i*itemTime);
            canvas.drawText(startTimeStr, mMarginLeft+spanX*i, yBase+heights/2 - mPaintText.descent(), mPaintText);
        }
    }

    private String timeToStr(Long time) {
        return timeToStr(time,STRPHOUR);
    }

    private String timeToStr(Long time,String formate) {
        if (time != null && time>0) {
            return new SimpleDateFormat(formate).format(new Date(time));
        }
        return "";
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("zero","===============ACTION_DOWN x:"+event.getX());
                mTouchX = event.getX();
                mTouchY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                mTouchEndX = event.getX();
//                mDiffX = mTouchDiffX-mTouchX;
//                Log.i("zero","===============x diff:"+mDiffX);
                postInvalidate();
                break;
        }

        return true;
//        return super.onTouchEvent(event);
    }



    /* 获取三阶贝塞尔曲线方程上的某一点
     *
     * float t 指定的x坐标比例，取值范围是 0 ~ 1 之间
     * CGPoint startPoint 起始点
     * CGPoint controlPoint1 控制点1
     * CGPoint controlPoint2 控制点2
     * CGPoint endPoint 结束点
     * 返回值：返回指定x坐标的贝塞尔曲线上的某一点
     */
//    CGPoint getCubicBezierPathThePoint(const float t,const CGPoint startPoint,const CGPoint controlPoint1,const CGPoint controlPoint2,const CGPoint endPoint){
//        CGPoint point = CGPointZero;
//        float temp = 1 - t;
//        point.x = startPoint.x * temp * temp * temp + 3 * controlPoint1.x * t * temp * temp + 3 * controlPoint2.x * t * t * temp + endPoint.x * t * t * t;
//        point.y = startPoint.y * temp * temp * temp + 3 * controlPoint1.y * t * temp * temp + 3 * controlPoint2.y * t * t * temp + endPoint.y * t * t * t;
//        return point;
//    }
//    private float getCubY() {
//
//    }




    public static class ReViewBean implements Comparable<ReViewBean>{

        public Long time;
        public double value;

        public ReViewBean(Long time, double value) {
            this.time = time;
            this.value = value;
        }

        @Override
        public int compareTo(ReViewBean o) {
            if (value > o.value) return 1;
            if (value==o.value) return 0;
            return -1;
        }
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
