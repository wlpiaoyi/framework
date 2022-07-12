package org.wlpiaoyi.framework.utils.aop;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/12 16:11
 * @Version 1.0
 */
@Slf4j
public class ProxyTest implements ObjectInterceptorProgress, ClassInterceptorProgress{
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test(){
        ObjectInvocationProxy<IDaoImpl> invocationHandler1 = new ObjectInvocationProxy<>(new IDaoImpl(), this);
        IDao dao = invocationHandler1.getProxy();
        ObjectInvocationProxy<IServiceImpl> invocationHandler2 = new ObjectInvocationProxy<>(new IServiceImpl(dao), this);
        IService service = invocationHandler2.getProxy();
        int value = service.save(10);
        System.out.println(value);
        try{
            service.add();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void test2(){
        ClassInterceptorProxy methodInterceptor = new ClassInterceptorProxy(this);
        IDao dao = methodInterceptor.getProxy(IDaoImpl.class);
        IServiceImpl temp = new IServiceImpl();
        IService service = methodInterceptor.getProxy(IServiceImpl.class);
        ((IServiceImpl)service).setDao(dao);
        int value = service.save(10);
        System.out.println(value);
        try{
            service.add();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws Exception {
    }

    @Override
    public boolean startInterceptorProgress(Object obj, Method method, Object[] objects, MethodProxy methodProxy) {
        log.info("startInterceptorProgress:" + methodProxy);
        return true;
    }

    @Override
    public Exception exceptionInterceptorProgress(Object obj, Method method, Object[] objects, MethodProxy methodProxy, Exception e) {
        log.info("exceptionInterceptorProgress:" + methodProxy);
        return e;
    }

    @Override
    public Object endInterceptorProgress(Object obj, Method method, Object[] objects, MethodProxy methodProxy, Object result) {
        log.info("endInterceptorProgress:" + methodProxy);
        return result;
    }

    @Override
    public boolean startInterceptorProgress(Object proxy, Method method, Object[] args) {
        log.info("startInterceptorProgress:" + method);
        return true;
    }

    @Override
    public Exception exceptionInterceptorProgress(Object proxy, Method method, Object[] args, Exception e) {
        log.info("exceptionInterceptorProgress:" + method);
        return e;
    }

    @Override
    public Object endInterceptorProgress(Object proxy, Method method, Object[] args, Object result) {
        log.info("endInterceptorProgress:" + method);
        if(result instanceof Integer && ((Integer) result).intValue() == 10){
            return new Integer(20);
        }
        return result;
    }
}
