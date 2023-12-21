package org.wlpiaoyi.framework.utils.exception;

import lombok.Getter;

@Getter
public class CatchException extends Exception{

    protected int code;

    protected String message;

    public CatchException(String message) {
        super(message);
        this.code = 501;
        this.message = message;
    }

    public CatchException(String message, Throwable cause) {
        super(message, cause);
        this.code = 501;
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
