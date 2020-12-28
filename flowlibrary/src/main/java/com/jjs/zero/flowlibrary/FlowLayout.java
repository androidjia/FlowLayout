package com.jjs.zero.flowlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/4/16
 * @Details: <功能描述>
 */
public class FlowLayout extends ViewGroup {

    private int mHorizontalSpace = 0;
    private int mVerticalSpace = 0;
    private boolean horizontally = false;
    private int maxLint = 0;

    private Context mContext;
    public FlowLayout(Context context) {
        this(context,null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mHorizontalSpace = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_horizontal_space, 0);
        mVerticalSpace = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_vertical_space, 0);
        horizontally = typedArray.getBoolean(R.styleable.FlowLayout_horizontally, false);
        maxLint = typedArray.getInt(R.styleable.FlowLayout_max_line_num, 0);
        typedArray.recycle();
        this.mContext = context;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 如果是warp_content情况下，记录宽和高
        int height = 0;

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        //记录每一行的宽度，width不断取最大宽度
        int lineWidth = 0;

        //每一行的高度，累加至height
        int lineHeight = 0;

        int lineNum = 1;
        int currentLineChildCount = 0;

        int cCount = getChildCount();
        // 遍历每个子元素
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            // 当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth();
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight();
            // 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
            if (lineWidth + childWidth + mHorizontalSpace * currentLineChildCount > sizeWidth - paddingLeft - paddingRight) {
                ++lineNum;
                currentLineChildCount = 1;
                lineWidth = childWidth; // 重新开启新行，开始记录
                // 叠加当前高度，
                height += lineHeight;
                // 开启记录下一行的高度
                lineHeight = childHeight;
            } else {
                // 否则累加值lineWidth,lineHeight取最大高度
                ++currentLineChildCount;
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }
        //把最后一行加上来
        height += lineHeight;
        height += (lineNum - 1) * mVerticalSpace;
        height += (getPaddingTop() + getPaddingBottom());

        setMeasuredDimension(sizeWidth,height);
    }
    /**
     * 存储所有的View，按行记录
     */
    private List<List<View>> mAllViews = new ArrayList<>();
    /**
     * 记录每一行的最大高度
     */
    private List<Integer> mLineHeight = new ArrayList<>();
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int lineWidth = 0;
        int lineHeight = 0;
        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<>();
        int cCount = getChildCount();
        // 遍历所有的孩子
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            // 如果已经需要换行
            if (childWidth + lineWidth + mHorizontalSpace * lineViews.size() > width) {
                // 记录这一行所有的View以及最大高度
                mLineHeight.add(lineHeight);
                // 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
                mAllViews.add(lineViews);
                lineWidth = 0;// 重置行宽
                lineViews = new ArrayList<>();
            }

            //如果不需要换行，则累加
            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);
        }
        // 记录最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);


        int top = getPaddingTop();
        // 得到总行数
        int lineNum = mAllViews.size();
        for (int i = 0; i < lineNum; i++) {
            // 每一行的所有的views
            lineViews = mAllViews.get(i);
            // 当前行的最大高度
            lineHeight = mLineHeight.get(i);
            // 遍历当前行所有的View

            int left = getPaddingLeft();
            if (horizontally) {
                int childsWidth = 0;
                for (View lineView : lineViews) {
                    childsWidth += lineView.getMeasuredWidth();
                }
                childsWidth += mHorizontalSpace * (lineViews.size() - 1);
                left = left + (width - childsWidth) / 2;
            }

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                //计算childView的left,top,right,bottom
                int lc = left;
                int rc = lc + child.getMeasuredWidth();
                int bc = top + child.getMeasuredHeight();
                child.layout(lc, top, rc, bc);
                left += child.getMeasuredWidth();
                left += mHorizontalSpace;
            }
            top += lineHeight;
            top += mVerticalSpace;
        }
    }


    public int getHorizontalSpace() {
        return mHorizontalSpace;
    }

    public void setHorizontalSpace(int mHorizontalSpace) {
        this.mHorizontalSpace = mHorizontalSpace;
    }

    public int getVerticalSpace() {
        return mVerticalSpace;
    }

    public void setVerticalSpace(int mVerticalSpace) {
        this.mVerticalSpace = mVerticalSpace;
    }

    public boolean isHorizontally() {
        return horizontally;
    }

    public void setHorizontally(boolean horizontally) {
        this.horizontally = horizontally;
    }

    public int getMaxLint() {
        return maxLint;
    }

    public void setMaxLint(int maxLint) {
        this.maxLint = maxLint;
    }

    public void addView(List<String> data, OnChildView onChildView) {
        if (data != null && data.size()>0 && onChildView != null) {
            for (int i=0;i<data.size();i++) {
                TextView view =  onChildView.getChildView(i);
                view.setText(data.get(i));
                addView(view);
            }
        }
    }


    public interface OnChildView{
        TextView getChildView(int i);
    }
}
