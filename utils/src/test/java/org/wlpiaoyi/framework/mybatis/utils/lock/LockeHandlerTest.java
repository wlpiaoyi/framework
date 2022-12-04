package org.wlpiaoyi.framework.mybatis.utils.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.mybatis.utils.queue.Queue;
import org.wlpiaoyi.framework.mybatis.utils.queue.QueueFactory;
import org.wlpiaoyi.framework.mybatis.utils.web.lock.Lock;

@Slf4j
public class LockeHandlerTest {


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws Exception {

        LockHandlerDefault handler = new LockHandlerDefault();

        Lock lock = new Lock("1");
        Queue queue = QueueFactory.createUnordered();
        queue.addTask(() -> {
            handler.lock(lock);
            System.out.println("lock1===>");
            boolean flag1 = handler.tryLock(lock);
            System.out.println("lock1-1===>:" + flag1);
            boolean flag2 = handler.tryLock(lock);
            System.out.println("lock1-2===>:" + flag2);
            handler.lock(lock);
            System.out.println("lock1-3===");
            handler.unLock(lock);
            if(flag2)
                handler.unLock(lock);
            System.out.println("lock1-1<===:" + flag1);
            if(flag1)
                handler.unLock(lock);
            System.out.println("lock1-2<===:" + flag1);
            int timer = 0;
            while (timer < 100){
                try {Thread.sleep(10);
                } catch (Exception e) {}
                timer += 10;
                System.out.println("lock1===:" + timer);
            }
            handler.unLock(lock);
            System.out.println("lock1<===");

            try {Thread.sleep(40);
            } catch (Exception e) {}
            boolean flag4 = handler.tryLock(lock);
            System.out.println("lock1-4===>:" + flag4);
            if(flag4)
                handler.unLock(lock);
            System.out.println("lock1-4<===:" + flag4);
        });
        queue.addTask(() -> {
            try {Thread.sleep(20);
            } catch (Exception e) {}
            handler.lock(lock);
            System.out.println("lock2===>");
            int timer = 0;
            while (timer < 100){
                try {Thread.sleep(10);
                } catch (Exception e) {}
                timer += 10;
                System.out.println("lock2===:" + timer);
            }
            handler.unLock(lock);
            System.out.println("lock2<===");
        });
        queue.start();
        queue.await();
    }

    @Test
    public void test2() throws Exception {

        LockHandlerDefault handler = new LockHandlerDefault();
        Lock lock = new Lock("1");
        Queue queue = QueueFactory.createUnordered();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            queue.addTask(() -> {
                handler.lock(lock);
                System.out.println("lock" + finalI + "===>");
                int timer = 0;
                while (timer < 30){
                    try {Thread.sleep(10);
                    } catch (Exception e) {}
                    timer += 10;
                    System.out.println("lock" + finalI + "===:" + timer);
                }
                System.out.println("lock" + finalI + "<===");
                handler.unLock(lock);
            });
        }
        queue.start();
        queue.await();
    }

    @After
    public void tearDown() throws Exception {

    }



}
