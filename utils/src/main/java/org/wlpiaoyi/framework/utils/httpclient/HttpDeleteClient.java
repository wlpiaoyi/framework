package org.wlpiaoyi.framework.utils.httpclient;

import lombok.NonNull;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;


/**
 * 已废弃
 * @author wlpiaoyi
 */
@Deprecated
public class HttpDeleteClient extends HttpClient {


    /**
     * DELETE同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Map Map(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap) throws URISyntaxException, IOException {
        return HttpDeleteClient.Map(url, headerMap, paramMap, null);
    }

    /**
     * DELETE同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static Map Map(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        HttpResponse response = HttpDeleteClient.Response(url, headerMap, paramMap, proxy);
        return HttpDeleteClient.getResponseMap(response);
    }

    /**
     * DELETE同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String String(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap) throws URISyntaxException, IOException {
        return HttpDeleteClient.Data(url, headerMap, paramMap, String.class, null);
    }


    /**
     * DELETE同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String String(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        return HttpDeleteClient.Data(url, headerMap, paramMap, String.class, proxy);
    }


    /**
     * DELETE同步请求
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
        return HttpDeleteClient.Data(url, headerMap, paramMap, clazz, null);
    }

    /**
     * DELETE同步请求
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
        HttpResponse response = HttpDeleteClient.Response(url, headerMap, paramMap, null);
        return HttpClient.getResponseData(response, clazz);
    }

    /**
     * DELETE同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static HttpResponse Response(@NonNull String url, @Nullable Map<String, Object> headerMap, @Nullable Map<String, Object> paramMap, @Nullable final HttpHost proxy) throws URISyntaxException, IOException {
        HttpDelete request = new HttpDelete(HttpClient.createURI(url, paramMap));
        if(headerMap != null){
            for (Map.Entry<String, Object> param : headerMap.entrySet()) {
                request.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if(url.startsWith(SCHEME_HTTPS))return HttpClient.getHttpsResponse(request, proxy);
        else return HttpClient.getHttpResponse(request, proxy);
    }
}
