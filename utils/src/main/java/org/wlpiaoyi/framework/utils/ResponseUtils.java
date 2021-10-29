package org.wlpiaoyi.framework.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.CatchException;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class ResponseUtils {

    @Data
    public static class ResponseMessage{

        private int code;

        private String message;

    }

    @Data
    public static class ResponseData<T>{

        private int code;

        private T data;

        private String message;

    }

    @Data
    public static class ResponseException{

        private int code;

        private String message;

        private Exception exception;

    }

    public static <T> ResponseData getResponseSuccess(T data){
        return getResponseData(200, data);
    }

    public static <T> ResponseData getResponseData(int code, T data){
        ResponseData responseData = new ResponseData<T>();
        responseData.code = code;
        responseData.message = "SUCCESS";
        responseData.data = data;
        return responseData;
    }
    public static ResponseMessage getResponseMessage(int code, String message){
        ResponseMessage responseData = new ResponseMessage();
        responseData.code = code;
        responseData.message = message;
        return responseData;
    }

    public static ResponseException getResponseException(Exception e){
        if(e instanceof BusinessException){
            BusinessException bEx = (BusinessException) e;
            return getResponseException(bEx.getCode(), bEx.getMessage(), bEx);
        }
        if(e instanceof CatchException){
            CatchException cEx = (CatchException) e;
            return getResponseException(cEx.getCode(), cEx.getMessage(), cEx);
        }
        return getResponseException(500, e.getMessage(), e);
    }

    public static ResponseException getResponseException(int code, String message, Exception e){
        ResponseException responseData = new ResponseException();
        responseData.exception = e;
        responseData.code = code;
        responseData.message = message;
        return responseData;
    }

    public static void writeResponseJson(@Nullable Object json, int code, @NonNull HttpServletResponse response) throws IOException {
        response.setHeader("content-type", "application/json;charset=utf-8");
        ResponseUtils.writeResponseData(json, "application/json;charset=utf-8", code, response);
    }

    public static void writeResponseData(@Nullable Object data, @NonNull String contentType, int code,
                                         @NonNull HttpServletResponse response) throws IOException {
        String repStr;
        if(data != null){
            if(data instanceof String){
                repStr = (String) data;
            }else if(data instanceof StringBuffer){
                repStr = ((StringBuffer) data).toString();
            }else if(data instanceof StringBuilder){
                repStr = ((StringBuilder) data).toString();
            }else {
                repStr = new Gson().toJson(data);
            }
        }else{
            repStr = "";
        }
        response.setStatus(code);
        response.setContentType(contentType);
        response.getWriter().write(repStr);
    }

}
