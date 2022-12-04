package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.product;

import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.interfaces.Phone;

/**
 * 联想牌手机
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:52
 * @Version 1.0
 */
public class LenovoPhone implements Phone {
    @Override
    public void start() {
        System.out.println("联想手机正在启动...");
    }

    @Override
    public void music() {
        System.out.println("联想手机播放音乐中...");
    }

    @Override
    public void ring() {
        System.out.println("联想手机开启闹铃...");
    }

    @Override
    public void stop() {
        System.out.println("联想手机关机中...");
    }
}
