package org.wlpiaoyi.framework.utils.http.factory;

import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2024-09-22 22:14:09</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class HttpFactory {

    public static final String HEADER_CHARSET = "utf-8";
    public static final String HEADER_APPLICATION_JSON = "application/json";
    public static final String HEADER_APPLICATION_FORM = "application/x-www-form-urlencoded";
    public static final String HEADER_KEY0 = "content-encoding";
    public static final String HEADER_KEY1 = "content-type";
    public static final String HEADER_KEY2 = "accept";
    public static final String HEADER_KEY3 = "set-cookie";
    public static final String HEADER_VALUE1_1 = HEADER_APPLICATION_JSON + ";charset=" + HEADER_CHARSET;
    public static final String HEADER_VALUE1_2 = HEADER_APPLICATION_FORM + ";charset=" + HEADER_CHARSET;
    public static final String HEADER_VALUE2 = HttpFactory.HEADER_APPLICATION_JSON;

    public static Gson GSON = GsonBuilder.gsonDefault();

    public static final int TIME_OUT_MS = 120000;

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";



    public static <T> Response<T> handleResponse(ClassicHttpResponse res, Class<T> tClass) throws IOException {
        Header[] hds = res.getHeaders();
        Map<String, String> headers = HashMap.newHashMap(hds.length);
        for (Header header : hds) {
            headers.put(header.getName(), header.getValue());
        }
        T body = getResponseBody(res.getEntity(), headers, tClass);
        return new Response<>(res.getCode(), headers, body);
    }

    public static ClassicHttpRequest getHttpRequest(String executeUrl, Request<?> request) throws UnsupportedEncodingException {
        HttpUriRequestBase httpRequest;
        switch (request.getMethod()){
            case Get -> {
                httpRequest = new HttpGet(executeUrl);
            }
            case Put -> {
                HttpPut httpPut = new HttpPut(executeUrl);
                httpPut.setEntity(bodyEntity(request, null, null));
                httpRequest = httpPut;
            }
            case Post -> {
                HttpPost httpPost = new HttpPost(executeUrl);
                httpPost.setEntity(bodyEntity(request, null, null));
                httpRequest = httpPost;
            }
            case Delete -> {
                httpRequest = new HttpDelete(executeUrl);
            }
            default -> {
                httpRequest = new HttpPatch(executeUrl);
            }
        }
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(HttpFactory.TIME_OUT_MS, TimeUnit.MILLISECONDS)
                .setResponseTimeout(HttpFactory.TIME_OUT_MS, TimeUnit.MILLISECONDS).build();
        httpRequest.setConfig(requestConfig);
        if(ValueUtils.isNotBlank(request.getHeaders())){
            Set<Map.Entry<String, String>> entrySet = request.getHeaders().entrySet();
            for (Map.Entry<String, String> entry : entrySet){
                httpRequest.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpRequest;
    }

    /**
     * 创建Json
     * @param request
     * @param <T>
     * @return
     */
    public static <T> HttpEntity bodyEntity(Request<T> request, @Nullable String charset, @Nullable String accept) throws UnsupportedEncodingException {
        if(ValueUtils.isBlank(charset)){
            charset = getCharsetInHeaders(request.getHeaders());
        }
        if(ValueUtils.isBlank(charset)){
            charset = HEADER_CHARSET;
        }
        byte[] buffer;
        if(request.getBody() == null){
            buffer = null;
        }else if(request.getBody() instanceof String parameter){
            buffer = parameter.getBytes(charset);
        }else if(request.getBody() instanceof Map){
            String parameter = GSON.toJson(request.getBody(), Map.class);
            buffer = parameter.getBytes(charset);
        }else if(request.getBody() instanceof List){
            String parameter = GSON.toJson(request.getBody(), List.class);
            buffer = parameter.getBytes(charset);
        }else if(request.getBody() instanceof Set){
            String parameter = GSON.toJson(request.getBody(), Set.class);
            buffer = parameter.getBytes(charset);
        }else if((request.getBody() instanceof byte[]) || request.getBody() instanceof Byte[]){
            buffer = (byte[]) request.getBody();
        }else{
            String parameter = GSON.toJson(request.getBody());
            buffer = parameter.getBytes(charset);
        }

        if(ValueUtils.isBlank(accept)){
            accept = HttpFactory.HEADER_APPLICATION_JSON;
        }
        if(ValueUtils.isBlank(buffer)){
            buffer = new byte[0];
        }
        return new ByteArrayEntity(buffer, ContentType.create(accept, Charset.forName(charset)));
    }


    public static String getCharsetInHeaders(Map<String, String> headers){
        if(headers == null || headers.isEmpty()) {
            return HEADER_CHARSET;
        }
        String charset = HEADER_CHARSET;
        if(headers.containsKey(HttpFactory.HEADER_KEY0)){
            charset = headers.get(HttpFactory.HEADER_KEY0);
        }else if(headers.containsKey(HttpFactory.HEADER_KEY1)){
            String value = headers.get(HttpFactory.HEADER_KEY1);
            for(String arg : value.split(";")){
                String args[] = arg.split("=");
                if(args.length == 2 && "charset".equals(args[0])){
                    charset = args[1];
                    break;
                }
            }
        }
        return charset;
    }


    private static <T> T getResponseBody(HttpEntity entity, Map<String, String> headers, Class<T> tClass) throws IOException {
        String resStr = ReaderUtils.loadString(entity.getContent(), Charset.forName(HttpFactory.getCharsetInHeaders(headers)));
        if(tClass == String.class){
            return (T) resStr;
        }
        return HttpFactory.GSON.fromJson(resStr, tClass);
    }
}
