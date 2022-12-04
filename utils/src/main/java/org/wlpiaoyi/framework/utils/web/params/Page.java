package org.wlpiaoyi.framework.utils.web.params;

import lombok.Data;
import lombok.NonNull;

import java.util.Collection;

@Data
public class Page<T>{

    private Collection<T> items;
    private final int pageIndex;
    private final int pageSize;

    private final long startLimit;
    private final long endLimit;

    private long count = 0;

    public Page(){
        this.pageIndex = 0;
        this.pageSize = 20;
        this.startLimit = ((long) this.pageIndex) * ((long) this.pageSize);
        this.endLimit = this.pageSize;
    }
    public Page(long startLimit, long endLimit){
        this.startLimit = startLimit;
        this.endLimit = endLimit;
        this.pageSize = (int) endLimit;
        this.pageIndex = this.pageSize;
    }
    public Page(int pageIndex, int pageSize){
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.startLimit = ((long) this.pageIndex) * ((long) this.pageSize);
        this.endLimit = this.pageSize;
    }

    public Page(@NonNull Query query){
        this.pageIndex = query.getPageIndex();
        this.pageSize = query.getPageSize();
        this.startLimit = ((long) this.pageIndex) * ((long) this.pageSize);
        this.endLimit = this.pageSize;
    }

}

