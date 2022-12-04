package org.wlpiaoyi.framework.utils.web.lock;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author wlpiaoyi
 * @Date 2022/8/1 08:51
 * @Version 1.0
 */
@Slf4j
abstract class Handler {
    private static final ThreadLocal<Map<String, Integer>> ThreadLocalObj = ThreadLocal.withInitial(() ->
            new HashMap<>());

    protected final boolean setLock(Lock lock, long lockExpireTime) {
        boolean isGetLock = false;
        try{
            if (hasRedisLock(lock)) {
                if(ValueUtils.isBlank(ThreadLocalObj.get()))
                    return false;
                if(!ThreadLocalObj.get().containsKey(lock.getId()))
                    return false;
            }else{
                boolean lockFlag = setRedisLock(lock, lockExpireTime);
                //如果设置锁标识失败进入等待 并 try lock
                if (!lockFlag)
                    return false;
            }
            isGetLock = true;
            return true;
        }finally {
            if(isGetLock){
                Map<String, Integer> itemMap = ThreadLocalObj.get();
                Integer index = itemMap.get(lock.getId());
                if(ValueUtils.isBlank(index)){
                    index = 0;
                }
                index ++;
                itemMap.put(lock.getId(), index);
            }
        }
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock           锁的名称
     * @param lockExpireTime 锁的过期
     * @return 0 获取成功，-1获取超时, 1没有拿到
     */
    public boolean tryLock(Lock lock, long lockExpireTime) {
        if (ValueUtils.isBlank(lock.getId())) {
            return false;
        }
        return this.setLock(lock, lockExpireTime);
    }

    /**
     * 释放锁
     */
    public boolean unLock(Lock lock) {
        if(ValueUtils.isBlank(lock.getId()))
            return false;

        Map<String, Integer> itemMap = ThreadLocalObj.get();
        Integer index = itemMap.get(lock.getId());
        if(ValueUtils.isBlank(index)){
            this.delRedisLock(lock);
            return true;
        }
        index --;
        if(ValueUtils.isBlank(index) || index <= 0){
            itemMap.remove(lock.getId());
            this.delRedisLock(lock);
        }else{
            itemMap.put(lock.getId(), index);
        }
        return true;

    }

    /**
     * 尝试获取全局锁
     *
     * @param lock           锁的名称
     * @param timeout        获取锁的超时时间
     * @param tryInterval    多少毫秒尝试获取一次
     * @param lockExpireTime 锁的过期
     * @return 0 获取成功，-1获取超时, 1没有拿到
     */
    public int lock(Lock lock, long timeout, long tryInterval, long lockExpireTime) {
        try {
            if (ValueUtils.isBlank(lock.getId())) {
                return 1;
            }
            long startTime = System.currentTimeMillis();
            boolean continueFlag = true;
            do {
                if (System.currentTimeMillis() - startTime > timeout) {//尝试超过了设定值之后直接跳出循环
                    log.debug("lock is time out for lock[" + lock.getId() + "]");
                    return -1;
                }
                if(!this.setLock(lock, lockExpireTime)){
                    Thread.sleep(tryInterval);
                    continue;
                }
                continueFlag = false;
            }
            while (continueFlag);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            this.unLock(lock);
            return -1;
        }
        return 0;
    }


    protected abstract boolean setRedisLock(Lock lock, long lockExpireTime);
//    {
//        RedisTemplate<String, Boolean> rt = this.redisTemplate;
//        boolean res = rt.execute(
//                (RedisCallback<Boolean>) connection -> {
//                    return connection.setNX(
//                            lock.getName().getBytes(StandardCharsets.UTF_8),
//                            lock.getValue().getBytes(StandardCharsets.UTF_8));
//                });
//        if(res){
//            res = redisTemplate.getConnectionFactory().getConnection().pExpire(
//                    lock.getName().getBytes(StandardCharsets.UTF_8),
//                    lockExpireTime);
//        }
//        return res;
//    }

    protected abstract boolean hasRedisLock(Lock lock);
//    {
//        RedisTemplate<String, Boolean> rt = this.redisTemplate;
//        return rt.execute(
//                (RedisCallback<Boolean>) connection -> {
//                    byte[] bytes = connection.getRange(
//                            lock.getName().getBytes(StandardCharsets.UTF_8),
//                            0,0);
//                    if(ValueUtils.isBlank(bytes)) return false;
//                    return true;
//                });
//    }

    protected abstract Long delRedisLock(Lock lock);
//    {
//        RedisTemplate<String, Long> rt = redisTemplate;
//        Long count = rt.execute(
//                (RedisCallback<Long>) connection -> {
//                    return connection.del(lock.getName().getBytes(StandardCharsets.UTF_8));
//                });
//        if(count == null)
//            count = 0L;
//        return count;
//    }

}
