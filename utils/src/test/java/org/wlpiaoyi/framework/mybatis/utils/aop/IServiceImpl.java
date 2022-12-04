package org.wlpiaoyi.framework.mybatis.utils.aop;

import lombok.Setter;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/12 15:53
 * @Version 1.0
 */
public class IServiceImpl implements IService{

    @Setter
    private IDao dao;

    public IServiceImpl(){

    }

    public IServiceImpl(IDao dao){
        this.dao = dao;
    }

    @Override
    public int save(int value) {
        this.dao.save();
        System.out.println("IServiceImpl.save(" + value + ")");
        return value;
    }

    @Override
    public void add() {
        System.out.println("IServiceImpl.add()");
        throw new RuntimeException("error test");
    }
}
