package org.wlpiaoyi.framework.demo.designmode.creator.factory.abstracits.interfaces;

/**
 * 品牌接口(可根据不同的品牌获取不同的产品)
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:46
 * @Version 1.0
 */
public interface Brand {
    Phone getPhone();
    Router getRouter();
}
