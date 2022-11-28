package com.kse.lpep.common.exception;

public class QuestionNotExistException extends BaseException{
    public QuestionNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuestionNotExistException(String message) {
        super(message);
    }

    public QuestionNotExistException(Throwable cause) {
        super(cause);
    }
}
