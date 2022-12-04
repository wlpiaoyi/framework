package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.singleton;


/**
 * 枚举单例
 * 1、枚举单例如何限制实例个数：创建时有几个就有几个
 * 2、枚举单例属于懒汉式还是饿汉式：饿汉式，相当于静态成员变量
 * 3、枚举单例能否防止反射：不能。
 * 4、枚举单例能否防止反序列化：可以！枚举父类实现了序列化接口，但是它在反序列化的时作了处理。可以防止反序列化创建新对象
 * 5、枚举单例在单例创建时希望加入初始化逻辑怎么做：可以在构造方法中写逻辑
 * 6、枚举单例在创建时是否有并发问题：没有，在类加载时创建，由jvm保证。不会有并发问题
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:34
 * @Version 1.0
 */
public enum SingleEnum {

    INSTANCE;

    public void doSomething() {
    }
}