package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.prototype;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/17 00:23
 * @Version 1.0
 */
public class Food {
    String food;

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public Food(String food) {
        this.food = food;
    }
}
