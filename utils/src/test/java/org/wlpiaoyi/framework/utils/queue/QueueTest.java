package org.wlpiaoyi.framework.utils.queue;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

@Slf4j
public class QueueTest implements QueueProgress{

    @Before
    public void setUp() throws Exception {

    }

    private final Object synTag = new Object();
    private volatile boolean isSyn = false;

    @Test
    public void test0() throws Exception {



    }

    @Test
    public void test1() throws Exception {

        Queue queue = QueueFactory.createSequence();
        queue.setQueueProgress(this);
        queue.addTask(() -> {
            System.out.println("to1->");
            int index = 100;
            while (index-- > 0){
                System.out.println("to1==" + index);
                if(index % 5 == 0){
                    synchronized (synTag){
                        isSyn = !isSyn;
                        if(!isSyn){
                            synTag.notify();
                        }
                    }
                }
                try {sleep(100);} catch (Exception e) {e.printStackTrace();}
            }
            System.out.println("to1<-");
        });
        queue.addTask(() -> {
            System.out.println("t1->");
            int index = 50;
            while (index-- > 0){
                System.out.println("t1==" + index);
                synchronized (synTag){
                    if(isSyn) {
                        try {synTag.wait();} catch (Exception e) {e.printStackTrace();}
                    }
                }
                try {sleep(100);} catch (Exception e) {e.printStackTrace();}
            }
            System.out.println("t1<-");
        });
        queue.addTask(() -> {
            System.out.println("t2->");
            int index = 10;
            while (index-- > 0){
                System.out.println("t2==" + index);
                try {sleep(100);} catch (Exception e) {e.printStackTrace();}
            }
            System.out.println("t2<-");
        });

        queue.addTask(() -> {
            System.out.println("t3->");
            int index = 10;
            while (index-- > 0){
                System.out.println("t3==" + index);
                try {sleep(100);} catch (Exception e) {e.printStackTrace();}
            }
            System.out.println("t3<-");
        });
        System.out.println("queue start");
        List<Task> taskList = queue.getWaitingTasks();
        Task task = taskList.get(0);
        queue.runTask(task);
        queue.start();
        System.out.println("queue await");
        queue.await();
        System.out.println("queue end");

        queue = QueueFactory.createUnordered();
        queue.setQueueProgress(this);
        queue.addTask(() -> {
            System.out.println("t4->");
            try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
            System.out.println("t4<-");
        });
        System.out.println("queue start");
        queue.start();
        queue.addTask(() -> {
            System.out.println("t5->");
            try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
            System.out.println("t5<-");
        });
        Queue finalQueue = queue;
        new Thread(() -> {
            finalQueue.addTask(() -> {
                System.out.println("t6->");
                int index = 10;
                while (index-- > 0){
                    System.out.println("t6==" + index);
                    try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
                }
                System.out.println("t6<-");
            });
            finalQueue.addTask(() -> {
                System.out.println("t7->");
                try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
                System.out.println("t7<-");
            });
            finalQueue.start();
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finalQueue.safeOption(() -> {
                finalQueue.addTask(() -> {
                    System.out.println("t6-1->");
                    int index = 10;
                    while (index-- > 0){
                        System.out.println("t6-1==" + index);
                        try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
                    }
                    System.out.println("t6-1<-");
                });
                finalQueue.addTask(() -> {
                    System.out.println("t7-1->");
                    try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
                    System.out.println("t7-1<-");
                });
                finalQueue.start();
            });
        }).start();
        queue.start();
        queue.await();
        System.out.println("queue end");
    }

