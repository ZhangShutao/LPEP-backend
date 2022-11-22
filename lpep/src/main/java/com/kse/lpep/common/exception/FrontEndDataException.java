package com.kse.lpep.common.exception;

public class FrontEndDataException extends BaseException{
    public FrontEndDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrontEndDataException(String message) {
        super(message);
    }

    public FrontEndDataException(Throwable cause) {
        super(cause);
    }
}
