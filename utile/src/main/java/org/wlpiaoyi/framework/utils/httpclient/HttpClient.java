package org.wlpiaoyi.framework.utils.httpclient;


import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.wlpiaoyi.framework.utils.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpClient {

    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    public static final int TIME_OUT_MS = 30000;

    private static PoolingHttpClientConnectionManager CONNECTION_MANAGER;

    public static HttpResponse httpResponse(HttpRequestBase request, final HttpHost proxy) throws IOException {
        RequestConfig.Builder config = RequestConfig.copy(RequestConfig.DEFAULT);
        config.setProxy(proxy);
        config.setConnectionRequestTimeout(TIME_OUT_MS);
        config.setSocketTimeout(TIME_OUT_MS);
        request.setConfig(config.build());
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = httpClient.execute(request);
        return response;
    }

    public static HttpResponse httpsResponse(HttpRequestBase request, final HttpHost proxy) throws IOException {
        RequestConfig.Builder config = RequestConfig.copy(RequestConfig.DEFAULT);
        config.setProxy(proxy);
        config.setConnectionRequestTimeout(TIME_OUT_MS);
        config.setSocketTimeout(TIME_OUT_MS);
        request.setConfig(config.build());
        CloseableHttpResponse response = getHttpsClient().execute(request);
        return response;
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
    public static <T> T GETData(String url, Map<String, Object> headerMap, Map<String, Object> paramMap, Class<T> clazz) throws URISyntaxException, IOException {
        HttpResponse response = HttpClient.GETResponse(url, headerMap, paramMap, null);
        return HttpClient.getResponseData(response, clazz);
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
    public static HttpResponse GETResponse(String url, Map<String, Object> headerMap, Map<String, Object> paramMap) throws URISyntaxException, IOException {
        return HttpClient.GETResponse(url, headerMap, paramMap, null);
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
    public static HttpResponse GETResponse(String url, Map<String, Object> headerMap, Map<String, Object> paramMap,final HttpHost proxy) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(HttpClient.createURI(url, paramMap));
        if(headerMap != null){
            for (Map.Entry<String, Object> param : headerMap.entrySet()) {
                request.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if(url.startsWith(SCHEME_HTTPS))return HttpClient.httpsResponse(request, proxy);
        else return HttpClient.httpResponse(request, proxy);
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
    public static <T> T DELETEData(String url, Map<String, Object> headerMap, Map<String, Object> paramMap, Class<T> clazz) throws URISyntaxException, IOException {
        HttpResponse response = HttpClient.DELETEResponse(url, headerMap, paramMap, null);
        return HttpClient.getResponseData(response, clazz);
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
    public static HttpResponse DELETEResponse(String url, Map<String, Object> headerMap, Map<String, Object> paramMap) throws URISyntaxException, IOException {
        return HttpClient.DELETEResponse(url, headerMap, paramMap, null);
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
    public static HttpResponse DELETEResponse(String url, Map<String, Object> headerMap, Map<String, Object> paramMap,final HttpHost proxy) throws URISyntaxException, IOException {
        HttpDelete request = new HttpDelete(HttpClient.createURI(url, paramMap));
        if(headerMap != null){
            for (Map.Entry<String, Object> param : headerMap.entrySet()) {
                request.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if(url.startsWith(SCHEME_HTTPS))return HttpClient.httpsResponse(request, proxy);
        else return HttpClient.httpResponse(request, proxy);
    }


    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param clazz
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T POSTData(String url, Map<String, Object> headerMap, Map<String, Object> paramMap, Class<T> clazz) throws URISyntaxException, IOException {
        HttpResponse response = HttpClient.POSTResponse(url, headerMap, paramMap, null);
        return HttpClient.getResponseData(response, clazz);
    }
    /**
     * POST同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static HttpResponse POSTResponse(String url, Map<String, Object> headerMap, Map<String, Object> paramMap) throws URISyntaxException, IOException {
        return HttpClient.POSTResponse(url, headerMap, paramMap, null);
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
    public static HttpResponse POSTResponse(String url, Map<String, Object> headerMap, Map<String, Object> paramMap,final HttpHost proxy) throws URISyntaxException, IOException {
        HttpPost request = new HttpPost(HttpClient.createURI(url, paramMap));
        if(headerMap != null){
            for (Map.Entry<String, Object> param : headerMap.entrySet()) {
                request.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if(url.startsWith(SCHEME_HTTPS))return HttpClient.httpsResponse(request, proxy);
        else return HttpClient.httpResponse(request, proxy);
    }


    /**
     * PUT同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param clazz
     * @param <T>
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static <T> T PUTData(String url, Map<String, Object> headerMap, Map<String, Object> paramMap, Class<T> clazz) throws URISyntaxException, IOException {
        HttpResponse response = HttpClient.PUTResponse(url, headerMap, paramMap, null);
        return HttpClient.getResponseData(response, clazz);
    }
    /**
     * PUT同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static HttpResponse PUTResponse(String url, Map<String, Object> headerMap, Map<String, Object> paramMap) throws URISyntaxException, IOException {
        return HttpClient.PUTResponse(url, headerMap, paramMap, null);
    }

    /**
     * PUT同步请求
     * @param url
     * @param headerMap
     * @param paramMap
     * @param proxy
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static HttpResponse PUTResponse(String url, Map<String, Object> headerMap, Map<String, Object> paramMap,final HttpHost proxy) throws URISyntaxException, IOException {
        HttpPut request = new HttpPut(HttpClient.createURI(url, paramMap));
        if(headerMap != null){
            for (Map.Entry<String, Object> param : headerMap.entrySet()) {
                request.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if(url.startsWith(SCHEME_HTTPS))return HttpClient.httpsResponse(request, proxy);
        else return HttpClient.httpResponse(request, proxy);
    }



    public static String getResponseText(HttpResponse response) throws IOException {
        if(response == null) return null;
        HttpEntity entity = response.getEntity();
        if (entity == null) return null;
        return EntityUtils.toString(entity);
    }


    public static <T> T getResponseData(HttpResponse response, Class<T> clazz) throws IOException {
        if(response == null) return null;
        String text = HttpClient.getResponseText(response);
        if(StringUtils.isBlank(text)) return null;

        if(clazz == String.class) return (T)text;

        HttpEntity entity = response.getEntity();
        if (entity == null) return null;

        if(entity.getContentType().getValue().contains("application/json")){
            Gson gson = new Gson();
            return gson.fromJson(text, clazz);
        }
        return null;
    }


    public static final void initCM(){
        if(CONNECTION_MANAGER == null){
            synchronized (HttpClient.class){
                if (CONNECTION_MANAGER == null) {
                    CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
                    CONNECTION_MANAGER.setMaxTotal(50);// 整个连接池最大连接数
                    CONNECTION_MANAGER.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
                    SocketConfig sc = SocketConfig.custom().setSoTimeout(TIME_OUT_MS).build();
                    CONNECTION_MANAGER.setDefaultSocketConfig(sc);
                }
            }
        }
    }

    /**
     * 通过连接池获取HttpClient
     *
     * @return
     */
    private static CloseableHttpClient getHttpsClient(){
        initCM();
        try {
            SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            ctx.init(null, new TrustManager[]{new TrustAllManager()}, null);
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, (s, sslSession) -> true);
            // 创建Registry
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setExpectContinueEnabled(Boolean.TRUE)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                    .setSocketTimeout(TIME_OUT_MS/5)
                    .setConnectTimeout(TIME_OUT_MS/5)
                    .setConnectionRequestTimeout(TIME_OUT_MS/8)
                    .setRedirectsEnabled(false)
                    .build();

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(SCHEME_HTTP, PlainConnectionSocketFactory.INSTANCE)
                    .register(SCHEME_HTTPS, socketFactory).build();
            // 创建ConnectionManager，添加Connection配置信息
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            return HttpClients.custom().setConnectionManager(CONNECTION_MANAGER)
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .setConnectionManagerShared(true).build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            return HttpClient.getHttpClient();
        }
    }

    /**
     * 通过连接池获取HttpClient
     *
     * @return
     */
    private static CloseableHttpClient getHttpClient(){
        initCM();
        return HttpClients.custom().setConnectionManager(CONNECTION_MANAGER)
                .setConnectionManagerShared(true).build();
    }

    private static List<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

    private static URI createURI(String url, Map<String, Object> paramMap) throws URISyntaxException {
        URIBuilder ub = new URIBuilder(url);
        if(paramMap != null){
            List<NameValuePair> pairs = covertParams2NVPS(paramMap);
            ub.setParameters(pairs);
        }
        return ub.build();
    }

    private static class TrustAllManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
