package org.wlpiaoyi.framework.utils;

import lombok.Data;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

public class ResponseUtils {

    @Data
    public static class ResponseData{

        private int code;

        private String message;

        private Exception exception;

        private Object data;

    }

    public static ResponseData getResponseSuccess(Object data){;
        return ResponseUtils.getResponse(200,"success", null,data);
    }

    public static  ResponseData getResponseData(int code, Object data){
        return ResponseUtils.getResponse(code, null, null, data);
    }
    public static  ResponseData getResponseMessage(int code, String message){
        return ResponseUtils.getResponse(code, message, null, null);
    }

    public static  ResponseData getResponseException(Exception e){
        if(e instanceof BusinessException){
            BusinessException bEx = (BusinessException) e;
            return ResponseUtils.getResponse(bEx.getCode(), bEx.getMessage(), null, null);
        }else{
            return ResponseUtils.getResponse(500, null, e, null);
        }
    }

    public static  ResponseData getResponseException(int code, String message, Exception e){
        return ResponseUtils.getResponse(code, message, e, null);
    }

    public static ResponseData getResponse(int code, String message, Exception e, Object data){
        ResponseData responseData = new ResponseData();
        responseData.code = code;
        responseData.message = message;
        responseData.exception = e;
        responseData.data = data;
        return responseData;
    }

}
