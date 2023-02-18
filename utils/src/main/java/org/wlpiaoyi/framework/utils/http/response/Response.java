package org.wlpiaoyi.framework.utils.http.response;

import lombok.Getter;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

import java.util.*;

public class Response <T> {

    @Getter
    private T body;

    @Getter
    private Set<Header> headers;

    @Getter
    private Set<Cookie> cookies;

    @Getter
    private int statusCode;

    public Response setBody(T body){
        this.body = body;
        return this;
    }

    public Response setHeaders(Collection<Header> headers){
        if(this.headers == null) {
            this.headers = new HashSet<>();
        }
        this.headers.addAll(headers);
        return this;
    }

    public Response removeHeaders(String name){
        Set<Header> removes = new HashSet<>();
        for (Header header :
                this.headers) {
            if (header.getName().equals(name)) {
                removes.add(header);
            }
        }
        this.headers.removeAll(removes);
        return this;
    }
    public Response removeHeader(Header header){
        this.headers.remove(header);
        return this;
    }

    public Response setStatusCode(int statusCode){
        this.statusCode = statusCode;
        return this;
    }

    public Response setCookies(Set<Cookie> cookies){
        this.cookies = cookies;
        return this;
    }

    public Cookie getCookie(String name) {
        for (Cookie cooke :
                this.cookies) {
            if(cooke.getName().equals(name)) {
                return cooke;
            }
        }
        return null;
    }
}
