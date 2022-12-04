package org.wlpiaoyi.framework.mybatis.utils.http;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.wlpiaoyi.framework.mybatis.utils.exception.BusinessException;
import org.wlpiaoyi.framework.mybatis.utils.http.factory.CookieFactory;
import org.wlpiaoyi.framework.mybatis.utils.http.factory.HttpFactory;
import org.wlpiaoyi.framework.mybatis.utils.http.factory.ResponseFactory;
import org.wlpiaoyi.framework.mybatis.utils.http.request.Request;
import org.wlpiaoyi.framework.mybatis.utils.http.response.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class HttpClient<T> {

    @Getter
    private Request<T> request;

    @Getter
    private Class<T> rpClazz;

    @Getter
    public String rpAccept;

    private HttpClient(Request request){
        this.rpAccept = HttpFactory.HEADER_APPLICATION_JSON;
        this.request = request;
    }

    public static HttpClient instance(Request request){
        return new HttpClient(request);
    }

    public HttpClient setRpClazz(Class<T> rpClazz){
        this.rpClazz = rpClazz;
        return this;
    }

    public HttpClient setRpAccept(String rpAccept){
        this.rpAccept = rpAccept;
        return this;
    }



    @SneakyThrows
    public Response<T> response(){
        Response<T> rp;
        switch (this.request.getMethod()){
            case Post:
                rp = this.PostResponse();
                break;
            case Delete:
                rp = this.DeleteResponse();
                break;
            case Put:
                rp = this.PutResponse();
                break;
            default:
                rp = this.GetResponse();
                break;
        }
        Set<Cookie> cookies = new HashSet<>();
        String setCookieKey = HttpFactory.HEADER_KEY3.toUpperCase();
        for (Header header :
                rp.getHeaders()) {
            if(!header.getName().toLowerCase().equals(setCookieKey)) continue;
            String[] keyValues =  header.getValue().split(";");
            for (String keyValue :
                    keyValues) {
                String name = keyValue.split("=")[0];
                Cookie cookie = CookieFactory.getCookie(request.getCookieStore(), name);
                if(cookie != null) cookies.add(cookie);
            }
        }
        rp.setCookies(cookies);
        return rp;
    }

    public Response<T> GetResponse() throws IOException, URISyntaxException {
        if(this.request == null) throw new BusinessException("request is null");
        if(this.rpClazz == null) throw new BusinessException("rpClazz is null");
        HttpResponse rp = ResponseFactory.GetResponse(this.request);
        Response<T> response = ResponseFactory.ResponseData(rp, this.rpClazz);
        return response;
    }

    public Response<T> PostResponse() throws IOException, URISyntaxException {
        if(this.request == null) throw new BusinessException("request is null");
        if(this.rpClazz == null) throw new BusinessException("rpClazz is null");
        HttpResponse rp = ResponseFactory.PostResponse(this.request, this.rpAccept);
        Response<T> response = ResponseFactory.ResponseData(rp, this.rpClazz);
        return response;
    }

    public Response<T> DeleteResponse() throws IOException, URISyntaxException {
        if(this.request == null) throw new BusinessException("request is null");
        if(this.rpClazz == null) throw new BusinessException("rpClazz is null");
        HttpResponse rp = ResponseFactory.DeleteResponse(this.request);
        Response<T> response = ResponseFactory.ResponseData(rp, this.rpClazz);
        return response;
    }

    public Response<T> PutResponse() throws IOException, URISyntaxException {
        if(this.request == null) throw new BusinessException("request is null");
        if(this.rpClazz == null) throw new BusinessException("rpClazz is null");
        HttpResponse rp = ResponseFactory.PutResponse(this.request, this.rpAccept);
        Response<T> response = ResponseFactory.ResponseData(rp, this.rpClazz);
        return response;
    }

}
