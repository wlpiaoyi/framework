package org.wlpiaoyi.framework.utils.exception;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private int code;

    private String message;

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
