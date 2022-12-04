package org.wlpiaoyi.framework.utils.web.lock;

import lombok.Getter;
import lombok.NonNull;


/**
 * 全局锁，包括锁的名称
 * @Author wlpiaoyi
 * @Date 2022/7/21 14:50
 * @Version 1.0
 */
public class Lock {

    private static final Object synIndexTag = new Object();

    private static volatile long valueIndex = 0;

    public Lock(@NonNull String id) {
        this.id = id;
    }
    
    void nextLock(){
        synchronized (this.synIndexTag){
            this.index = valueIndex ++;
        }
    }

    @Getter
    private String id;
    private volatile long index;

    public long getIndex() {
        synchronized (this.synIndexTag){
            return index;
        }
    }
}
