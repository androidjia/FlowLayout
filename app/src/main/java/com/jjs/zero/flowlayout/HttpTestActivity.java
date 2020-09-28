package com.jjs.zero.flowlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.jjs.zero.httplibrary.common.service.ConfigUrl;
import com.jjs.zero.httplibrary.common.service.ResultObserver;
import com.jjs.zero.httplibrary.demo.Goods;
import com.jjs.zero.httplibrary.demo.RequestManager;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HttpTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_test);

        ConfigUrl url = ConfigUrl.load(this);

        Log.i("zero","HttpTestActivity url:"+url.getUrl());


        new RequestManager(this,"tokens","2313523424").getProductsAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver<>(success->{
                    Log.i("zero","success url:"+url.getUrl());
                },errorMsg -> {
                    Log.i("zero","errorMsg url:"+errorMsg.getErrorMsg());
                }));



//                .subscribe(new Observer<List<Goods>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.i("zero","onSubscribe url:"+url.getUrl());
//
//                    }
//
//                    @Override
//                    public void onNext(List<Goods> goods) {
//                        Log.i("zero","onNext url:"+url.getUrl());
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.i("zero","onError url:"+url.getUrl());
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.i("zero","onComplete url:"+url.getUrl());
//
//                    }
//                });

    }
}