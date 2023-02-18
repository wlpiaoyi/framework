package org.wlpiaoyi.framework.utils.http.factory;

import lombok.NonNull;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.wlpiaoyi.framework.utils.http.request.Request;

import java.util.HashMap;
import java.util.Map;

public class CookieFactory {

    static Map<String, HttpContext> xHttpContextMap = new HashMap<>();
    public static HttpContext getLocationContext(@NonNull String host) {
        HttpContext xHttpContext = xHttpContextMap.get(host);
        if(xHttpContext != null) {
            return xHttpContext;
        }
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

    public static CookieStore getLocationCookieStore(@NonNull String host) {
        CookieStore cookieStore = (CookieStore) CookieFactory.getLocationContext(host).getAttribute(HttpClientContext.COOKIE_STORE);
        return cookieStore;
    }

    public static void setCookie(@NonNull CookieStore cookieStore, @NonNull Cookie cookie){
        Cookie c = CookieFactory.getCookie(cookieStore, cookie.getName());
        if(c != null) {
            CookieFactory.removeCookie(cookieStore,c);
        }
        cookieStore.addCookie(cookie);
    }
    public static Cookie getCookie(@NonNull CookieStore cookieStore, @NonNull String name){
        for (Cookie cooke :
                cookieStore.getCookies()) {
            if (cooke.getName().equals(name)) {
                return cooke;
            }
        }
        return null;
    }

    public static boolean removeCookie(@NonNull CookieStore cookieStore, @NonNull Cookie cookie){
        return cookieStore.getCookies().remove(cookie);
    }
}
