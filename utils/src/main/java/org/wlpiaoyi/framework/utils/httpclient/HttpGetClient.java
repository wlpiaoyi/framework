package org.wlpiaoyi.framework.utils.httpclient;

import lombok.NonNull;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpGetClient extends HttpClient{


    /**
     * GET同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Map Map(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap) throws URISyntaxException, IOException {
        return HttpGetClient.Map(url, headerMap, paramMap, null);
    }
    /**
     * GET同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Map Map(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        HttpResponse response = HttpGetClient.Response(url, headerMap, paramMap, proxy);
        return HttpClient.getResponseMap(response);
    }

    /**
     * GET同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String String(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap) throws URISyntaxException, IOException {
        return HttpGetClient.String(url, headerMap, paramMap, null);
    }

    /**
     * GET同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String String(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        return HttpGetClient.Data(url, headerMap, paramMap, String.class, proxy);
    }

    /**
     * GET同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param clazz
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T Data(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @NonNull Class<T> clazz) throws URISyntaxException, IOException {
        return HttpGetClient.Data(url, headerMap, paramMap, clazz, null);
    }

    /**
     * GET同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param clazz
     * @param <T>
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T Data(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @NonNull Class<T> clazz, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        HttpResponse response = HttpGetClient.Response(url, headerMap, paramMap, proxy);
        return HttpClient.getResponseData(response, clazz);
    }

    /**
     * GET同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static HttpResponse Response(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(HttpClient.createURI(url, paramMap));
        if(headerMap != null){
            for (Map.Entry<String, Object> param : headerMap.entrySet()) {
                request.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if(url.startsWith(SCHEME_HTTPS))return HttpClient.getHttpsResponse(request, proxy);
        else return HttpClient.getHttpResponse(request, proxy);
    }
}
