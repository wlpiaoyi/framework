package org.wlpiaoyi.framework.utils.gson;

public interface JsonSerializer<T> extends com.google.gson.JsonSerializer<T> {
    static Class getType(){
        return null;
    }
}