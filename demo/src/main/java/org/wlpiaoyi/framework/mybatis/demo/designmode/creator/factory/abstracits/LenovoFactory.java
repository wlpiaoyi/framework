package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits;

import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.interfaces.Brand;
import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.interfaces.Phone;
import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.interfaces.Router;
import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.product.LenovoPhone;
import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.abstracits.product.LenovoRouter;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:57
 * @Version 1.0
 */
public class LenovoFactory implements Brand {
    @Override
    public Phone getPhone() {
        return new LenovoPhone();
    }

    @Override
    public Router getRouter() {
        return new LenovoRouter();
    }
}
