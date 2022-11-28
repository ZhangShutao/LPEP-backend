package com.kse.lpep.common.exception;

/**
 * 不支持当前操作系统
 * @author 张舒韬
 * @since 2022/11/23
 */
public class UnsupportedOsTypeException extends Exception {
    public UnsupportedOsTypeException() {
        super("未知操作系统类型");
    }

    public UnsupportedOsTypeException(String message) {
        super("未知操作系统类型：" + message);
    }
}
