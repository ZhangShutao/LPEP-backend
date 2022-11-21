package com.kse.lpep.common.exception;

public class ExperNameDuplicateException extends BaseException{

    public ExperNameDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExperNameDuplicateException(String message) {
        super(message);
    }

    public ExperNameDuplicateException(Throwable cause) {
        super(cause);
    }

}
