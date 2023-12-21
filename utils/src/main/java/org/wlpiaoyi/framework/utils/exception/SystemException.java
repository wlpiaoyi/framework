package org.wlpiaoyi.framework.utils.exception;

import lombok.Getter;

/**
 * @author wlpia
 */
@Getter
public class SystemException extends RuntimeException{

    protected int code;

    protected String message;

    public SystemException(String message) {
        super(message);
        this.code = 401;
        this.message = message;
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = 401;
        this.message = message;
    }

    public SystemException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public SystemException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
