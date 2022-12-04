package org.wlpiaoyi.framework.mybatis.demo.designmode.creator.prototype;

/**
 * 原型模式
 * @Author wlpiaoyi
 * @Date 2022/9/17 00:23
 * @Version 1.0
 */
public class Student implements Cloneable{

    @Override //重写clone()方法
    public Object clone() throws CloneNotSupportedException { // 提升访问权限
        Student student = (Student) super.clone();
        //针对成员变量进行拷贝
        student.name = new String(name);
        student.food = new Food(food.getFood());
        return student;
    }

    private Food food;
    private String name;
    int age;

    public Student() {
    }

    public Student(String name, int age, Food food) {
        this.name = name;
        this.age = age;
        this.food = food;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setFood(Food food){
        this.food = food;
    }

    public Food getFood(){
        return food;
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        Food food  = new Food("apple");
        System.out.println(food); // JavaPackage_1.Food@6d311334

//Java深拷贝，对内层的对象也进行拷贝。
        Student student1 = new Student("stu", 29,food);
// 通过student1克隆student2，不通过new对象的形式创建
        Student student2 = (Student) student1.clone();

        System.out.println(student1); // JavaPackage_1.Student@3d075dc0
        System.out.println(student2); // JavaPackage_1.Student@214c265e
        System.out.println(student1==student2); // false，student1和student2指向的是不同地址

        System.out.println(student1.getFood()); // JavaPackage_1.Food@6d311334
        System.out.println(student2.getFood()); // JavaPackage_1.Food@448139f0
        System.out.println(student1.getFood()==student2.getFood()); // false，student1.food和student2.food指向不同地址

        food.setFood("orange");
        System.out.println(student1.getFood().getFood()); // orange
        System.out.println(student2.getFood().getFood()); // apple
    }
}
