package org.wlpiaoyi.framework.utils.http.request;

import lombok.Getter;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求体
 * @author wlpiaoyi
 * @param <T>
 */
public class Request<T> {

    private final static Map<String, String> HEDAER_JSON_DEFAULTS = new HashMap(){{
        put("Content-Type", "application/json;charset=UTF-8");
        put("Accept", "application/json");
    }};

    private static Map<String, String> HEDAER_FORM_DEFAULTS = new HashMap(){{
        put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        put("Accept", "application/json");
    }};


    public enum Method {
        Get,Post,Delete,Put
    }

    @Getter
    private final Map<String, Object> headers;

    @Getter
    private final List<Cookie> cookies;

    @Getter
    private T body;

    private Map<String, String> params;

    @Getter
    private Method method;

    @Getter
    private String url;

    @Getter
    private HttpHost proxy;

    private String host;

    public String getHost(){
        if(this.host == null){
            String host = this.url.substring(this.url.indexOf(':'));
            int index = host.indexOf('/');
            if(index < 0) return host;
            host = host.substring(index);
            this.host = host;
        }
        return host;
    }

    public static Request initForm(String url){
        Request request = new Request(url);
        for (Map.Entry<String, String> entry : HEDAER_FORM_DEFAULTS.entrySet()){
            if(request.getHeaders().containsKey(entry.getKey())) continue;
            request.getHeaders().put(entry.getKey(), entry.getValue());
        }
        return request;
    }



    public static Request initJson(String url){
        Request request = new Request(url);
        for (Map.Entry<String, String> entry : HEDAER_JSON_DEFAULTS.entrySet()){
            if(request.getHeaders().containsKey(entry.getKey())) continue;
            request.getHeaders().put(entry.getKey(), entry.getValue());
        }
        return request;
    }

    private Request(String url){
        this.headers = new HashMap<>();
        this.cookies = new ArrayList<>();
        this.method = Method.Get;
        this.url = url;
    }

    public Request setMethod(Method method){
        this.method = method;
        return this;
    }
    public Request setHeader(String key, Object value){
        this.headers.put(key, value);
        return this;
    }

    public Request addCookie(Cookie cookie){
        this.cookies.add(cookie);
        this.getCookieStore().addCookie(cookie);
        return this;
    }

    public Request removeCookie(String name){
        CookieStore cookieStore = this.getCookieStore();
        for (Cookie cookie : cookieStore.getCookies()){
            if(cookie.getName().equals(name)){
                this.getCookies().remove(cookie);
                break;
            }
        }
        for (Cookie cookie : this.getCookies()){
            if(cookie.getName().equals(name)){
                this.getCookies().remove(cookie);
                break;
            }
        }


        return this;
    }

    public Request setBody(T body){
        this.body = body;
        return this;
    }

    public Request setParams(Map params){
        this.params = params;
        return this;
    }

    public Request setParams(String name, String value){
        if(this.params == null){
            this.params = new HashMap<>();
        }
        this.params.put(name, value);
        return this;
    }


    public Request setProxy(HttpHost host){
        if(ValueUtils.isBlank(host)){
            this.proxy = null;
        }else this.proxy = host;
        return this;
    }
    public Request setProxy(String host, int port){
        if(ValueUtils.isBlank(host)){
            this.proxy = null;
        }else this.proxy = new HttpHost(host, port);
        return this;
    }

    public String getContentType(){
        if(this.getHeaders() == null || this.getHeaders().isEmpty()) return null;
        if(this.getHeaders().containsKey("Content-Type")){
            String value = (String) this.getHeaders().get("Content-Type");
            String args[] = value.split(";");
            if(args.length > 0) return args[0];
        }
        return null;
    }

    public String getCharset(){
        if(this.getHeaders() == null || this.getHeaders().isEmpty()) return null;
        String charset = null;
        if(this.getHeaders().containsKey("Content-Encoding")){
            charset = (String) this.getHeaders().get("Content-Encoding");
        }else if(this.getHeaders().containsKey("Content-Type")){
            String value = (String) this.getHeaders().get("Content-Type");
            for(String arg : value.split(";")){
                String args[] = arg.split("=");
                if(args.length == 2 && args[0].equals("charset")){
                    charset = args[1];
                    break;
                }
            }
        }
        return charset;
    }

    static Map<String, HttpContext> xHttpContextMap = new HashMap<>();
    public static HttpContext getLocationContext(String host) {
        HttpContext xHttpContext = xHttpContextMap.get(host);
        if(xHttpContext != null) return xHttpContext;
        synchronized (Request.class){
            if(xHttpContext == null){
                CookieStore cookieStore = new BasicCookieStore();
                HttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
                xHttpContext = localContext;
                xHttpContextMap.put(host, xHttpContext);
            }
        }
        return xHttpContext;
    }

    public HttpContext getContext() {
        return Request.getLocationContext(this.getHost());
    }

    public CookieStore getCookieStore() {
        CookieStore cookieStore = (CookieStore) this.getContext().getAttribute(HttpClientContext.COOKIE_STORE);
        return cookieStore;
    }

    public URI URI() throws URISyntaxException {
        URIBuilder ub = new URIBuilder(this.url);
        List<NameValuePair> pairs = this.covertParams2NVPS();
        if(pairs != null && pairs.size() > 0) ub.setParameters(pairs);
        return ub.build();
    }

    private List<NameValuePair> covertParams2NVPS(){
        if(this.params == null || this.params.isEmpty()) return null;
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> param : this.params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

}
