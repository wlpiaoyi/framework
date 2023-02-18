package org.wlpiaoyi.framework.utils.http.request;

import lombok.Getter;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.wlpiaoyi.framework.utils.http.factory.CookieFactory;
import org.wlpiaoyi.framework.utils.http.factory.HttpFactory;
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


    public final static Map<String, String> HEADER_JSON_DEFAULTS = new HashMap(){{
        put(HttpFactory.HEADER_KEY1, HttpFactory.HEADER_VALUE1_1);
        put(HttpFactory.HEADER_KEY2, HttpFactory.HEADER_VALUE2);
    }};

    public static Map<String, String> HEADER_FORM_DEFAULTS = new HashMap(){{
        put(HttpFactory.HEADER_KEY1, HttpFactory.HEADER_VALUE1_2);
        put(HttpFactory.HEADER_KEY2, HttpFactory.HEADER_VALUE2);
    }};


    /**
     * Http-Method
     */
    public enum Method {
        Get,Post,Delete,Put
    }

    @Getter
    private final Map<String, Object> headers;

    @Getter
    private T body;

    @Getter
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
            String host = this.url.split("//")[1].split("/")[0].split(":")[0];
            this.host = host;
        }
        return host;
    }

    public static Request initForm(String url){
        Request request = new Request(url);
        for (Map.Entry<String, String> entry : HEADER_FORM_DEFAULTS.entrySet()){
            if(request.getHeaders().containsKey(entry.getKey())) {
                continue;
            }
            request.getHeaders().put(entry.getKey(), entry.getValue());
        }
        return request;
    }



    public static Request initJson(String url){
        Request request = new Request(url);
        for (Map.Entry<String, String> entry : HEADER_JSON_DEFAULTS.entrySet()){
            if(request.getHeaders().containsKey(entry.getKey())) {
                continue;
            }
            request.getHeaders().put(entry.getKey(), entry.getValue());
        }
        return request;
    }


    private Request(String url){
        this.headers = new HashMap<>();
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

    public Request setCookie(String key, String value){
        CookieFactory.setCookie(this.getCookieStore(), new BasicClientCookie(key, value));
        return this;
    }

    public Request removeCookie(String name){
        CookieFactory.removeCookie(this.getCookieStore(),
                CookieFactory.getCookie(this.getCookieStore(), name));
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

    public Request setParam(String name, String value){
        if(this.params == null){
            this.params = new HashMap<>();
        }
        this.params.put(name, value);
        return this;
    }


    public Request setProxy(HttpHost host){
        this.proxy = host;
        return this;
    }
    public Request setProxy(String host, int port){
        if(ValueUtils.isBlank(host)){
            this.proxy = null;
        }else {
            this.proxy = new HttpHost(host, port);
        }
        return this;
    }

    public String getContentType(){
        if(this.getHeaders() == null || this.getHeaders().isEmpty()) {
            return null;
        }
        if(this.getHeaders().containsKey(HttpFactory.HEADER_KEY1)){
            String value = (String) this.getHeaders().get(HttpFactory.HEADER_KEY1);
            String args[] = value.split(";");
            if(args.length > 0) {
                return args[0];
            }
        }
        return null;
    }

    public String getCharset(){
        if(this.getHeaders() == null || this.getHeaders().isEmpty()) {
            return null;
        }
        String charset = null;
        if(this.getHeaders().containsKey(HttpFactory.HEADER_KEY0)){
            charset = (String) this.getHeaders().get(HttpFactory.HEADER_KEY0);
        }else if(this.getHeaders().containsKey(HttpFactory.HEADER_KEY1)){
            String value = (String) this.getHeaders().get(HttpFactory.HEADER_KEY1);
            for(String arg : value.split(";")){
                String args[] = arg.split("=");
                if(args.length == 2 && "charset".equals(args[0])){
                    charset = args[1];
                    break;
                }
            }
        }
        return charset;
    }

    public HttpContext getContext() {
        return CookieFactory.getLocationContext(this.getHost());
    }

    public CookieStore getCookieStore() {
        return CookieFactory.getLocationCookieStore(this.getHost());
    }

    public URI URI() throws URISyntaxException {
        URIBuilder ub = new URIBuilder(this.url);
        List<NameValuePair> pairs = this.covertParams2NVPS();
        if(pairs != null && pairs.size() > 0) {
            ub.setParameters(pairs);
        }
        return ub.build();
    }

    private List<NameValuePair> covertParams2NVPS(){
        if(this.params == null || this.params.isEmpty()) {
            return null;
        }
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> param : this.params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

}
