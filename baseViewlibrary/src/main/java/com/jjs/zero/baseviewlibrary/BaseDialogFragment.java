package com.jjs.zero.baseviewlibrary;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;


/**
 * @Author: jiajunshuai
 * @CreateTime: 2020-01-16
 * @Details: <弹框基类封装>
 */
@SuppressLint("ValidFragment")
public abstract class BaseDialogFragment<D extends ViewDataBinding> extends DialogFragment {

    protected D viewBinding;
    protected Context mContext;
    private int viewSite = Gravity.CENTER;//布局位置
    private boolean isOnTouchOutSide = false;

    protected BaseDialogFragment() {
        super();
    }

    /**
     * 是否点击外部消失
     * @param isOnTouchOutSide
     */
    protected BaseDialogFragment(boolean isOnTouchOutSide) {
        this.isOnTouchOutSide = isOnTouchOutSide;
    }

    /**
     * 布局位置
     * @param site
     */
    protected BaseDialogFragment(int site) {
        viewSite = site;
    }
    protected BaseDialogFragment(int site, boolean isOnTouchOutSide) {
        this(isOnTouchOutSide);
        viewSite = site;
    }

    protected abstract @LayoutRes
    int layoutResId();

    protected abstract void initData();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        if (getDialog()!=null){
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题
            getDialog().setCanceledOnTouchOutside(isOnTouchOutSide);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);// 解决全屏时状态栏变黑
            }
        }
        viewBinding =  DataBindingUtil.inflate(inflater,layoutResId(),container,false);
        initData();
        return viewBinding.getRoot();
    }


    public boolean isOnTouchOutSide() {
        return isOnTouchOutSide;
    }

    public BaseDialogFragment setOnTouchOutSide(boolean onTouchOutSide) {
        isOnTouchOutSide = onTouchOutSide;
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(onTouchOutSide);
        }
        return this;
    }

    public int getViewSite() {
        return viewSite;
    }

    public BaseDialogFragment setViewSite(int viewSite) {
        this.viewSite = viewSite;
        setSite(viewSite);
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
       setSite(viewSite);
    }


    private void setSite(int site) {
        Window mWindow = getDialog().getWindow();
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
//        mLayoutParams.windowAnimations = R.style.
        mLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = site;
        mWindow.setAttributes(mLayoutParams);
//        mWindow.setDimAmount(0.5f);// 0~1 , 1表示完全昏暗
    }

}
