package org.wlpiaoyi.framework.utils.http.factory;

import lombok.NonNull;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ResponseFactory {


    /**
     * Get Response
     * @param request
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> HttpResponse GetResponse(Request<T> request) throws URISyntaxException, IOException {
        HttpGet hr = new HttpGet(request.URI());
        if(request.getHeaders() != null){
            for (Map.Entry<String, Object> param : request.getHeaders().entrySet()) {
                hr.removeHeaders(param.getKey());
                hr.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }

        if(request.getUrl().startsWith(HttpFactory.SCHEME_HTTPS)) {
            return ResponseFactory.HttpsResponse(hr, request);
        } else {
            return ResponseFactory.HttpResponse(hr, request);
        }

    }

    /**
     * Post Response
     * @param request
     * @param accept
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> HttpResponse PostResponse(@NonNull Request<T> request, String accept) throws URISyntaxException, IOException {
        HttpPost hr = new HttpPost(request.URI());
        if(request.getHeaders() != null){
            for (Map.Entry<String, Object> param : request.getHeaders().entrySet()) {
                hr.removeHeaders(param.getKey());
                hr.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        HttpEntity entity = HttpFactory.autoEntity(request, accept);

        if(entity != null) {
            hr.setEntity(entity);
        }

        if(request.getUrl().startsWith(HttpFactory.SCHEME_HTTPS)) {
            return ResponseFactory.HttpsResponse(hr, request);
        } else {
            return ResponseFactory.HttpResponse(hr, request);
        }
    }

    /**
     * Delete Response
     * @param request
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> HttpResponse DeleteResponse(Request<T> request) throws URISyntaxException, IOException {
        HttpDelete hr = new HttpDelete(request.URI());
        if(request.getHeaders() != null){
            for (Map.Entry<String, Object> param : request.getHeaders().entrySet()) {
                hr.removeHeaders(param.getKey());
                hr.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if(request.getUrl().startsWith(HttpFactory.SCHEME_HTTPS)) {
            return ResponseFactory.HttpsResponse(hr, request);
        } else {
            return ResponseFactory.HttpResponse(hr, request);
        }
    }


    /**
     * Put Response
     * @param request
     * @param accept
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> HttpResponse PutResponse(@NonNull Request<T> request, String accept) throws URISyntaxException, IOException {
        HttpPut hr = new HttpPut(request.URI());
        if(request.getHeaders() != null){
            for (Map.Entry<String, Object> param : request.getHeaders().entrySet()) {
                hr.removeHeaders(param.getKey());
                hr.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }

        HttpEntity entity = HttpFactory.autoEntity(request, accept);
        if(entity != null) {
            hr.setEntity(entity);
        }

        if(request.getUrl().startsWith(HttpFactory.SCHEME_HTTPS)) {
            return ResponseFactory.HttpsResponse(hr, request);
        } else {
            return ResponseFactory.HttpResponse(hr, request);
        }
    }


    //BaseResponse===>
    /**
     *
     * @param rq
     * @param request
     * @return
     * @throws IOException
     */
    static HttpResponse HttpResponse(HttpUriRequestBase rq, @NonNull Request request) throws IOException {
        rq.setConfig(HttpFactory.createConfig(request.getProxy()).build());
        for (Object obj : request.getHeaders().entrySet()){
            Map.Entry<String, String> entry= (Map.Entry<String, String>) obj;
            rq.removeHeaders(entry.getKey());
            rq.addHeader(entry.getKey(), entry.getValue());
        }
        StringBuilder cookies = new StringBuilder();
        for (Cookie cookie :
                request.getCookieStore().getCookies()) {
            cookies.append(cookie.getName());
            cookies.append("=");
            cookies.append(cookie.getValue());
            cookies.append(";");
        }
        if(cookies.length() > 0){
            rq.removeHeaders("cookie");
            rq.addHeader("cookie", cookies.substring(0, cookies.length() - 1));
        }
        CloseableHttpResponse response = HttpFactory.getHttpClient().execute(rq, request.getContext());
        return response;
    }


    /**
     *
     * @param rq
     * @param request
     * @return
     * @throws IOException
     */
    static HttpResponse HttpsResponse(HttpUriRequestBase rq, @NonNull Request request) throws IOException {
        rq.setConfig(HttpFactory.createConfig(request.getProxy()).build());
        for (Object obj : request.getHeaders().entrySet()){
            Map.Entry<String, String> entry= (Map.Entry<String, String>) obj;
            rq.removeHeaders(entry.getKey());
            rq.addHeader(entry.getKey(), entry.getValue());
        }
        StringBuilder cookies = new StringBuilder();
        for (Cookie cookie :
                request.getCookieStore().getCookies()) {
            cookies.append(cookie.getName());
            cookies.append("=");
            cookies.append(cookie.getValue());
            cookies.append(";");
        }
        if(!cookies.isEmpty()){
            rq.removeHeaders("cookie");
            rq.addHeader("cookie", cookies.substring(0, cookies.length() - 1));
        }
        CloseableHttpResponse response = HttpFactory.getHttpsClient().execute(rq, request.getContext());
        return response;
    }
    //BaseResponse<===


    public static <T> Response<T> ResponseData(HttpResponse rp, Class<T> clazz) throws IOException {
        return null;
//        if(rp == null) {
//            return null;
//        }
//
//        if(clazz == byte[].class){
//            Response<byte[]> response = ResponseFactory.ResponseBuffer(rp);
//            return (Response<T>) response;
//        }
//
//        if(clazz == String.class){
//            Response<String> response = ResponseFactory.ResponseText(rp);
//            return (Response<T>) response;
//        }
//
//        T body;
//        Response<String> responseText = ResponseFactory.ResponseText(rp);
//        if(rp.getEntity().getContentType().getValue().contains(HttpFactory.HEADER_APPLICATION_JSON)){
//            body = HttpFactory.GSON.fromJson(responseText.getBody(), clazz);
//        }else {
//            body = (T) responseText.getBody();
//        }
//        Response<T> response = ResponseFactory.Response(rp, body);
//        return response;
    }

    public static Response<String> ResponseText(HttpResponse rp) throws IOException {
        return null;
//        if(rp == null) {
//            return null;
//        }
//        HttpEntity entity = rp.getEntity();
//        if (entity == null) {
//            return null;
//        }
//        String body = EntityUtils.toString(entity);
//        return ResponseFactory.Response(rp, body);
    }

    public static Response<byte[]> ResponseBuffer(HttpResponse rp) throws IOException {
        return null;
//        if(rp == null) {
//            return null;
//        }
//        HttpEntity entity = rp.getEntity();
//        if (entity == null) {
//            return null;
//        }
//        byte[] buffer = EntityUtils.toByteArray(entity);
//        return ResponseFactory.Response(rp, buffer);
    }

    public static <T> Response<T> Response(HttpResponse rp, T body){
        return null;
//        if(rp == null) {
//            return null;
//        }
//        HttpEntity entity = rp.getEntity();
//        if (entity == null) {
//            return null;
//        }
//        Set<Header> headers = new HashSet<>();
//        for (Header header : rp.getAllHeaders()) {
//            headers.add(header);
//        }
//        return new Response<T>().setBody(body)
//                .setHeaders(headers)
//                .setStatusCode(rp.getStatusLine().getStatusCode());
    }


}
