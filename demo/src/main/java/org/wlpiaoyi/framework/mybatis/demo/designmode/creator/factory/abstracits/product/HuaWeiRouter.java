package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.product;

import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.interfaces.Router;

/**
 * 路由器的实现类
 * 华为路由器
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:53
 * @Version 1.0
 */
public class HuaWeiRouter implements Router {
    @Override
    public void start() {
        System.out.println("华为路由器正在启动...");
    }

    @Override
    public void stop() {
        System.out.println("华为路由器正在关机...");
    }

    @Override
    public void work() {
        System.out.println("华为路由器开始工作...");
    }
}
