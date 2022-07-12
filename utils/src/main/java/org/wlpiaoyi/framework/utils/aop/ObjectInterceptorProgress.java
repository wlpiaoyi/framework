package org.wlpiaoyi.framework.utils.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/12 16:42
 * @Version 1.0
 */
public interface ObjectInterceptorProgress {
    boolean startInterceptorProgress(Object proxy, Method method, Object[] args);
    Exception exceptionInterceptorProgress(Object proxy, Method method, Object[] args, Exception e);
    Object endInterceptorProgress(Object proxy, Method method, Object[] args, Object result);
}
