package org.wlpiaoyi.framework.utils.exception;


import lombok.Getter;

/**
 * <p<b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * 业务异常,可以不用catch
 * </p>
 * <p><b>{@code @date:}</b>2022/11/18 18:48</p>
 * <p><b>{@code @version:}</b>1.0</p>
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
        this.code = ErrorDefine.BIZ_BASE_ERROR_CODE;
        this.message = String.format(message, params);
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ErrorDefine errorDefine, Throwable cause) {
        super(errorDefine.getMessage(), cause);
        this.code = errorDefine.getCode();
        this.message = errorDefine.getMessage();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ErrorDefine errorDefine) {
        super(errorDefine.getMessage());
        this.code = errorDefine.getCode();
        this.message = errorDefine.getMessage();
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorDefine.BIZ_BASE_ERROR_CODE;
        this.message = message;
    }

    public BusinessException(Throwable throwable) {
        super(throwable);
        this.code = ErrorDefine.BIZ_BASE_ERROR_CODE;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ErrorDefine.BIZ_BASE_ERROR_CODE;
        this.message = message;
    }



}
