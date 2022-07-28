package org.wlpiaoyi.framework.utils.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.queue.Queue;
import org.wlpiaoyi.framework.utils.queue.QueueFactory;
import org.wlpiaoyi.framework.utils.rsa.Rsa;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class LockeHandlerTest extends LockHandler{


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws Exception {

        Lock lock = new Lock("1");
        Queue queue = QueueFactory.createUnordered();
        queue.addTask(() -> {
            lock(lock);
            System.out.println("lock1");
            boolean flag1 = tryLock(lock);
            System.out.println("lock1-1:" + flag1);
            boolean flag2 = tryLock(lock);
            System.out.println("lock1-2:" + flag2);
            lock(lock);
            System.out.println("lock1-3");
            unLock(lock);
            if(flag2)
                unLock(lock);
            if(flag1)
                unLock(lock);
            int timer = 0;
            while (timer < 100){
                try {Thread.sleep(10);
                } catch (Exception e) {}
                timer += 10;
                System.out.println("lock1:" + timer);
            }
            unLock(lock);

            try {Thread.sleep(40);
            } catch (Exception e) {}
            boolean flag4 = tryLock(lock);
            System.out.println("lock1-4:" + flag4);
            if(flag4)
                unLock(lock);
        });
        queue.addTask(() -> {
            try {Thread.sleep(20);
            } catch (Exception e) {}
            lock(lock);
            System.out.println("lock2");
            int timer = 0;
            while (timer < 100){
                try {Thread.sleep(10);
                } catch (Exception e) {}
                timer += 10;
                System.out.println("lock2:" + timer);
            }
            unLock(lock);
        });
        queue.start();
        queue.await();
    }

    @Test
    public void test2() throws Exception {

        Lock lock = new Lock("1");
        Queue queue = QueueFactory.createUnordered();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            queue.addTask(() -> {
                lock(lock);
                System.out.println("lock" + finalI);
                int timer = 0;
                while (timer < 30){
                    try {Thread.sleep(10);
                    } catch (Exception e) {}
                    timer += 10;
                    System.out.println("lock" + finalI + "：" + timer);
                }
                unLock(lock);
            });
        }
        queue.start();
        queue.await();
    }

    @After
    public void tearDown() throws Exception {

    }




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
