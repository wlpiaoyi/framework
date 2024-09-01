package org.wlpiaoyi.framework.utils.http.response;

import lombok.Getter;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.core5.http.Header;

import java.util.*;

@Getter
public class Response <T> {

    private T body;

    private Set<Header> headers;

    private Set<Cookie> cookies;

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
