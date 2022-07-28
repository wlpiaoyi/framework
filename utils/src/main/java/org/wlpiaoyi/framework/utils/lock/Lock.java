package org.wlpiaoyi.framework.utils.lock;

import lombok.Getter;
import lombok.NonNull;
import org.wlpiaoyi.framework.utils.snowflake.IdWorker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局锁，包括锁的名称
 */

public class Lock {

    private static final Object synIndexTag = new Object();

    private static volatile long valueIndex = 0;

    Lock(@NonNull String id) {
        this.id = id;
        synchronized (synIndexTag){
            this.index = valueIndex;
        }
    }

    private synchronized static long nextValueIndex(){
        return valueIndex ++;
    }

    void nextLock(){
        synchronized (this.synIndexTag){
            this.index = nextValueIndex();
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
