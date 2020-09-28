package com.jjs.zero.httplibrary.common.error;

/**
 * @Author: jiajunshuai
 * @CreateTime: 2020/9/28
 * @Details: <功能描述>
 */
public class CommonException extends Exception{

    private ErrorMsg errorMsg;

    public CommonException(ErrorMsg errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ErrorMsg getErrorMsg(){
        return errorMsg;
    }
}
