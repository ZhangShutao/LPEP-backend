package com.kse.lpep.common.exception;

/**
 * 自定义异常，表示操作对应的记录在数据库中不存在
 * @author 张舒韬
 * @since 2022/11/22
 */
public class NoSuchRecordException extends Exception {

    public NoSuchRecordException(String message) {
        super(message);
    }

    public NoSuchRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
