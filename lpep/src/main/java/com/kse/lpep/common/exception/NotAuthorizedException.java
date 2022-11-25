package com.kse.lpep.common.exception;

/**
 * 当前用户不具备对目标对象进行指定操作的权限
 * @author 张舒韬
 * @since 2022/11/24
 */
public class NotAuthorizedException extends Exception {

    public NotAuthorizedException(String message) {
        super(message);
    }

    public NotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
