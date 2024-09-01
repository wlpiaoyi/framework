package org.wlpiaoyi.framework.utils.exception;

import lombok.Getter;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    系统异常 必须catch
 * {@code @date:}           2022/11/18 18:48
 * {@code @version:}:       1.0
 */
@Getter
public class SystemException extends Exception{

    protected int code;

    protected String message;

    public SystemException(String message) {
        super(message);
        this.code = 502;
        this.message = message;
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = 502;
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
