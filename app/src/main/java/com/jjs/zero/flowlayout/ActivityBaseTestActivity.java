package com.jjs.zero.flowlayout;

import android.util.SparseArray;

import com.jjs.zero.baseviewlibrary.BaseActivity;
import com.jjs.zero.flowlayout.databinding.ActivityBaseTestBinding;

import java.util.LinkedHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class ActivityBaseTestActivity extends BaseActivity<ActivityBaseTestBinding> {

    @Override
    public int layoutResId() {
        return R.layout.activity_base_test;
    }

    @Override
    protected void initData() {

        SparseArray<String> sparseArray = new SparseArray<>();

//        Observable.just("sdfsdf").flatMap(new Function<String, ObservableSource<?>>() {
//            @Override
//            public ObservableSource<?> apply(String string) throws Exception {
//                return Observable.just("dsf").doOnNext(new Consumer<String>() {
//                    @Override
//                    public void accept(String string) throws Exception {
//
//                    }
//                });
//            }
//        });
    }
}