package org.wlpiaoyi.framework.demo.designmode.structure.proxy;

/**
 * 通过组合实现静态代理
 * @Author wlpiaoyi
 * @Date 2022/9/17 20:28
 * @Version 1.0
 */
public class Combination {

    interface Movable {
        void move();
    }

    static class Tank implements Movable{
        @Override
        public void move() {
            System.out.println("Tank moving ....");
        }
    }

    static class LogProxy implements Movable{
        private Movable movable;
        public LogProxy(Movable movable) {
            this.movable = movable;
        }
        @Override
        public void move() {
            System.out.println("方法执行前....");
            movable.move();
            System.out.println("方法执行后....");
        }
    }


    public static void main(String[] args) {
        Tank tank = new Tank();
        new LogProxy(tank).move();
    }
}
