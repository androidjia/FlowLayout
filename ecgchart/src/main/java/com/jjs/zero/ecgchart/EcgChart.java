package com.jjs.zero.ecgchart;

import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jjs.zero.ecgchart.databinding.ViewMotionStateBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/4/30
 * @Details: <功能描述>
 */
public class EcgChart extends View {

    private int horizontalBigGirdNum = 3;
    private int verticalBigGirdNum = 4;
//    horizontalBigGirdNum 为横向的线，即纵向大格子的数量。
//    verticalBigGirdNum 为纵向的线，即横向大格子的数量。
    private int  widthOfSmallGird;
    private int baseLine;//基线
    private int maxLevel;//最大高度
    private int width;
    private int heightChart;
    private int heightEcg;
    BitmapDrawable paintDrawable;
    private Paint paintLine;
    private Paint ecgWave;
    private Path ecgPath;
    private List<Float> data = new ArrayList<>();



//    private ViewMotionStateBinding views;


    //chart图表

    private Paint paintLineChart;
    private Paint circlePaint;

    private int XPoint=50; //原点X轴坐标
    private int YPoint=700; //原点Y轴坐标
    private int XLength=600;//X轴长度
    private int YLength=600;//Y轴长度
    private int XScale=100;//X轴刻度的宽度
    private int YScale=100;//Y轴刻度的宽度
    private String[] XLabel={"1-14","1-15","1-16","1-17","1-18","1-19"};//X轴的刻度
    private String[] YLabel={"0","40","80","120","160","200"};//Y轴的刻度

    public EcgChart(Context context) {
        this(context,null);
    }

    public EcgChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EcgChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paintLine = new Paint();
        paintLine.setStyle(Paint.Style.STROKE);
        ecgWave = new Paint();
        ecgWave.setColor(Color.YELLOW);
        ecgWave.setStyle(Paint.Style.STROKE);
        ecgWave.setStrokeWidth(5);

        ecgPath = new Path();

        paintDrawable =(BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_query,null);

//        views = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.view_motion_state,null,false);

        paintLineChart = new Paint();
        paintLineChart.setAntiAlias(true);
        paintLineChart.setColor(Color.RED);
        circlePaint = new Paint();
        circlePaint.setColor(Color.BLUE);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        heightEcg = h*2/5;
        heightChart = h*3/5;
        widthOfSmallGird =15;
        baseLine = heightEcg/2;
        maxLevel = heightEcg/2;

        YLength = heightEcg-100;
        YScale = YLength/6;
        YPoint = h-100;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawEcg(canvas);

//        View viewStats = views.getRoot();
//        viewStats.setClipBounds(new Rect(width-300,heightEcg,width,heightEcg+30));
//        viewStats.draw(canvas);

        //绘制图表
        drawChart(canvas);

    }

    private void drawEcg(Canvas canvas) {
        drawGird(canvas);
        drawEcgLine(canvas);
        paintDrawable.setBounds(100,90,130,120);
        paintDrawable.draw(canvas);
    }

    public void setData(List<Float> data) {
        this.data = data;
        postInvalidate();
    }


    /**
     * 绘制心电图
     * @param canvas
     */
    private void drawEcgLine(Canvas canvas) {

        canvas.drawPath(ecgPath,ecgWave);
        if (data.size()>0) {
            ecgPath.moveTo(0,baseLine-data.get(0));
        } else {
            ecgPath.moveTo(0,baseLine);
        }

        int len = data.size();
        for (int i = 0; i < len; i++) {
            float y = baseLine-data.get(i);
            ecgPath.lineTo(i*widthOfSmallGird,y);
        }
        canvas.drawPath(ecgPath,ecgWave);


    }

    /**
     * 画网格
     * @param canvas
     */
    private void drawGird(Canvas canvas) {
        paintLine.setColor(Color.GREEN);

        //横线
        for (int i=0;i<=width/widthOfSmallGird;i++) {
            if (i%5 == 0) {
                paintLine.setStrokeWidth(3);
            } else {
                paintLine.setStrokeWidth(1);
            }
            canvas.drawLine(i*widthOfSmallGird,0,i*widthOfSmallGird,heightEcg,paintLine);
        }

        for (int i=0;i<=heightEcg/widthOfSmallGird;i++) {
            if (i%5 == 0) {
                paintLine.setStrokeWidth(3);
            } else {
                paintLine.setStrokeWidth(1);
            }
            canvas.drawLine(0,i*widthOfSmallGird,width,i*widthOfSmallGird,paintLine);
        }
    }




    private void drawChart(Canvas canvas) {

        canvas.drawLine(XPoint,YPoint,XPoint+XLength,YPoint,paintLineChart);//X轴
        canvas.drawLine(XPoint,YPoint,XPoint,YPoint-YLength,paintLineChart);//Y轴

        //绘制箭头
        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint-6,paintLineChart);//X箭头
        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint+6,paintLineChart);

        canvas.drawLine(XPoint,YPoint-YLength,XPoint-6,YPoint-YLength+6,paintLineChart);//Y箭头
        canvas.drawLine(XPoint,YPoint-YLength,XPoint+6,YPoint-YLength+6,paintLineChart);

        //绘制Y轴刻度
//        for (int i=0;i*YScale<YLength;i++){
        for (int i=0;i<YLabel.length;i++){
            canvas.drawLine(XPoint,YPoint-i*YScale,XPoint+5,YPoint-i*YScale,paintLineChart);
            canvas.drawText(YLabel[i],XPoint-20,YPoint-i*YScale+5,paintLineChart);
        }


        //绘制X轴刻度

        for (int i=0;i*XScale<XLength;i++){
            canvas.drawLine(XPoint+i*XScale,YPoint,XPoint+i*XScale,YPoint-5,paintLineChart);
            canvas.drawText(XLabel[i],XPoint+i*XScale-10,YPoint+20,paintLineChart);
            //画折线
            if(i<data.size()-1)
                canvas.drawLine(XPoint+i*XScale,YCoord(data.get(i)),XPoint+(i+1)*XScale,YCoord(data.get(i)),paintLineChart);
                canvas.drawCircle(XPoint+i*XScale,YCoord(data.get(i)),4,circlePaint);//每个数据对应的Y坐标
        }


    }

    /**
     * 计算数据对应的Y坐标
     * @param data
     * @return
     */
    private float YCoord(float data) {
//        Log.i("打印数据",YPoint-data*YScale/Integer.parseInt(YLabel[1])+"");
        return YPoint-data*YScale/Integer.parseInt(YLabel[1]);
    }
}
