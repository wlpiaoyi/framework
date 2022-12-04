package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.builder;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/17 00:14
 * @Version 1.0
 */
abstract class PizzaBuilder{
    protected Pizza pizza;
    public Pizza getPizza(){
        return pizza;
    }
    public void createNewPizza(){
        pizza = new Pizza();
    }
    public abstract void buildParts() ;

    static class HawaiianPizzaBuilder extends PizzaBuilder{
        public void buildParts(){
            pizza.setParts("cross + mild + ham&pineapp1e");
        }
    }

    static class SpicyPizzaBuilder extends PizzaBuilder{
        public void buildParts(){
            pizza.setParts("panbaked + hot + pepperoni&salami");
        }
    }
}
