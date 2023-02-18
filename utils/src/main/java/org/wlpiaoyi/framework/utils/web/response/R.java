package org.wlpiaoyi.framework.utils.web.response;

import lombok.Getter;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/15 22:23
 * {@code @version:}:       1.0
 */
@Getter
public class R<T> {

    private int code;

    private T data;

    private String message;

    public static <T> R<T>  success(T data){
       R<T> r = new R<>();
       r.message = "SUCCESS";
       r.data = data;
       r.code = 200;
       return r;
    }

    public static <T> R<T>  success(T data, String message){
        R<T> r = new R<>();
        r.message = message;
        r.data = data;
        r.code = 200;
        return r;
    }

    public static <T> R<T> data(int code){
        R<T> r = new R<>();
        r.code = code;
        return r;
    }

    public static <T> R<T> data(int code, String message){
        R<T> r = new R<>();
        r.message = message;
        r.code = code;
        return r;
    }


    public static <T> R<T> data(int code, T data, String message){
        R<T> r = new R<>();
        r.message = message;
        r.data = data;
        r.code = code;
        return r;
    }

    public static <T> R<T>  fail(String message){
        R<T> r = new R<>();
        r.message = message;
        r.code = 500;
        return r;
    }
    public static <T> R<T>  fail(T data, String message){
        R<T> r = new R<>();
        r.message = message;
        r.data = data;
        r.code = 500;
        return r;
    }
}
