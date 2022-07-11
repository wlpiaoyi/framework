package org.wlpiaoyi.framework.utils.queue;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

@Slf4j
public class QueueTest {

    @Before
    public void setUp() throws Exception {

    }



    @Test
    public void test1() throws Exception {

        Queue queue = QueueFactory.createDefault();
        queue.addTask(() -> {
            System.out.println("t1->");
            try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
            System.out.println("t1<-");
        });
        queue.addTask(() -> {
            System.out.println("t2->");
            try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
            System.out.println("t2<-");
        });
        queue.addTask(() -> {
            System.out.println("t3->");
            try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
            System.out.println("t3<-");
        });
        System.out.println("queue start");
        queue.start();
        System.out.println("queue await");
        queue.await();
        System.out.println("queue end");

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                queue.addTask(() -> {
                    System.out.println("t6->");
                    int index = 5;
                    while (index-- > 0){
                        System.out.println("t6==" + index);
                        try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
                    }
                    System.out.println("t6<-");
                });
                queue.addTask(() -> {
                    System.out.println("t7->");
                    try {sleep(1000);} catch (Exception e) {e.printStackTrace();}
                    System.out.println("t7<-");
                });
//                List<Task> taskList = queue.getWaitingTasks();
//                Task task = taskList.get(taskList.size() - 2);
//                queue.runTask(task);

            }
        }).start();
        queue.start();
//        queue.await();
        Thread.sleep(10000);
        System.out.println("queue end");
    }




    @After
    public void tearDown() throws Exception {

    }
}
