package org.wlpiaoyi.framework.mybatis.utils.exception;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    protected int code;

    protected String message;

    public BusinessException(String message) {
        super(message);
        this.code = 0;
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
