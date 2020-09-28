package com.jjs.zero.httplibrary.common.service;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/28
 * @Details: <统一请求类>
 */
public class BaseResult<T> {
    private boolean success;
    private String errorCode;
    private String errorMsg;
    private T result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
