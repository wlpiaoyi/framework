package org.wlpiaoyi.framework.utils.web.response;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.CatchException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wlpia
 */
public class ResponseUtils {


    private static final String ContentTypeKey = "content-type";
    private static final String ContentTypeValueJson = "application/json;charset=utf-8";
    public static void writeResponseJson(@Nullable Object json,
                                         int code,
                                         @NonNull HttpServletResponse response) throws IOException {
        response.setHeader(ContentTypeKey, ContentTypeValueJson);
        ResponseUtils.writeResponseData(json, ContentTypeValueJson, code, response);
    }

    public static void writeResponseData(@Nullable Object data,
                                         @NonNull String contentType,
                                         int code,
                                         @NonNull HttpServletResponse response) throws IOException {
        String repStr;
        if(data != null){
            if(data instanceof String){
                repStr = (String) data;
            }else if(data instanceof StringBuffer){
                repStr = ((StringBuffer) data).toString();
            }else if(data instanceof StringBuilder){
                repStr = ((StringBuilder) data).toString();
            }else{
                repStr = GsonBuilder.gsonDefault().toJson(data);
            }
        }else{
            repStr = "";
        }
        response.setStatus(code);
        response.setContentType(contentType);
        response.getWriter().write(repStr);
    }
}
