package org.wlpiaoyi.framework.utils.aop;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Getter
    static class Point {
        private double x;
        private double y;

        public Point(double x, double y){
            this.x = x;
            this.y = y;
        }
    }

    @Getter
    static class Line {
        private Point point1;
        private Point point2;
        private double sin;
        private double size;
        /**
         * 0,1,2,3 逆时针
         */
        private short start;



        public Line(Point point1, Point point2){
            this.point1 = point1;
            this.point2 = point2;
            this.initStart();
            this.initSize();
        }

        private void initSize(){
            double a,b;
            a = Math.abs(this.point1.getY() - this.point2.getY());
            b = Math.abs(this.point1.getX() - this.point2.getX());
            double c = Math.pow(a, 2) + Math.pow(b, 2);
            c = Math.sqrt(c);
            this.size = c;
            this.sin = a/c;
        }

        private void initStart(){

            if(this.point1.getX() < this.point2.getX()){
                if(this.point1.getY() > this.point2.getY()){
                    this.start = 0;
                }else{
                    this.start = 1;
                }
            }else{
                if(this.point1.getY() > this.point2.getY()){
                    this.start = 3;
                }else{
                    this.start = 4;
                }
            }
        }
    }

    /**
     * 是否有交点
     * @param line1
     * @param line2
     * @return
     */
    public static Point getAcrossPoint(Line line1, Line line2){
        Point l1_0 = line1.getPoint1();
        Point l1_1 = line1.getPoint2();
        Point l2_0 = line2.getPoint1();
        Point l2_1 = line2.getPoint2();

        double a1=l1_0.getY()-l1_1.getY();
        double b1=l1_1.getX()-l1_0.getX();
        double c1=a1*l1_0.getX()+b1*l1_0.getY();

        double a2=l2_0.getY()-l2_1.getY();
        double b2=l2_1.getX()-l2_0.getX();
        double c2=a2*l2_0.getX()+b2*l2_0.getY();

        double k=a1*b2-a2*b1;

        if(Math.abs(k) < 0.00001){
            return null;
        }

        double a=b2/k;
        double b=-1*b1/k;
        double c=-1*a2/k;
        double d=a1/k;

        double x=a*c1+b*c2;
        double y=c*c1+d*c2;

        return new Point(x,y);

    }
    @Test
    public void testLine(){

        Point p = getAcrossPoint(
                new Line(new Point(0,0), new Point(4,4)),
                new Line(new Point(0,4), new Point(4,0)));

        Point p2 = getAcrossPoint(
                new Line(new Point(0,4), new Point(4,0)),
                new Line(new Point(0,0), new Point(4,4)));


        Point p3 = getAcrossPoint(
                new Line(new Point(0,0), new Point(4,4)),
                new Line(new Point(0,0), new Point(4,4)));


        Point p4 = getAcrossPoint(
                new Line(new Point(-4,-4), new Point(4,4)),
                new Line(new Point(0,4), new Point(4,0)));

        System.out.println();

    }


    public static boolean isInPoly(List<Point> points, Point target){
        Line linet_1 = new Line(target, new Point(target.getX(), -999999999));
        Line linet_2 = new Line(target, new Point(target.getX(), 999999999));
        Map<Line, Point> map = new HashMap();
        Point prep = null;
        for (Point point : points){
            try{
                if(prep == null){
                    continue;
                }
                Line line = new Line(prep, point);
                Point p = getAcrossPoint(linet_1, line);
                if(p == null){
                    continue;
                }
                if(map.containsKey(linet_1)){
                    return false;
                }
                map.put(linet_1, point);
                if(map.size() == 2){
                    return true;
                }


                p = getAcrossPoint(linet_2, line);
                if(p == null){
                    continue;
                }
                if(map.containsKey(linet_2)){
                    return false;
                }
                map.put(linet_2, point);
                if(map.size() == 2){
                    return true;
                }
            }finally {
                prep = point;
            }
        }
        return false;
    }

    @Test
    public void testPoly(){
        boolean b = isInPoly(new ArrayList(){{
            add(new Point(-2, 2));
            add(new Point(2, 2));
            add(new Point(2, -2));
            add(new Point(2, -2));
            add(new Point(-2, 2));
        }}, new Point(0, 0));

        System.out.println();
    }




    @Test
    public void test(){
        ObjectInvocationProxy<IDaoImpl> invocationHandler1 = new ObjectInvocationProxy<>(new IDaoImpl(), this);
        IDao dao = invocationHandler1.getProxy();
        ObjectInvocationProxy<IServiceImpl> invocationHandler2 = new ObjectInvocationProxy<>(new IServiceImpl(dao), this);
        IService service = invocationHandler2.getProxy();
        int value = service.save(10);
        log.info("save value:{} res value:{}", 10, value);
        if(value != 20){
            throw new BusinessException("代理截获返回值失败");
        }
        try{
            service.add();
        }catch (Exception e){
            log.error("对象动态代理截获异常测试", e);
        }

    }

    @Test
    public void test2(){
        ClassInterceptorProxy methodInterceptor = new ClassInterceptorProxy(this);
        IDao dao = methodInterceptor.getProxy(IDaoImpl.class);
        IService service = methodInterceptor.getProxy(IServiceImpl.class);
        ((IServiceImpl)service).setDao(dao);
        int value = service.save(10);
        System.out.println(value);
        try{
            service.add();
        }catch (Exception e){
            log.error("字节码动态代理获异常测试", e);
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
        log.error("exceptionInterceptorProgress:" + method);
        return e;
    }

    @Override
    public Object endInterceptorProgress(Object proxy, Method method, Object[] args, Object result) {
        log.info("endInterceptorProgress:" + method);
        if(result instanceof Integer && (Integer) result == 10){
            return 20;
        }
        return result;
    }
}
