package com.jjs.zero.httplibrary.common.service;

import com.jjs.zero.httplibrary.common.error.CommonException;
import com.jjs.zero.httplibrary.common.error.ErrorMsg;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/28
 * @Details: <功能描述>
 */
public class ResultMapper<T> implements Function<BaseResult<T>, Observable<T>> {


    @Override
    public Observable<T> apply(BaseResult<T> tBaseResult) throws Exception {
        if (!tBaseResult.isSuccess()){
            //可自定义异常类型
            throw new CommonException(new ErrorMsg(tBaseResult.getErrorCode(),tBaseResult.getErrorMsg()));
        }
        if (tBaseResult == null) {
            return Observable.empty();
        }
        return Observable.just(tBaseResult.getResult());
    }
}
