package org.wlpiaoyi.framework.utils.aop;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 对象动态代理
 * @Author wlpiaoyi
 * @Date 2022/7/12 15:56
 * @Version 1.0
 */
public class ObjectInvocationProxy<T> implements InvocationHandler {
    private final T target;// 这其实业务实现类对象，用来调用具体的业务方法
    private final ObjectInterceptorProgress progress;
    // 通过构造函数传入目标对象
    public ObjectInvocationProxy(T target, ObjectInterceptorProgress progress) {
        this.target = target;
        this.progress = progress;
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(this.progress != null && !this.progress.startInterceptorProgress(proxy, method, args)){
            return null;
        }
        Object result = null;
        try{
            result = method.invoke(target, args);
        }catch (Exception e){
            if(this.progress != null){
                e = this.progress.exceptionInterceptorProgress(proxy, method, args,e);
            }
            if(e != null) throw e;
        }
        if(this.progress != null){
            result = this.progress.endInterceptorProgress(proxy, method, args, result);
        }
        return result;
    }

    public T getProxy(){
        ClassLoader loader = this.target.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        // 主要装载器、一组接口及调用处理动态代理实例
        T proxyInstance = (T) Proxy.newProxyInstance(loader, interfaces, this);
        return proxyInstance;
    }


}
