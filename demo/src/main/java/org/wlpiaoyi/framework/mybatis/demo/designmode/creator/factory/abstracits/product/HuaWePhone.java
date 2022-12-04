package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.product;

import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.interfaces.Phone;

/**
 * 手机的实现类
 * 华为牌手机
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:51
 * @Version 1.0
 */
public class HuaWePhone implements Phone {
    @Override
    public void start() {
        System.out.println("华为手机启动中...");
    }

    @Override
    public void music() {
        System.out.println("华为手机播放音乐中...");
    }

    @Override
    public void ring() {
        System.out.println("华为手机开启闹铃中...");
    }

    @Override
    public void stop() {
        System.out.println("华为手机关机中...");
    }
}
