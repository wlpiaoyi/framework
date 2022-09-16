package org.wlpiaoyi.framework.demo.designmode.creator.singleton;

/**
 * 懒汉式
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:32
 * @Version 1.0
 */
public final class SingleLanHan {
    private static SingleLanHan singleLanHan = null;

    private SingleLanHan() { }

    //此处能否保证线程安全：可以，因为创建对象或者获得对象会加锁。但是，锁的范围较大，就算对象创建好了，以后获得对象也要加锁，性能较低！
    public static synchronized SingleLanHan getSingleLanhan() {
        if(singleLanHan!=null) {
            return singleLanHan;
        }
        singleLanHan = new SingleLanHan();
        return singleLanHan;
    }

}