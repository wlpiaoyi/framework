package org.wlpiaoyi.framework.demo.AQS;

import org.wlpiaoyi.framework.utils.queue.Queue;
import org.wlpiaoyi.framework.utils.queue.QueueFactory;
import org.wlpiaoyi.framework.utils.queue.Task;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/26 12:07
 * @Version 1.0
 */
public class ReentrantLockTest {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock(true);
        Queue queue = QueueFactory.createUnordered();
        queue.addTask(new Task() {
            @Override
            public void run() {
                try{
                    System.out.println("a1->start");
                    lock.tryLock(2000, TimeUnit.MILLISECONDS);
                    System.out.println("a1->ing");
                    int i = 30;
                    while (i > 0){
                        Thread.sleep(100);
                        i--;
                        System.out.println("a2->i:" + i);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    lock.unlock();
                    System.out.println("a1->end");
                }
            }
        });
        queue.addTask(new Task() {
            @Override
            public void run() {
                try{
                    Thread.sleep(100);
                    System.out.println("a2->start");
                    lock.tryLock(2000, TimeUnit.MILLISECONDS);
                    System.out.println("a2->ing");
                    int i = 10;
                    while (i > 0){
                        Thread.sleep(100);
                        i--;
                        System.out.println("a2->i:" + i);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    lock.unlock();
                    System.out.println("a2->end");
                }
            }
        });
        queue.start();
        queue.await();

    }

}
