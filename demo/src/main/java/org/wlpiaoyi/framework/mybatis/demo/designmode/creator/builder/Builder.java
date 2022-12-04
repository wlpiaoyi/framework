package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.builder;


/**
 * 生成器模式（Builder）
 * @Author wlpiaoyi
 * @Date 2022/9/17 00:16
 * @Version 1.0
 */
public class Builder {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Waiter waiter = new Waiter();
        PizzaBuilder hawaiian_pizzabuilder = new PizzaBuilder.HawaiianPizzaBuilder();
        waiter.setPizzaBuilder(hawaiian_pizzabuilder);
        waiter.construct();
        System.out.println("pizza：" + waiter.getPizza());
        System.out.println("=========================================");
        PizzaBuilder spicy_pizzabuilder = new PizzaBuilder.SpicyPizzaBuilder();
        waiter.setPizzaBuilder(spicy_pizzabuilder);
        waiter.construct();
        System.out.println("pizza：" + waiter.getPizza());
    }
}
