package org.wlpiaoyi.framework.utils.web.params;

import lombok.Data;

import java.util.Collection;

@Data
public class Page<T>{

    private Collection<T> items;
    private int pageIndex = 0;
    private int pageSize = 20;
    private long count = 0;

    public void bundleFrom(Query query){
        this.setPageIndex(query.getPageIndex());
        this.setPageSize(query.getPageSize());
    }

}

