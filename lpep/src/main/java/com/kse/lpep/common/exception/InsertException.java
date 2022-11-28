package com.kse.lpep.common.exception;

public class InsertException extends BaseException{
    public InsertException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsertException(String message) {
        super(message);
    }

    public InsertException(Throwable cause) {
        super(cause);
    }
}
