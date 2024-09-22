package org.wlpiaoyi.framework.utils.http.factory;

import lombok.NonNull;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.protocol.HttpContext;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b>Cookies manager and store</p>
 * <p><b>{@code @date:}</b>2024-08-04 10:43:18</p>
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
     * <p><b>{@code @date:}</b>2024/8/4 11:00</p>
     * <p><b>{@code @return:}</b>{@link CookieStore}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static CookieStore getLocationCookieStore(@NonNull HttpContext httpContext) {
        Object value =  httpContext.getAttribute(HttpClientContext.COOKIE_STORE);
        if(value == null){
            return null;
        }
        return (CookieStore) value;
    }

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
