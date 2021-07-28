package org.wlpiaoyi.framework.utils.http;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.http.factory.ResponseFactory;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.IOException;
import java.net.URISyntaxException;

public class HttpClient<T> {


    @Getter
    private Request<T> request;

    @Getter
    private Class<T> rpClazz;

    @Getter
    public String rpAccept;

    private HttpClient(Request request){
        this.rpAccept = "application/json";
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
        switch (this.request.getMethod()){
            case Post:
                return this.PostResponse();
            case Delete:
                return this.DeleteResponse();
            case Put:
                return this.PutResponse();
            default:
                return this.GetResponse();
        }
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
