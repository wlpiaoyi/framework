package org.wlpiaoyi.framework.utils.exception;


import lombok.Getter;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    业务异常,可以不用catch
 * {@code @date:}           2022/11/18 18:48
 * {@code @version:}:       1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    protected int code;

    protected String message;

    public BusinessException(Throwable throwable) {
        super(throwable);
    }

    public BusinessException(String message) {
        super(message);
        this.code = 501;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 501;
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }



}
