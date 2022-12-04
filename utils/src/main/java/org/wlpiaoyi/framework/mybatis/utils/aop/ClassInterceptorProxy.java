package org.wlpiaoyi.framework.mybatis.utils.aop;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 字节码动态代理
 * @Author wlpiaoyi
 * @Date 2022/7/12 16:16
 * @Version 1.0
 */
public class ClassInterceptorProxy implements MethodInterceptor {

    private final ClassInterceptorProgress progress;
    public ClassInterceptorProxy(ClassInterceptorProgress progress){
        this.progress = progress;
    }

    /**
     *
     * @param obj 表示要进行增强的对象
     * @param method 表示拦截的方法
     * @param objects 数组表示参数列表，基本数据类型需要传入其包装类型，如int-->Integer、long-Long、double-->Double
     * @param methodProxy 表示对方法的代理，invokeSuper方法表示对被代理对象方法的调用
     * @return 执行结果
     * @throws Throwable 异常
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if(this.progress != null && !this.progress.startInterceptorProgress(obj, method, objects, methodProxy)){
            return null;
        }
        Object result = null;
        try{
            result = methodProxy.invokeSuper(obj,objects);;
        }catch (Exception e){
            if(this.progress != null){
                e = this.progress.exceptionInterceptorProgress(obj, method, objects, methodProxy, e);
            }
            if(e != null) throw e;
        }
        if(this.progress != null){
            result = this.progress.endInterceptorProgress(obj, method, objects, methodProxy, result);
        }
        return result;
    }

    public <T> T getProxy(Class<T> clazz){

        //指定代理类生成路径
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./");
        // 创建Enhancer对象，类似于JDK动态代理的Proxy类
        Enhancer enhancer = new Enhancer();
        // 设置目标类的字节码文件
        enhancer.setSuperclass(clazz);
        // 设置回调函数
        enhancer.setCallback(this);
        // create方法创建代理类
        T proxyObj = (T)enhancer.create();
        return proxyObj;
    }


}