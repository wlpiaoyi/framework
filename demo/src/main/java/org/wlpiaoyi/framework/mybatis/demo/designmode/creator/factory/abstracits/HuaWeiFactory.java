package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits;

import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.interfaces.Brand;
import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.interfaces.Phone;
import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.interfaces.Router;
import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.product.HuaWePhone;
import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.product.HuaWeiRouter;

/**
 * 华为工厂(提供华为手机、华为路由器)
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:56
 * @Version 1.0
 */
public class HuaWeiFactory implements Brand {
    @Override
    public Phone getPhone() {
        return new HuaWePhone();
    }

    @Override
    public Router getRouter() {
        return new HuaWeiRouter();
    }
}