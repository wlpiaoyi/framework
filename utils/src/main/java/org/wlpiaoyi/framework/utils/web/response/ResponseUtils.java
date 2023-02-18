package org.wlpiaoyi.framework.utils.web.response;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wlpia
 */
public class ResponseUtils {


    private static final String CONTENT_TYPE_KEY = "content-type";
    private static final String CONTENT_TYPE_VALUE_JSON = "application/json;charset=utf-8";
    public static void writeResponseJson(@Nullable Object json,
                                         int code,
                                         @NonNull HttpServletResponse response) throws IOException {
        response.setHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE_JSON);
        ResponseUtils.writeResponseData(json, CONTENT_TYPE_VALUE_JSON, code, response);
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
