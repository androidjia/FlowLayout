package com.jjs.zero.httplibrary.common.service;


import android.text.TextUtils;

import com.jjs.zero.httplibrary.common.error.CommonException;
import com.jjs.zero.httplibrary.common.error.ErrorMsg;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/28
 * @Details: <功能描述>
 */
public class ResultObserver<T> implements Observer<T> {

    private Disposable disposable;

    private Consumer<? super T> successHandler;
    private Consumer<ErrorMsg> errorHandler;
    private boolean isRun = false;
    private ResultObserver decorator;

    public ResultObserver() {
    }

    public ResultObserver(ResultObserver decorator) {
        this.decorator = decorator;
    }

    public ResultObserver(Consumer<? super T> successHandler) {
        this.successHandler = successHandler;
    }

    public ResultObserver(Consumer<? super T> successHandler, ResultObserver decorator) {
        this.successHandler = successHandler;
        this.decorator = decorator;
    }

    public ResultObserver(Consumer<? super T> successHandler, Consumer<ErrorMsg> errorHandler) {
        this.successHandler = successHandler;
        this.errorHandler = errorHandler;
    }

    public ResultObserver(Consumer<? super T> successHandler, Consumer<ErrorMsg> errorHandler, ResultObserver decorator) {
        this.successHandler = successHandler;
        this.errorHandler = errorHandler;
        this.decorator = decorator;
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(T t) {
        if (!isRun) {
            isRun = true;
            onSuccess(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (decorator != null) {
            decorator.onError(e);
        } else if (e instanceof CommonException) {
            onFailed(((CommonException) e).getErrorMsg());
        } else {
            onFailed(new ErrorMsg("10-00", TextUtils.isEmpty(e.getMessage()) ? "未知错误" : e.getMessage(), e));
        }
    }

    @Override
    public void onComplete() {
        if (!isRun) {
            isRun = true;
            onSuccess(null);
        }
    }

    public void onFailed(ErrorMsg exception) {
        if (errorHandler != null) {
            try {
                errorHandler.accept(exception);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onSuccess(T t) {
        if (successHandler != null) {
            try {
                successHandler.accept(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dispose() {
        this.disposable.dispose();
    }
}
