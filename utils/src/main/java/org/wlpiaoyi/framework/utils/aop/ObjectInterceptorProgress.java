package org.wlpiaoyi.framework.utils.aop;

import java.lang.reflect.Method;

/**
 * 对象拦截器进度
 * @Author wlpiaoyi
 * @Date 2022/7/12 16:42
 * @Version 1.0
 */
public interface ObjectInterceptorProgress {
    /**
     * 开始拦截
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    boolean startInterceptorProgress(Object proxy, Method method, Object[] args);

    /**
     * 拦截异常
     * @param proxy
     * @param method
     * @param args
     * @param e
     * @return
     */
    Exception exceptionInterceptorProgress(Object proxy, Method method, Object[] args, Exception e);

    /**
     * 结束拦截
     * @param proxy
     * @param method
     * @param args
     * @param result
     * @return
     */
    Object endInterceptorProgress(Object proxy, Method method, Object[] args, Object result);
}
