package com.kse.lpep.common.exception;

public class RecordNotExistException extends BaseException{
    public RecordNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordNotExistException(String message) {
        super(message);
    }

    public RecordNotExistException(Throwable cause) {
        super(cause);
    }

}
