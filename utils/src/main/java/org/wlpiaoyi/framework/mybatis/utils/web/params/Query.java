package org.wlpiaoyi.framework.mybatis.utils.web.params;

import lombok.Data;

@Data
public class Query<T> {

    private T condition;

    private int pageIndex = 0;
    private int pageSize = 20;

    private final long startLimit;
    private final long endLimit;


    public Query(){
        this.pageIndex = 0;
        this.pageSize = 20;
        this.startLimit = ((long) this.pageIndex) * ((long) this.pageSize);
        this.endLimit = this.pageSize;
    }
    public Query(long startLimit, long endLimit){
        this.startLimit = startLimit;
        this.endLimit = endLimit;
        this.pageSize = (int) endLimit;
        this.pageIndex = (int) (startLimit / this.pageSize);
    }
    public Query(int pageIndex, int pageSize){
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.startLimit = ((long) this.pageIndex) * ((long) this.pageSize);
        this.endLimit = this.pageSize;
    }


}