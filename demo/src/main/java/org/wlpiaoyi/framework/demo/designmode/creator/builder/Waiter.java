package org.wlpiaoyi.framework.demo.designmode.creator.builder;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/17 00:15
 * @Version 1.0
 */
class Waiter {
    private PizzaBuilder pizzaBuilder;
    public void setPizzaBuilder(PizzaBuilder pizzaBuilder){
        /*设置构建器*/
        this.pizzaBuilder = pizzaBuilder;
    }
    public Pizza getPizza(){
        return pizzaBuilder.getPizza();
    }
    public void construct(){
        /* 构建 */
        pizzaBuilder.createNewPizza();
        pizzaBuilder.buildParts();
    }
}
