package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.singleton;


import java.io.Serializable;

/**
 * 饿汉式：实现简单，安全可靠
 * 1、为什么使用final:防止子类覆盖父类方法破坏单例
 * 2、如果实现了序列化，如何防止反序列化破坏单例：加入readResolve方法，
 * 在反序列化时就会采用readResolve返回的对象，而不是反序列化生成的对象
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:31
 * @Version 1.0
 */
public final class SingleEhan implements Serializable {

    //3、构造方法为什么为私有：防止使用者使用构造器创建对象，破坏单例。
    //4、能否防止反射：不能，设置accessable为true通过构造方法反射依然会获得对象
    private SingleEhan() {

    }
    //5、这样初始化单例能否保证单例对象创建时的线程安全：可以，静态成员变量，在类初始化的时候创建。由jvm保证线程安全
    private static final SingleEhan singleEhan = new SingleEhan();

    // 6、为什么使用方法而不是直接将singleEhan设置为public：为了更好的封装，可以改写成懒惰初始化。也可以支持泛型。方法中可以写一些别的逻辑
    public static SingleEhan getSingleEhan() {
        return singleEhan;
    }

    public Object readResolve(){
        return singleEhan;
    }
}
