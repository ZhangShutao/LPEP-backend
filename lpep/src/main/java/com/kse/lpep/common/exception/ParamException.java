package com.kse.lpep.common.exception;
// 参数手动校验失败
public class ParamException extends BaseException{
    public ParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamException(String message) {
        super(message);
    }

    public ParamException(Throwable cause) {
        super(cause);
    }
}
