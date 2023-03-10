package org.wlpiaoyi.framework.utils.exception;

import lombok.Getter;

/**
 * @author wlpia
 */
@Getter
public class CatchException extends Exception{

    protected int code;

    protected String message;

    public CatchException(String message) {
        super(message);
        this.code = 0;
        this.message = message;
    }

    public CatchException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CatchException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
