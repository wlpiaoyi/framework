package org.wlpiaoyi.framework.mybatis.utils.lock;

import org.wlpiaoyi.framework.mybatis.utils.web.lock.Lock;
import org.wlpiaoyi.framework.mybatis.utils.web.lock.LockHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author wlpiaoyi
 * @Date 2022/8/1 08:56
 * @Version 1.0
 */
public class LockHandlerDefault extends LockHandler {


    private static final Map<String, String> lockMap = new ConcurrentHashMap<>();

    @Override
    protected boolean setRedisLock(Lock lock, long lockExpireTime) {
        if(lockMap.containsKey(lock.getId())) return false;
        synchronized (lockMap){
            if(lockMap.containsKey(lock.getId())) return false;
            lockMap.put(lock.getId(), "1");
        }
        return true;
    }

    @Override
    protected boolean hasRedisLock(Lock lock) {
        return lockMap.containsKey(lock.getId());
    }

    @Override
    protected Long delRedisLock(Lock lock) {
        lockMap.remove(lock.getId());
        return 1L;
    }
}
