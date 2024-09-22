package org.wlpiaoyi.framework.utils.http4.factory;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.jetbrains.annotations.Nullable;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.http4.request.Request;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

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

    private static PoolingHttpClientConnectionManager CONNECTION_MANAGER;

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";


    static RequestConfig.Builder createConfig(final HttpHost proxy){
        RequestConfig.Builder config = RequestConfig.copy(RequestConfig.DEFAULT);
        config.setProxy(proxy)
                .setConnectionRequestTimeout(Timeout.ofMinutes(1))
                .setConnectionKeepAlive(Timeout.ofMinutes(5))
                .setResponseTimeout(Timeout.ofMinutes(1));
        return config;
    }


    /**
     * 通过连接池获取HttpClient
     *
     */
    static CloseableHttpClient getHttpClient(){
        initCM();
        return HttpClients.custom().setConnectionManager(CONNECTION_MANAGER)
                .setConnectionManagerShared(true).build();
    }

    /**
     * 通过连接池获取HttpClient
     *
     */
    static CloseableHttpClient getHttpsClient(){
        initCM();
        try {

            SSLContext ctx = SSLContext.getInstance("TLSv1.1");
            ctx.init(null, new TrustManager[]{new TrustAllManager()}, null);
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, (s, sslSession) -> true);
            // 创建Registry
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setExpectContinueEnabled(Boolean.TRUE)
                    .setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.BASIC, StandardAuthScheme.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.BASIC))
                    .setConnectionRequestTimeout(Timeout.ofMinutes(1))
                    .setConnectionKeepAlive(Timeout.ofMinutes(5))
                    .setResponseTimeout(Timeout.ofMinutes(1))
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
            return HttpFactory.getHttpClient();
        }
    }


    private static void initCM(){
        if(CONNECTION_MANAGER == null){
            synchronized (HttpFactory.class){
                if (CONNECTION_MANAGER == null) {
                    CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
                    CONNECTION_MANAGER.setMaxTotal(50);// 整个连接池最大连接数
                    CONNECTION_MANAGER.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
                    SocketConfig sc = SocketConfig.custom().setSoTimeout(Timeout.ofMinutes(5)).build();
                    CONNECTION_MANAGER.setDefaultSocketConfig(sc);
                }
            }
        }
    }


    private static class TrustAllManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


    public static <T> @Nullable HttpEntity autoEntity(@Nullable Request<T> request, String accept) throws UnsupportedEncodingException {
        switch (request.getMethod()){
            case Post:
            case Put:
                return HttpFactory.bodyEntity(request, accept);
            default:
                return HttpFactory.formEntity(request, accept);
        }
    }

    /**
     * 创建Form类型
     * @param request
     * @param <T>
     * @return
     * @throws UnsupportedEncodingException
     */
    public static <T> @Nullable HttpEntity formEntity(@Nullable Request<T> request, @Nullable String accept) throws UnsupportedEncodingException {
        if(request == null) return null;
        Map<String, Object> formMap = null;
        if(request.getBody() instanceof Map){
            formMap = new HashMap();
            for (Map.Entry entry : (Set<Map.Entry>)((Map) request.getBody()).entrySet()){
                formMap.put(entry.getKey().toString(), entry.getValue());
            }
        }else if(request.getBody() instanceof String){

        }else if(request.getBody() != null){
            formMap = GSON.fromJson(GSON.toJsonTree(request.getBody()), Map.class);
        }
        /// 处理请求体
        List<NameValuePair> paramsUri = new ArrayList<>();
        for (Map.Entry<String, Object> map : formMap.entrySet()) {
            paramsUri.add(new BasicNameValuePair(map.getKey(), map.getValue().toString()));
        }
        String charset = request.getCharset();
        if(ValueUtils.isBlank(charset)){
            charset = "UTF-8";
        }

        return new UrlEncodedFormEntity(paramsUri, Charset.forName(charset));
    }

    /**
     * 创建Json
     * @param request
     * @param accept
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> @Nullable HttpEntity bodyEntity(@Nullable Request<T> request, @Nullable String accept){

        String charset = request.getCharset();
        if(ValueUtils.isBlank(charset)){
            charset = "UTF-8";
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
}
