package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.normal;

import org.wlpiaoyi.framework.mybatis.demo.designmode.creator.factory.normal.Product.*;

/**
 * 把对象的创建功能交给工厂，达到解耦目的。
 * 适合场景：
 * 无法预知对象确切类别及依赖关系时，可使用。
 * 希望复用现有对象来节省系统资源，而不是每次都创建对象。
 * 创建一个工厂类，生产上面三个产品(即生成上面三个类的对象)
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:38
 * @Version 1.0
 */
public class Factory {

    //创建生产上面三个类的对象的方法
    public static Car makeBYD(){
        BYD bydCar = new BYD();
        System.out.println(bydCar.getType());
        return bydCar;
    }
    public static Car makeBWM(){
        BWM bwmCar = new BWM();
        System.out.println(bwmCar.getType());
        return bwmCar;
    }
    public static Car makeMAZDA(){
        MAZDA mazdaCar = new MAZDA();
        System.out.println(mazdaCar.getType());
        return mazdaCar;
    }
}
