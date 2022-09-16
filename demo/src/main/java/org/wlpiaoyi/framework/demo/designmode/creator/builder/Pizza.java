package org.wlpiaoyi.framework.demo.designmode.creator.builder;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/17 00:13
 * @Version 1.0
 */
public class Pizza {
    private String parts;
    public void setParts(String parts){
        this.parts = parts;
    }
    public String toString(){
        return this.parts;
    }
}
