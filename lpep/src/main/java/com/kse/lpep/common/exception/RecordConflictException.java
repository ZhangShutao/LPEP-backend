package com.kse.lpep.common.exception;

public class RecordConflictException extends BaseException{
    public RecordConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordConflictException(String message) {
        super(message);
    }

    public RecordConflictException(Throwable cause) {
        super(cause);
    }

}
