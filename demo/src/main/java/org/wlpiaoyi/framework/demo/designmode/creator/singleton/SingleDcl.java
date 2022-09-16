package org.wlpiaoyi.framework.demo.designmode.creator.singleton;

/**
 * 双重验证
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:32
 * @Version 1.0
 */
public final class SingleDcl {
    // 1、此处为何要用volatile：防止singleDcl 在实例化的时候指令重排，导致别的线程获得实例的时候虽不为空，但还未赋值。
    private static volatile SingleDcl singleDcl = null;

    private SingleDcl() {}

    // 2、对比单纯的懒汉式，这里有什么优点：初始化成功后，都不用上锁
    public static SingleDcl getSingleDcl() {
        if(singleDcl!=null) {
            return singleDcl;
        }
        synchronized (SingleDcl.class) {
            // 3、此处为什么还要为空判断：因为首次初始化时都会进入到同步块。
            if(singleDcl!=null) {
                return singleDcl;
            }
            singleDcl = new SingleDcl();
        }
        return singleDcl;
    }

}
