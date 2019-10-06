package org.wlpiaoyi.framework.utils.httpclient;


import com.google.gson.Gson;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpClient {

    public enum Method {
        GET, POST, PUT, DELETE
    }

    public static String getResponseText(Response response) throws IOException {
        if(response == null) return null;
        byte[] bytes = response.body().bytes();
        String text = new String(bytes);
        return text;
    }

    public static Object getResponseData(Response response) throws IOException {
        if(response == null) return null;
        String text = HttpClient.getResponseText(response);
        if(response.header("Content-Type").contains("application/json")){
            Gson gson = new Gson();
            return gson.fromJson(text, Map.class);
        }
        return text;
    }

    /**
     * GET同步请求
     * @param headerMap
     * @param url
     * @return
     */
    public static Response syncGETResponse(Map<String, String> headerMap, String url) {
        Request request = createRequest(Method.GET, headerMap, url, null);
        return syncResponse(request, null, -1);
    }

    /**
     * POST-Form同步请求
     * @param headerMap
     * @param url
     * @param params
     * @return
     */
    public static Response syncPOSTFormResponse(Map<String, String> headerMap, String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        RequestBody bodyData = builder.build();
        Request request = createRequest(Method.POST, headerMap, url, bodyData);
        return syncResponse(request, null, -1);
    }

    /**
     * POST-Json同步请求
     * @param headerMap
     * @param url
     * @param params
     * @return
     */
    public static Response syncPOSTJsonResponse(Map<String, String> headerMap, String url, Object params) {
        Gson gson = new Gson();
        String json = gson.toJson(params);
        RequestBody bodyData = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);
        Request request = createRequest(Method.POST, headerMap, url, bodyData);
        return syncResponse(request, null, -1);
    }

    /**
     * PUT同步请求
     * @param headerMap
     * @param url
     * @param requestBody
     * @return
     */
    public static Response syncPUTResponse(Map<String, String> headerMap, String url, RequestBody requestBody) {
        Request request = createRequest(Method.PUT, headerMap, url, requestBody);
        return syncResponse(request, null, -1);
    }

    /**
     * PUT同步请求
     * @param headerMap
     * @param url
     * @param requestBody
     * @return
     */
    public static Response syncDELETEResponse(Map<String, String> headerMap, String url, RequestBody requestBody) {
        Request request = createRequest(Method.DELETE, headerMap, url, requestBody);
        return syncResponse(request, null, -1);
    }

    /**
     * 同步请求
     * @param request
     * @param proxyIp
     * @param proxyPort
     * @return
     */
    private static Response syncResponse(Request request, String proxyIp, int proxyPort) {
        try {
            OkHttpClient httpClient = HttpClient.createClient(request, proxyIp, proxyPort);
            Response response = httpClient.newCall(request).execute();
            if (response != null) {
                return response;
            }
        } catch (SocketTimeoutException e1){
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 创建连接端
     * @param request
     * @param proxyIp
     * @param proxyPort
     * @return
     */
    private static final OkHttpClient createClient(Request request, String proxyIp, int proxyPort){
        OkHttpClient.Builder build;
        if(request.url().isHttps()){
            build = new OkHttpClient.Builder().sslSocketFactory(createSSLSocketFactory(), new TrustAllManager());
        }else{
            build = new OkHttpClient.Builder();
        }
        if(proxyIp != null && proxyIp.length() > 0){
            build.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, proxyPort)));
        }
        OkHttpClient httpClient = build.build();

        return  httpClient;
    }

    /**
     *
     * @param method
     * @param headerMap
     * @param url
     * @param requestBody
     * @return
     */
    private static Request createRequest(Method method, Map<String, String> headerMap, String url, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder().url(url);
        if (headerMap != null && headerMap.size() > 0) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        switch (method){
            case GET:{
                builder.get();
            }
            break;
            case POST:{
                builder.post(requestBody);
            }
            break;
            case PUT:{
                builder.put(requestBody);
            }
            break;
            case DELETE:{
                builder.delete(requestBody);
            }
            break;
        }
        Request request = builder.build();
        return request;
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
