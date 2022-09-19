package org.wlpiaoyi.framework.demo.designmode.structure.proxy;

/**
 * 通过继承实现静态代理
 * @Author wlpiaoyi
 * @Date 2022/9/17 20:24
 * @Version 1.0
 */
public class Extends {
    static class Tank{
        public void move() {
            System.out.println("Tank moving ....");
        }
    }
    static class  ProxyTank extends Tank{
        @Override
        public void move() {
            System.out.println("方法执行前...");
            super.move();
            System.out.println("方法执行后...");
        }
    }


    public static void main(String[] args) {
        new ProxyTank().move();
    }
}
