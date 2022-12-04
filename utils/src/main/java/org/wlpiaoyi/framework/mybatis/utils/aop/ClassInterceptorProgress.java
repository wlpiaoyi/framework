package org.wlpiaoyi.framework.mybatis.utils.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/12 16:36
 * @Version 1.0
 */
public interface ClassInterceptorProgress {

    boolean startInterceptorProgress(Object obj, Method method, Object[] objects, MethodProxy methodProxy);
    Exception exceptionInterceptorProgress(Object obj, Method method, Object[] objects, MethodProxy methodProxy, Exception e);
    Object endInterceptorProgress(Object obj, Method method, Object[] objects, MethodProxy methodProxy, Object result);

}
