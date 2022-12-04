package org.wlpiaoyi.framework.demo.designmode.structure.adapter;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/17 20:36
 * @Version 1.0
 */
class Adapter {
    //适配者类的接口
    interface PlugA {
        // 实现方法插头A充电
        void ChargeFromPlugA();
    }
    //目标接口
    interface PlugB {
        // 充电
        void ChargeFromPlugB();
    }
    static class PlugAImpl implements PlugA{
        @Override
        public void ChargeFromPlugA() {
            System.out.println("用插头A充电");
        }
    }
    static class PlugBImpl implements PlugB{

        @Override
        public void ChargeFromPlugB() {
            System.out.println("用插头B转换");
        }
    }

    //手机类
    static class MobilePhone {

        //使用A接口充电
        public void ChargeFromPlugA(PlugA plugA){
            if(plugA == null){
                throw new NullPointerException("plug is not null!");
            }
            plugA.ChargeFromPlugA();
        }
    }

    //适配器类
    static class PlugAAdapterPlugB extends PlugBImpl implements PlugA{

        @Override
        public void ChargeFromPlugA() {
            ChargeFromPlugB();
            System.out.println("使用插头A充电");

        }

    }
    static class PlugAAdapterPlugB_instance implements PlugA{

        private PlugB plugB;

        public PlugAAdapterPlugB_instance(PlugB plugB) {
            this.plugB = plugB;
        }

        @Override
        public void ChargeFromPlugA() {
            plugB.ChargeFromPlugB();
            System.out.println("使用插头A充电");

        }
    }

    public static void main(String[] args) {
        System.out.println("===1.手机不使用转接头充电===");
        MobilePhone mobilePhone = new MobilePhone();
        mobilePhone.ChargeFromPlugA(new PlugAImpl());

        System.out.println("===2.手机使用转接头B转到A充电[类适配器模式]===");
        PlugA plugA = new PlugAAdapterPlugB();
        mobilePhone.ChargeFromPlugA(plugA);

        System.out.println("===3.手机使用转接头B转到A充电[对象适配器模式]===");
        PlugA plugA2 = new PlugAAdapterPlugB_instance(new PlugBImpl());
        mobilePhone.ChargeFromPlugA(plugA2);
    }
}
