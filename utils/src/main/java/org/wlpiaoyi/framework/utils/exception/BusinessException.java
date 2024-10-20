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

    public BusinessException(int code, String message, Throwable throwable, Object... params) {
        super(throwable);
        this.code = code;
        this.message = String.format(message, params);
    }

    public BusinessException(String message, Throwable throwable, Object... params) {
        super(throwable);
        this.code = 501;
        this.message = String.format(message, params);
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public BusinessException(Error error, Throwable cause) {
        super(error.getMessage(), cause);
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(Error error) {
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 501;
        this.message = message;
    }

    public BusinessException(Throwable throwable) {
        super(throwable);
        this.code = 501;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 501;
        this.message = message;
    }



}
