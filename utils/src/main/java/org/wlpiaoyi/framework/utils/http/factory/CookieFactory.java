package org.wlpiaoyi.framework.utils.http.factory;

import lombok.NonNull;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.wlpiaoyi.framework.utils.ValueUtils;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2024-09-22 22:01:49</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class CookieFactory {

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>httpContext</b>
     * {@link HttpContext}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/22 22:04</p>
     * <p><b>{@code @return:}</b>{@link CookieStore}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static CookieStore loadCookieStore(@NonNull HttpContext httpContext) {
        Object value =  httpContext.getAttribute(HttpClientContext.COOKIE_STORE);
        if(value != null){
            return (CookieStore) value;
        }
        BasicCookieStore cookieStore = new BasicCookieStore();
        CookieFactory.setCookieStore(httpContext, cookieStore);
        return cookieStore;
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>httpContext</b>
     * {@link HttpContext}
     * </p>
     *
     * <p><b>@param</b> <b>cookieStore</b>
     * {@link CookieStore}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/22 22:04</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static void setCookieStore(@NonNull HttpContext httpContext, CookieStore cookieStore){
        httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>cookieStore</b>
     * {@link CookieStore}
     * </p>
     *
     * <p><b>@param</b> <b>cookieName</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>cookieValue</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/22 22:12</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static void setCookie(@NonNull CookieStore cookieStore, @NonNull String cookieName, @NonNull String cookieValue){
        CookieFactory.setCookie(cookieStore, cookieName, cookieValue, null, null);
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>cookieStore</b>
     * {@link CookieStore}
     * </p>
     *
     * <p><b>@param</b> <b>cookieName</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>cookieValue</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>domain</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>path</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/22 22:12</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static void setCookie(@NonNull CookieStore cookieStore, @NonNull String cookieName, @NonNull String cookieValue,  String domain, String path){
        BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
        if(ValueUtils.isNotBlank(domain)){
            cookie.setDomain(domain);
            cookie.setAttribute("domain", "true");
        }
        if(ValueUtils.isBlank(path)){
            path = "/";
        }
        cookie.setPath(path);
        CookieFactory.setCookie(cookieStore, cookie);
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>cookieStore</b>
     * {@link CookieStore}
     * </p>
     *
     * <p><b>@param</b> <b>cookie</b>
     * {@link Cookie}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/22 22:06</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static void setCookie(@NonNull CookieStore cookieStore, @NonNull Cookie cookie){
        Cookie c = getCookie(cookieStore, cookie.getName());
        if(c != null) {
            removeCookie(cookieStore, c);
        }
        cookieStore.addCookie(cookie);
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>cookieStore</b>
     * {@link CookieStore}
     * </p>
     *
     * <p><b>@param</b> <b>name</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/8/4 11:00</p>
     * <p><b>{@code @return:}</b>{@link Cookie}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static Cookie getCookie(@NonNull CookieStore cookieStore, @NonNull String name){
        for (Cookie cooke : cookieStore.getCookies()) {
            if (cooke.getName().equals(name)) {
                return cooke;
            }
        }
        return null;
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>cookieStore</b>
     * {@link CookieStore}
     * </p>
     *
     * <p><b>@param</b> <b>name</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/8/4 11:00</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static boolean removeCookie(@NonNull CookieStore cookieStore, @NonNull String name){
        Cookie cookie = getCookie(cookieStore, name);
        if(cookie == null){
            return false;
        }
        return removeCookie(cookieStore, cookie);
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>cookieStore</b>
     * {@link CookieStore}
     * </p>
     *
     * <p><b>@param</b> <b>cookie</b>
     * {@link Cookie}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/8/4 11:00</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static boolean removeCookie(@NonNull CookieStore cookieStore, @NonNull Cookie cookie){
        return cookieStore.getCookies().remove(cookie);
    }
}
