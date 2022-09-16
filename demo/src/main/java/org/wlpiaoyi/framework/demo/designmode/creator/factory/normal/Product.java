package org.wlpiaoyi.framework.demo.designmode.creator.factory.normal;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/16 23:37
 * @Version 1.0
 */
public class Product {

    //创建一个产品接口
    interface Car {
        String getType();
    }
    //创建三个具体的产品类，来实现产品接口
    static class BYD implements Car{
        public String getType(){
            return "生产了产品BYDCar，该产品为"+this;
        }
    }
    static class BWM implements Car{
        public String getType(){
            return "生产了产品BWMCar，该产品为"+this;
        }
    }
    static class MAZDA implements Car{
        public String getType(){
            return "生产了产品MAZDACar，该产品为"+this;
        }
    }
}
