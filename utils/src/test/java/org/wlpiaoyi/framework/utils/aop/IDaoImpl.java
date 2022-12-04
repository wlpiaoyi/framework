package org.wlpiaoyi.framework.utils.aop;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/12 15:51
 * @Version 1.0
 */
public class IDaoImpl implements IDao{

    IDaoImpl(){
        System.out.println("IDaoImpl creator");
    }

    @Override
    public void save() {
        System.out.println("IDaoImpl.save()");
    }
}
