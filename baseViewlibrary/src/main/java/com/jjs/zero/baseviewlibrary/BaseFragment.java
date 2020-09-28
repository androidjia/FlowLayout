package com.jjs.zero.baseviewlibrary;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;


/**
 * @Author: jiajunshuai
 * @CreateTime: 2020-01-06
 * @Details: <功能描述>
 */
public abstract class BaseFragment<V extends ViewDataBinding> extends Fragment {

    protected V viewBinding;
    protected boolean mIsVisible = false;
    private View emptyView;
    private LoadingFragment loadingView;

    protected abstract int layoutResId();
    protected Context mContext;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View ll = inflater.inflate(R.layout.fragment_base,container,false);
        viewBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),layoutResId(),null,false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewBinding.getRoot().setLayoutParams(params);
        RelativeLayout mContaioner = ll.findViewById(R.id.container);
        mContaioner.addView(viewBinding.getRoot());
        initData();
        return ll;
    }

    //数据缓加载
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            mIsVisible = true;
            onVisible();
        }else {
            mIsVisible = false;
            onInVisible();
        }
    }

    protected <T extends View> T getView(@IdRes int id){
        return (T)getView().findViewById(id);
    }

    //加载数据
    protected abstract void initData();

    protected void onInVisible() {

    }

    protected void onVisible() {
        loadData();
    }

    /**
     * 显示时加载数据 生命周期会先执行 setUserVisibleHint 再执行onActivityCreated
     * 在 onActivityCreated 之后第一次显示加载数据，只加载一次
     */
    protected void loadData() {

    }


    /**
     * 显示加载中状态
     */
    protected void showLoading() {
        if (loadingView != null) {
            loadingView = null;
        }
        loadingView = new LoadingFragment();
        loadingView.show(getActivity().getFragmentManager(),LoadingFragment.class.getName());
    }

    /**
     * 隐藏加载状态
     */
    protected void hideLoading() {
        if (loadingView != null) {
            loadingView.hide();
            loadingView = null;
        }
    }

    /**
     * 加载完成的状态
     */
    protected void showContentView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (viewBinding.getRoot().getVisibility() != View.VISIBLE) {
            viewBinding.getRoot().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 加载失败点击重新加载的状态
     */
    protected void showEmpty() {
        if (emptyView == null) {
            ViewStub viewStub = getView(R.id.view_empty);
            emptyView = viewStub.inflate();
            // 点击加载失败布局
//            errorView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showLoading();
//                    onErrorRefresh();
//                }
//            });
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
        if (viewBinding.getRoot().getVisibility() != View.GONE) {
            viewBinding.getRoot().setVisibility(View.GONE);
        }
    }

    protected void showEmpty(CharSequence text, @DrawableRes int image) {
        showEmpty();
        ImageView iv = emptyView.findViewById(R.id.iv_empty);
        iv.setImageResource(image);
        TextView tv = emptyView.findViewById(R.id.tv_empty);
        tv.setText(text);
    }

    //加载失败后点击
//    protected void onErrorRefresh() {
//
//    }

    protected void showToast(CharSequence msg){
        Toast.makeText(mContext,msg, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(@StringRes int msg){
        Toast.makeText(mContext,msg, Toast.LENGTH_SHORT).show();
    }

}
