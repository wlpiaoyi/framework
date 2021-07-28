package org.wlpiaoyi.framework.utils.httpclient;

import lombok.NonNull;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;


/**
 * 已废弃
 * @author wlpiaoyi
 */
@Deprecated
public class HttpPostClient extends HttpClient{


    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Map MapFormParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params) throws URISyntaxException, IOException {
        return HttpPostClient.MapFormParams(url, headerMap, params, null);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Map MapFormParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params, final HttpHost proxy) throws URISyntaxException, IOException {
        String result = HttpPostClient.StringFormParams(url, headerMap, params, proxy);
        return GSON.fromJson(result, Map.class);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Map MapJsonParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params) throws URISyntaxException, IOException {
        return HttpPostClient.MapJsonParams(url, headerMap, params, null);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Map MapJsonParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params, final HttpHost proxy) throws URISyntaxException, IOException {
        String result = HttpPostClient.StringJsonParams(url, headerMap, params, proxy);
        return GSON.fromJson(result, Map.class);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @param clazz
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T DataFormParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params, Class<T> clazz) throws URISyntaxException, IOException {
        return HttpPostClient.DataFormParams(url, headerMap, params, clazz, null);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @param clazz
     * @param proxy
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T DataFormParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params, Class<T> clazz, final HttpHost proxy) throws URISyntaxException, IOException {
        String result = HttpPostClient.StringFormParams(url, headerMap, params, proxy);
        if(clazz == null || clazz == String.class) return (T)result;
        return GSON.fromJson(result, clazz);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String StringFormParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params) throws URISyntaxException, IOException {
        return HttpPostClient.StringFormParams(url, headerMap, params, null);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String StringFormParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params, final HttpHost proxy) throws URISyntaxException, IOException {
        HttpEntity entity = HttpClient.createFormEntity(params);
        return HttpPostClient.String(url, headerMap, entity, proxy);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @param clazz
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T DataJsonParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params, Class<T> clazz) throws URISyntaxException, IOException {
        return HttpPostClient.DataJsonParams(url, headerMap, params, clazz, null);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @param clazz
     * @param proxy
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T DataJsonParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params, Class<T> clazz, final HttpHost proxy) throws URISyntaxException, IOException {
        String result = HttpPostClient.StringJsonParams(url, headerMap, params, proxy);
        if(clazz == null || clazz == String.class) return (T)result;
        return GSON.fromJson(result, clazz);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String StringJsonParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params, final HttpHost proxy) throws URISyntaxException, IOException {
        headerMap = HttpClient.createJsonHeaderMap(headerMap);
        HttpEntity entity = HttpClient.createJsonEntity(headerMap, params);
        return HttpPostClient.String(url, headerMap, entity, proxy);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String StringJsonParams(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Object params) throws URISyntaxException, IOException {
        return HttpPostClient.StringJsonParams(url, headerMap, params, null);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String String(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable HttpEntity entity) throws URISyntaxException, IOException {
        return HttpPostClient.String(url, headerMap, entity, null);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String String(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable HttpEntity entity, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        return HttpPostClient.String(url, headerMap, null, entity, proxy);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String String(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap,@Nullable HttpEntity entity, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        return HttpPostClient.Data(url, headerMap, paramMap, entity, String.class, proxy);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param clazz
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T Data(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable HttpEntity entity, @NonNull Class<T> clazz) throws URISyntaxException, IOException {
        return HttpPostClient.Data(url, headerMap, null, entity, clazz, null);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param entity
     * @param clazz
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T Data(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @Nullable HttpEntity entity, @NonNull Class<T> clazz, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        HttpResponse response = HttpPostClient.Response(url, headerMap, paramMap, entity, proxy);
        return HttpClient.getResponseData(response, clazz);
    }

    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static HttpResponse Response(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @Nullable HttpEntity entity, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        HttpPost request = new HttpPost(HttpClient.createURI(url, paramMap));
        if(headerMap != null){
            for (Map.Entry<String, Object> param : headerMap.entrySet()) {
                request.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if(entity != null) request.setEntity(entity);
        if(url.startsWith(SCHEME_HTTPS))return HttpClient.getHttpsResponse(request, proxy);
        else return HttpClient.getHttpResponse(request, proxy);
    }
}
