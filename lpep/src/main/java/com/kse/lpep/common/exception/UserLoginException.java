package com.kse.lpep.common.exception;

public class UserLoginException extends BaseException{
    public UserLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserLoginException(String message) {
        super(message);
    }

    public UserLoginException(Throwable cause) {
        super(cause);
    }
}
