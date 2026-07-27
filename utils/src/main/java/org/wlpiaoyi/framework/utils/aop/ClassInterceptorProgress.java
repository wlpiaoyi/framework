package org.wlpiaoyi.framework.utils.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 类动态代理
 * @Author wlpiaoyi
 * @Date 2022/7/12 16:36
 * @Version 1.0
 */
public interface ClassInterceptorProgress {

    /**
     * 开始拦截
     * @param obj
     * @param method
     * @param objects
     * @param methodProxy
     * @return
     */
    boolean startInterceptorProgress(Object obj, Method method, Object[] objects, MethodProxy methodProxy);

    /**
     * 拦截异常
     * @param obj
     * @param method
     * @param objects
     * @param methodProxy
     * @param e
     * @return
     */
    Exception exceptionInterceptorProgress(Object obj, Method method, Object[] objects, MethodProxy methodProxy, Exception e);

    /**
     * 结束拦截
     * @param obj
     * @param method
     * @param objects
     * @param methodProxy
     * @param result
     * @return
     */
    Object endInterceptorProgress(Object obj, Method method, Object[] objects, MethodProxy methodProxy, Object result);

}