    @Test
    public void test2() throws Exception {

        Queue queue1 = QueueFactory.createSequence();
        queue1.setQueueProgress(this);
        queue1.addTask(() -> {
            System.out.println("t1->");
            int index = 10;
            while (index-- > 0){
                System.out.println("t1==" + index);
                try {sleep(100);} catch (Exception e) {e.printStackTrace();}
            }
            System.out.println("t1<-");
        });
        queue1.addTask(() -> {
            System.out.println("t2->");
            int index = 30;
            while (index-- > 0){
                System.out.println("t2==" + index);
                try {sleep(100);} catch (Exception e) {e.printStackTrace();}
            }
            System.out.println("t2<-");
        });

        Queue queue2 = QueueFactory.createUnordered();
        queue2.setQueueProgress(this);
        queue2.addTask(() -> {
            System.out.println("a1->");
            int index = 20;
            while (index-- > 0){
                System.out.println("a1==" + index);
                try {sleep(100);} catch (Exception e) {e.printStackTrace();}
            }
            System.out.println("a1<-");
        });
        queue2.addTask(() -> {
            System.out.println("a2->");
            int index = 30;
            while (index-- > 0){
                System.out.println("a2==" + index);
                try {sleep(100);} catch (Exception e) {e.printStackTrace();}
            }
            System.out.println("a2<-");
        });
        System.out.println("queue1 start");
        queue1.start();
        System.out.println("queue2 start");
        queue2.start();
        System.out.println("queue2 await");
        queue2.await();
        System.out.println("queue1 await");
        queue1.await();
        System.out.println("queue end");
    }

//    private List<>

    @Test
    public void test3() throws Exception {

        List<String> tags = new ArrayList<>();
        Queue queue2 = QueueFactory.createUnordered();
        queue2.setQueueProgress(this);
        int i = 0;
        while (i < 10){
            int finalI = i;
            final String a = finalI + "";
            tags.add(a);
            queue2.addTask(() -> {

                System.out.println("a---" + finalI + "->");
                synchronized (a){
                    try {
                        a.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("a" + finalI + "->");
                int index = 10 + finalI;
                while (index-- > 0){
                    System.out.println("a" + finalI + ":" + index);
                    try {sleep(100);} catch (Exception e) {e.printStackTrace();}
                }
                System.out.println("a" + finalI + "<-");
            });
            i++;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<Integer> indexs = new ArrayList(){{
                    add(1);
                    add(8);
                    add(9);
                    add(2);
                    add(3);
                    add(4);
                    add(5);
                    add(6);
                    add(7);
                    add(0);
                }};
                for(int i = 0; i < indexs.size(); i++){
                    String a = tags.get(indexs.get(i));
                    synchronized (a){
                        System.out.println("notify:" + a);
                        a.notify();
                    }
                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        System.out.println("queue start");
        queue2.start();
        System.out.println("queue await");
        queue2.await();
        System.out.println("queue end");
    }


    @Test
    public void test4() throws Exception {
        Lock lock = new ReentrantLock();
        Queue queue = QueueFactory.createUnordered();
        queue.addTask(() -> {
            try {Thread.sleep(20);
            } catch (Exception e) {}
            lock.lock();
            System.out.println("lock1");
            int timer = 0;
            while (timer < 100){
                try {Thread.sleep(10);
                } catch (Exception e) {}
                timer += 10;
                System.out.println("lock1:" + timer);
            }
            lock.unlock();
        });
        queue.addTask(() -> {
            boolean flag = lock.tryLock();
            System.out.println("lock2:" + flag);
            int timer = 0;
            boolean flag1 = lock.tryLock();
            System.out.println("lock2-1:" + flag);
            if(flag1) lock.unlock();
            while (timer < 100){
                try {Thread.sleep(10);
                } catch (Exception e) {}
                timer += 10;
                System.out.println("lock2:" + timer);
            }
            if(flag) lock.unlock();
        });
//        queue.addTask(() -> {
//            try {Thread.sleep(30);
//            } catch (Exception e) {}
//            boolean flag = lock.tryLock();
//            System.out.println("lock2:" + flag);
//            int timer = 0;
//            while (timer < 50){
//                try {Thread.sleep(10);
//                } catch (Exception e) {}
//                timer += 10;
//                System.out.println("lock2:" + timer);
//            }
//            if(flag) lock.unlock();
//        });
        queue.start();
        queue.await();
    }


    @After
    public void tearDown() throws Exception {

        ThreadLocal fooThreadLocal = ThreadLocal.withInitial(QueueTest::new);
        fooThreadLocal.set("");
        Thread t = Thread.currentThread();

    }

    @Override
    public void beginQueue(Queue queue) {
        log.debug("beginQueue:" + queue);
    }

    @Override
    public void beginTask(Task task) {
        log.debug("beginTask:" + task);
    }

    @Override
    public void endTask(Task task) {
        log.debug("endTask:" + task);
    }

    @Override
    public void endQueue(Queue queue) {
        log.debug("endQueue:" + queue);

    }
}
