package org.wlpiaoyi.framework.utils.http.request;

import lombok.Getter;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.*;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.http.HttpMessage;
import org.wlpiaoyi.framework.utils.http.factory.HttpFactory;
import org.wlpiaoyi.framework.utils.http.HttpUtils;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>请求实体</p>
 * <p><b>{@code @date:}</b>2024-09-23 07:53:39</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class Request<T> implements HttpMessage<T> {

    /**
     * Http-Method
     */
    public enum Method {
        Get,Post,Delete,Put,Patch
    }

    @Getter
    private final HttpClientContext context;

    @Getter
    private final String url;

    @Getter
    private final Map<String, String> headers;

    @Getter
    private final Method method;

    @Getter
    private HttpHost httpProxy;

    @Getter
    private Map<String, String> params;

    @Getter
    private T body;

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>context</b>
     * {@link HttpClientContext}
     * </p>
     *
     * <p><b>@param</b> <b>url</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>headers</b>
     * {@link Map<String, String>}
     * </p>
     *
     * <p><b>@param</b> <b>method</b>
     * {@link Method>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 9:36</p>
     * <p><b>{@code @return:}</b>{@link }</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Request(HttpClientContext context, String url, Map<String, String> headers, Method method){
        this.context = context;
        this.url = url;
        this.headers = headers;
        this.method = method;
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>context</b>
     * {@link HttpClientContext}
     * </p>
     *
     * <p><b>@param</b> <b>url</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>method</b>
     * {@link Method}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 19:37</p>
     * <p><b>{@code @return:}</b>{@link }</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Request(HttpClientContext context, String url, Method method){
        this.context = context;
        this.url = url;
        this.headers = HttpUtils.HEADER_JSON_DEFAULTS();
        this.method = method;
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     * 
     * <p><b>@param</b> <b>context</b>
     * {@link HttpClientContext}
     * </p>
     * 
     * <p><b>@param</b> <b>url</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 20:34</p>
     * <p><b>{@code @return:}</b>{@link }</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Request(HttpClientContext context, String url){
        this.context = context;
        this.url = url;
        this.headers = HttpUtils.HEADER_FORM_DEFAULTS();
        this.method = Method.Get;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 执行请求
     * </p>
     *
     * <p><b>@param</b> <b>tClass</b>
     * {@link Class<T>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 19:37</p>
     * <p><b>{@code @return:}</b>{@link Response<T>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Response<T> execute(Class<T> tClass) throws IOException {
        String executeUrl;
        if(ValueUtils.isNotBlank(this.params)){
            executeUrl = HttpUtils.urlMergePatterns(this.url, this.params);
        }else executeUrl = this.url;
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if(this.httpProxy != null){
            httpClientBuilder.setProxy(this.httpProxy);
        }
        return httpClientBuilder.build().execute(HttpFactory.getHttpRequest(executeUrl, this),
                context, response -> HttpFactory.handleResponse(response, tClass));
    }


    /**
     * <p><b>{@code @description:}</b>
     * 添加Header
     * </p>
     *
     * <p><b>@param</b> <b>key</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>value</b>
     * {@link Object}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 9:36</p>
     * <p><b>{@code @return:}</b>{@link Request}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Request<T> setHeader(String key, String value){
        this.headers.put(key, value);
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置请求数据主体
     * </p>
     *
     * <p><b>@param</b> <b>body</b>
     * {@link T}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 20:34</p>
     * <p><b>{@code @return:}</b>{@link Request<T>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Request<T> setBody(T body){
        this.body = body;
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置请求参数
     * </p>
     *
     * <p><b>@param</b> <b>params</b>
     * {@link Map<String, String>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 20:35</p>
     * <p><b>{@code @return:}</b>{@link Request<T>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Request<T> setParams(Map<String, String> params){
        this.params = params;
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置请求参数
     * </p>
     *
     * <p><b>@param</b> <b>name</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>value</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 20:36</p>
     * <p><b>{@code @return:}</b>{@link Request<T>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Request<T> setParam(String name, String value){
        if(this.params == null){
            this.params = new HashMap<>();
        }
        this.params.put(name, value);
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置代理
     * </p>
     *
     * <p><b>@param</b> <b>httpProxy</b>
     * {@link HttpHost}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 20:36</p>
     * <p><b>{@code @return:}</b>{@link Request<T>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Request<T> setHttpProxy(HttpHost httpProxy) {
        this.httpProxy = httpProxy;
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置代理
     * </p>
     *
     * <p><b>@param</b> <b>hostname</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>port</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 20:36</p>
     * <p><b>{@code @return:}</b>{@link Request<T>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public Request<T> setHttpProxy(final String hostname, final int port) {
        this.httpProxy = new HttpHost(hostname, port);
        return this;
    }



    private String scheme;
    private String domain;
    private int port;
    private String path;

    public String getScheme() {
        if(ValueUtils.isBlank(this.scheme)){
            this.scheme = HttpUtils.schemeFromUrl(this.url);
        }
        return scheme;
    }
    public String getDomain() {
        if(ValueUtils.isBlank(this.domain)){
            this.domain = HttpUtils.domainFromUrl(this.url);
        }
        return domain;
    }
    public int getPort() {
        if(ValueUtils.isBlank(this.port)){
            this.port = HttpUtils.portFromUrl(this.url);
        }
        return port;
    }
    public String getPath() {
        if(ValueUtils.isBlank(this.path)){
            this.path = HttpUtils.pathFromUrl(this.url);
        }
        return path;
    }
}
