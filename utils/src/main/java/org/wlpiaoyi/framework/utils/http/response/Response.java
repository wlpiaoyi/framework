package org.wlpiaoyi.framework.utils.http.response;

import lombok.Data;
import lombok.Getter;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;

public class Response <T> {

    @Getter
    private T body;

    @Getter
    private Map<String, String> headers;

    @Getter
    private List<Cookie> cookies;

    public Response setBody(T body){
        this.body = body;
        return this;
    }

    public Response setHeaders(Map<String, String> headers){
        this.headers = headers;
        return this;
    }
    public Response setCookies(List<Cookie> cookies){
        this.cookies = cookies;
        return this;
    }

}
