package org.wlpiaoyi.framework.utils.httpclient;


import com.google.gson.Gson;
import lombok.NonNull;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class HttpClient {

    final static Map<String, String> HEDAER_JSON_DEFAULTS = new HashMap(){{
        put("Content-Type", "application/json;charset=UTF-8");
        put("Accept", "application/json");
    }};

//    final static Map<String, String> HEDAER_FORM_DEFAULTS = new HashMap(){{
//        put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//        put("Accept", "application/json");
//    }};

    static final String SCHEME_HTTP = "http";
    static final String SCHEME_HTTPS = "https";

    static Gson GSON = new Gson();

    public static final int TIME_OUT_MS = 30000;

    private static PoolingHttpClientConnectionManager CONNECTION_MANAGER;

    static @NonNull Map<String, Object> createJsonHeaderMap(@Nullable Map<String, Object> headerMap){
        if(headerMap == null){
            headerMap = new HashMap<>();
        }
        for (Map.Entry<String, String> entry : HEDAER_JSON_DEFAULTS.entrySet()){
            if(headerMap.containsKey(entry.getKey())) continue;
            headerMap.put(entry.getKey(), entry.getValue());
        }
        return headerMap;
    }

    static @Nullable HttpEntity createFormEntity(@Nullable Object params) throws UnsupportedEncodingException {
        if(params == null) return null;
        Map<String, Object> formMap = null;
        if(params instanceof Map){
            formMap = new HashMap();
            for (Map.Entry entry : (Set<Map.Entry>)((Map) params).entrySet()){
                formMap.put(entry.getKey().toString(), entry.getValue());
            }
        }else if(params instanceof String){

        }else if(params != null){
            formMap = GSON.fromJson(GSON.toJsonTree(params), Map.class);
        }
        /// 处理请求体
        List<NameValuePair> paramsUri = new ArrayList<>();
        for (Map.Entry<String, Object> map : formMap.entrySet()) {
            paramsUri.add(new BasicNameValuePair(map.getKey(), map.getValue().toString()));
        }
        return new UrlEncodedFormEntity(paramsUri);
    }

    static HttpEntity createJsonEntity(@NonNull Map<String, Object> headerMap, @Nullable Object params) throws UnsupportedEncodingException {
        String parameter;
        if(params instanceof Map){
            Map map = new HashMap();
            for (Map.Entry entry : (Set<Map.Entry>)((Map) params).entrySet()){
                map.put(entry.getKey(), entry.getValue());
            }
            params = map;
        }
        if(params == null){
            parameter = null;
        }else if(params instanceof String){
            parameter = (String) params;
        }else{
            parameter = GSON.toJson(params);
        }

        String charset = null;
        if(headerMap.containsKey("Content-Encoding")){
            charset = (String) headerMap.get("Content-Encoding");
        }else if(headerMap.containsKey("Content-Type")){
            String value = (String) headerMap.get("Content-Type");
            for(String arg : value.split(";")){
                String args[] = arg.split("=");
                if(args.length == 2 && args[0].equals("charset")){
                    charset = args[1];
                    break;
                }
            }
        }
        if(StringUtils.isBlank(charset)){
            charset = "UTF-8";
        }
        StringEntity entity = new StringEntity(parameter != null ? parameter : "", charset);
        entity.setContentEncoding(charset);
        entity.setContentType((String)headerMap.get("Accept"));
        return entity;
    }

    public static HttpResponse getHttpResponse(HttpRequestBase request, final HttpHost proxy) throws IOException {
        request.setConfig(createConfig(proxy).build());
        CloseableHttpResponse response = getHttpClient().execute(request);
        return response;
    }

    public static HttpResponse getHttpsResponse(HttpRequestBase request, final HttpHost proxy) throws IOException {
        request.setConfig(createConfig(proxy).build());
        CloseableHttpResponse response = getHttpsClient().execute(request);
        return response;
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
            return GSON.fromJson(text, clazz);
        }
        return null;
    }


    public static Map getResponseMap(HttpResponse response) throws IOException {
        return HttpClient.getResponseData(response, Map.class);
    }



    static URI createURI(String url, Map<String, Object> paramMap) throws URISyntaxException {
        URIBuilder ub = new URIBuilder(url);
        if(paramMap != null){
            List<NameValuePair> pairs = covertParams2NVPS(paramMap);
            ub.setParameters(pairs);
        }
        return ub.build();
    }

    private static final void initCM(){
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


    private static final RequestConfig.Builder createConfig(final HttpHost proxy){
        RequestConfig.Builder config = RequestConfig.copy(RequestConfig.DEFAULT);
        config.setProxy(proxy);
        config.setConnectionRequestTimeout(TIME_OUT_MS);
        config.setSocketTimeout(TIME_OUT_MS);
        return config;
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

    private static class TrustAllManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
