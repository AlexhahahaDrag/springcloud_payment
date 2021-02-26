package com.alex.springcloud.handler;

import com.alex.springcloud.domain.Response;
import com.alex.springcloud.exception.SystemException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.Path;
import java.util.List;


/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/13 14:28
 * @version:     1.0
 */
//拦截异常处理
@RestControllerAdvice
//代表这个过滤器在众多过滤器中级别最高，也就是过滤的时候最先执行
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleException(Exception e) {
        log.error("系统内部异常，异常信息：" + e);
        return new Response().message("系统内部异常");
    }

    @ExceptionHandler(value = SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleParamsInvalidException(SystemException e) {
        log.error("系统错误：{}", e.getMessage());
        return new Response().message(e.getMessage());
    }

    /**
     * 统一处理请求参数校验(实体对象传参)
     *
     * @param e BindException
     * @return FebsResponse
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response validExceptionHandler(BindException e) {
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            message.append(error.getField()).append(error.getDefaultMessage()).append(",");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return new Response().message(message.toString());
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleUnauthorizedException(Exception e) {
        log.error("权限不足，{}", e.getMessage());
    }

}
