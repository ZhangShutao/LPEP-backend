package com.kse.lpep.common.exception;

public class SaveFileIOException extends BaseException{
    public SaveFileIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveFileIOException(String message) {
        super(message);
    }

    public SaveFileIOException(Throwable cause) {
        super(cause);
    }
}
