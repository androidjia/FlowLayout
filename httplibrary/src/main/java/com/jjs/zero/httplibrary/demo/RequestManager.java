package com.jjs.zero.httplibrary.demo;

import android.content.Context;

import com.jjs.zero.httplibrary.common.service.BaseManager;
import com.jjs.zero.httplibrary.common.service.ResultMapper;

import java.util.List;

import io.reactivex.Observable;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/28
 * @Details: <功能描述>
 */
public class RequestManager extends BaseManager {
    private RequestApi requestApi;
    public RequestManager(Context context, String token, String userId) {
        super(context,token,userId);
        requestApi = super.getInterface(RequestApi.class);
    }


    public Observable<List<Goods>> getProductsAll(){
        return requestApi.getProductsAll().flatMap(new ResultMapper<List<Goods>>());
    }


}
