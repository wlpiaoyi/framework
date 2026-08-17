package org.wlpiaoyi.framework.utils.exception;

import lombok.Getter;

/**
 * <p<b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * 系统异常 需要catch
 * </p>
 * <p><b>{@code @date:}</b>2022/11/18 18:48</p>
 * <p><b>{@code @version:}</b>1.0</p>
 */
@Getter
public class SystemException extends RuntimeException{

    protected int code;
    
    protected String message;

    public SystemException(int code, String message, Throwable throwable, Object... params) {
        super(throwable);
        this.code = code;
        this.message = String.format(message, params);
    }

    public SystemException(String message, Throwable throwable, Object... params) {
        super(throwable);
        this.code = ErrorDefine.SYS_BASE_ERROR_CODE;
        this.message = String.format(message, params);
    }

    public SystemException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public SystemException(ErrorDefine errorDefine, Throwable cause) {
        super(errorDefine.getMessage(), cause);
        this.code = errorDefine.getCode();
        this.message = errorDefine.getMessage();
    }

    public SystemException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public SystemException(ErrorDefine errorDefine) {
        super(errorDefine.getMessage());
        this.code = errorDefine.getCode();
        this.message = errorDefine.getMessage();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorDefine.SYS_BASE_ERROR_CODE;
        this.message = message;
    }

    public SystemException(Throwable throwable) {
        super(throwable);
        this.code = ErrorDefine.SYS_BASE_ERROR_CODE;
    }

    public SystemException(String message) {
        super(message);
        this.code = ErrorDefine.SYS_BASE_ERROR_CODE;
        this.message = message;
    }
}
