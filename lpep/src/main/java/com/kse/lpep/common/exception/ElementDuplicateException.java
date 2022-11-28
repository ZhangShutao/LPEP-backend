package com.kse.lpep.common.exception;

public class ElementDuplicateException extends BaseException{

    public ElementDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElementDuplicateException(String message) {
        super(message);
    }

    public ElementDuplicateException(Throwable cause) {
        super(cause);
    }

}
