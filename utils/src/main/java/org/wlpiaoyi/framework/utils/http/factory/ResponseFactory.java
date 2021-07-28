package org.wlpiaoyi.framework.utils.http.factory;

import lombok.NonNull;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.EntityUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


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
                hr.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }

        if(request.getUrl().startsWith(HttpFactory.SCHEME_HTTPS))
            return ResponseFactory.HttpsResponse(hr, request);
        else
            return ResponseFactory.HttpResponse(hr, request);

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
                hr.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        HttpEntity entity = HttpFactory.autoEntity(request, accept);

        if(entity != null) hr.setEntity(entity);

        if(request.getUrl().startsWith(HttpFactory.SCHEME_HTTPS))
            return ResponseFactory.HttpsResponse(hr, request);
        else
            return ResponseFactory.HttpResponse(hr, request);
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
                hr.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if(request.getUrl().startsWith(HttpFactory.SCHEME_HTTPS))
            return ResponseFactory.HttpsResponse(hr, request);
        else
            return ResponseFactory.HttpResponse(hr, request);
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
                hr.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }

        HttpEntity entity = HttpFactory.autoEntity(request, accept);
        if(entity != null) hr.setEntity(entity);

        if(request.getUrl().startsWith(HttpFactory.SCHEME_HTTPS))
            return ResponseFactory.HttpsResponse(hr, request);
        else
            return ResponseFactory.HttpResponse(hr, request);
    }


    //BaseResponse===>
    /**
     *
     * @param rq
     * @param request
     * @return
     * @throws IOException
     */
    static HttpResponse HttpResponse(HttpRequestBase rq, @NonNull Request request) throws IOException {
        rq.setConfig(HttpFactory.createConfig(request.getProxy()).build());
        for (Object obj : request.getHeaders().entrySet()){
            Map.Entry<String, String> entry= (Map.Entry<String, String>) obj;
            rq.addHeader(entry.getKey(), entry.getValue());
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
    static HttpResponse HttpsResponse(HttpRequestBase rq, @NonNull Request request) throws IOException {
        rq.setConfig(HttpFactory.createConfig(request.getProxy()).build());
        for (Object obj : request.getHeaders().entrySet()){
            Map.Entry<String, String> entry= (Map.Entry<String, String>) obj;
            rq.addHeader(entry.getKey(), entry.getValue());
        }

        CloseableHttpResponse response = HttpFactory.getHttpsClient().execute(rq, request.getContext());
        return response;
    }
    //BaseResponse<===


    public static <T> Response<T> ResponseData(HttpResponse rp, Class<T> clazz) throws IOException {
        if(rp == null) return null;
        Response<String> responseText = ResponseFactory.ResponseText(rp);

        Response response = new Response<T>().setCookies(responseText.getCookies()).setHeaders(responseText.getHeaders());
        if(ValueUtils.isBlank(responseText.getBody())){
            return response;
        }

        if(clazz == String.class){
            return response.setBody(responseText.getBody());
        }
        HttpEntity entity = rp.getEntity();
        if (entity == null) return response;

        if(entity.getContentType().getValue().contains("application/json")){
            return response.setBody(HttpFactory.GSON.fromJson(responseText.getBody(), clazz));
        }
        return response;
    }

    public static Response<String> ResponseText(HttpResponse rp) throws IOException {
        if(rp == null) return null;
        HttpEntity entity = rp.getEntity();
        if (entity == null) return null;
        Map<String, String> headers = new HashMap<>();
        for (Header header : rp.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
        return new Response<>().setBody(EntityUtils.toString(entity)).setHeaders(headers);
    }


}
