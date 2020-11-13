package com.alex.springcloud.exception;

/**
 * @description: 系统全局异常处理
 * @author:      alex
 * @createTime:  2020/11/13 14:31
 * @version:     1.0
 */
public class SystemException extends Exception {

    private static final long serialVersionUID = -994962710559017255L;

    public SystemException(String message) {
        super(message);
    }
}
