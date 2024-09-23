package org.wlpiaoyi.framework.utils.http.response;

import lombok.Getter;
import org.wlpiaoyi.framework.utils.http.HttpMessage;

import java.util.Map;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>数据回应</p>
 * <p><b>{@code @date:}</b>2024-09-23 10:36:14</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class Response<T> implements HttpMessage<T> {


    @Getter
    private final Map<String, String> headers;

    @Getter
    private final int code;

    @Getter
    private T body;

    public Response(int code, Map<String, String> headers, T body){
        this.code = code;
        this.headers = headers;
        this.body = body;

    }

//    public String getBodyString() throws IOException {
//        return ;
//    }
//
//    public <T> T getBody(Class<T> tClass) throws IOException {
//        String resStr = getBodyString();
//        return HttpFactory.GSON.fromJson(resStr, tClass);
//    }



}
