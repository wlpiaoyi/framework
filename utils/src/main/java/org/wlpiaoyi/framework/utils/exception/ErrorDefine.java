package org.wlpiaoyi.framework.utils.exception;

/**
 * <p<b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * 错误定义
 * </p>
 * <p><b>{@code @date:}</b>2022/11/18 18:48</p>
 * <p><b>{@code @version:}</b>1.0</p>
 */
public interface ErrorDefine {

    /** 业务正确编码 */
    int BIZ_SUCCESS_CODE = 200;
    /** 业务基础错误编码 */
    int BIZ_BASE_ERROR_CODE = 501;
    /** 系统基础错误编码 */
    int SYS_BASE_ERROR_CODE = 502;

    int getCode();

    String getMessage();

}
