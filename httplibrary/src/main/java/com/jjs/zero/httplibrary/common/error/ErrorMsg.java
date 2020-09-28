package com.jjs.zero.httplibrary.common.error;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/28
 * @Details: <功能描述>
 */
public class ErrorMsg {
    private String errorCode;
    private String errorMsg;
    private Throwable exception;

    public ErrorMsg(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ErrorMsg(String errorCode, String errorMsg, Throwable e) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.exception = e;
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

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
