package org.wlpiaoyi.framework.utils.exception;

public class CatchException extends Exception{

    private int code;

    private String message;

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
