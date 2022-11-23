package com.kse.lpep.service.exception;

/**
 * 自定义异常，表示操作对应的记录在数据库中不存在
 * @author 张舒韬
 * @since 2022/11/22
 */
public class NoSuchRecordError extends Exception {

    public NoSuchRecordError(String message) {
        super(message);
    }

    public NoSuchRecordError(String message, Throwable cause) {
        super(message, cause);
    }
}
