package org.wlpiaoyi.framework.demo.designmode.structure.bridge;

import lombok.Setter;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/17 20:54
 * @Version 1.0
 */
class Bridge {
    /**
     * 水果接口
     */
    interface Fruit {
        String name();
    }

    /**
     * 食物抽象类
     */
    interface Food {

        String name();

        /**
         * 添加水果
         */
         void addFruit(Fruit fruit);
    }

    static class Cake implements Food {

        @Override
        public String name() {
            return "蛋糕";
        }

        @Override
        public void addFruit(Fruit fruit) {
            System.out.println(fruit.name() + this.name());
        }
    }
    static class Milk implements Food {

        @Override
        public String name() {
            return "牛奶";
        }

        @Override
        public void addFruit(Fruit fruit) {
            System.out.println(fruit.name() + this.name());
        }
    }

    static class Banana implements Fruit{
        @Override
        public String name() {
            return "香蕉";
        }
    }


    static class Strawberry implements Fruit{
        @Override
        public String name() {
            return "草莓";
        }
    }
    public static void main(String[] args) {

        Food cake = new Cake();
        Fruit banana = new Banana();
        cake.addFruit(banana);

        Food milk = new Milk();
        Fruit strawberry = new Strawberry();
        milk.addFruit(strawberry);
    }

}
