package org.wlpiaoyi.framework.utils.exception;

import lombok.Getter;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    可以捕获的异常
 * {@code @date:}           2022/11/18 18:48
 * {@code @version:}:       1.0
 */
@Getter
public class CatchException extends Exception{

    protected int code;

    protected String message;

    public CatchException(String message) {
        super(message);
        this.code = -1;
        this.message = message;
    }

    public CatchException(String message, Throwable cause) {
        super(message, cause);
        this.code = -1;
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
