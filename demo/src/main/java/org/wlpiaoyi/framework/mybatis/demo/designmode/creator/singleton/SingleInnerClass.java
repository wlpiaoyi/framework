package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.singleton;

/**
 * 静态内部类
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:33
 * @Version 1.0
 */
public final class SingleInnerClass {

    private SingleInnerClass() {

    }

    // 1、属于懒汉式还是饿汉式：懒汉式创建，类加载是懒惰的，在调用时静态变量才会初始化。
    private static class LazyHolder {
        static final SingleInnerClass singleInnerClass = new SingleInnerClass();
    }

    // 2、在创建单例时是否有并发问题：没有，初始化属于静态变量初始化，由jvm保证线程。
    public static SingleInnerClass getSingleInnerClass() {
        return LazyHolder.singleInnerClass;
    }

}
