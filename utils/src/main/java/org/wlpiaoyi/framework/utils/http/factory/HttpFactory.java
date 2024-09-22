package org.wlpiaoyi.framework.utils.http.factory;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2024-08-04 10:50:40</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Slf4j
public class HttpFactory {

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER;

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);// 整个连接池最大连接数
        connectionManager.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
        SocketConfig sc = SocketConfig.custom().setSoTimeout(Timeout.ofMinutes(5)).build();
        connectionManager.setDefaultSocketConfig(sc);
        CONNECTION_MANAGER = connectionManager;
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>host</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/8/4 10:52</p>
     * <p><b>{@code @return:}</b>{@link HttpContext}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static HttpContext createLocationContext(@NonNull String host) {
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        return localContext;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 通过连接池创建HttpClient
     * </p>
     *
     * <p><b>@param</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/8/4 11:08</p>
     * <p><b>{@code @return:}</b>{@link CloseableHttpClient}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    static CloseableHttpClient createDefaultHttpClient(){
        return HttpClients.custom().setConnectionManager(CONNECTION_MANAGER)
                .setConnectionManagerShared(true).build();
    }
    /**
     * 通过连接池获取HttpClient
     *
     */
    static CloseableHttpClient createCustomHttpClient(TrustManager[] trustManagers){
        try {
            if(trustManagers == null || trustManagers.length > 0){
                trustManagers = new TrustAllManager[]{new TrustAllManager()};
            }
            SSLContext ctx = SSLContext.getInstance("TLSv1.1");
            ctx.init(null, trustManagers, null);
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, (s, sslSession) -> true);
            // 创建Registry
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setExpectContinueEnabled(Boolean.TRUE)
                    .setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.BASIC, StandardAuthScheme.DIGEST))
                    .setProxyPreferredAuthSchemes(List.of(StandardAuthScheme.BASIC))
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
            log.error("create http client error", e);
            return null;
        }
    }


    private static class TrustAllManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
