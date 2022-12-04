package org.wlpiaoyi.framework.mybatis.utils.web.lock;

import lombok.extern.slf4j.Slf4j;


/**
 * @Author wlpiaoyi
 * @Date 2022/7/21 14:50
 * @Version 1.0
 */
@Slf4j
public abstract class LockHandler extends Handler{

    protected static int LOCK_TRY_INTERVAL = 20;//默认20ms尝试一次
    protected static long LOCK_TRY_TIMEOUT = 30 * 1000L;//默认尝试30s
    protected static long LOCK_EXPIRE = 60 * 1000L;//单个业务持有锁的时间60s，防止死锁

    /**
     * 尝试获取全局锁
     *
     * @param lock 锁的名称
     * @return true 获取成功，false获取失败
     */
    public int lock(Lock lock) {
         return super.lock(lock, LOCK_TRY_TIMEOUT, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock    锁的名称
     * @param tryInterval 多少毫秒尝试获取一次
     * @return true 获取成功，false获取失败
     */
    public int lock(Lock lock, int tryInterval) {
        return lock(lock, LOCK_TRY_TIMEOUT, tryInterval, LOCK_EXPIRE);
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock    锁的名称
     * @param timeout 获取超时时间 单位ms
     * @return true 获取成功，false获取失败
     */
    public int lock(Lock lock, long timeout) {
        return lock(lock, timeout, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
    }

    /**
     * 尝试获取全局锁
     *
     * @param lock        锁的名称
     * @param timeout     获取锁的超时时间
     * @param tryInterval 多少毫秒尝试获取一次
     * @return true 获取成功，false获取失败
     */
    public int lock(Lock lock, long timeout, int tryInterval) {
         return lock(lock, timeout, tryInterval, LOCK_EXPIRE);
    }


    /**
     * 尝试获取全局锁
     *
     * @param lock 锁的名称
     * @return true 获取成功，false获取失败
     */
    public boolean tryLock(Lock lock) {
        return tryLock(lock, LOCK_EXPIRE);
    }

}
