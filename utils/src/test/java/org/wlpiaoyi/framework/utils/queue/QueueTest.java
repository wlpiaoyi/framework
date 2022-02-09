package org.wlpiaoyi.framework.utils.queue;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class QueueTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Queue.singleInstance().addTask(new Task() {
                    @Override
                    public void run() {
                        System.out.println("1  " + System.currentTimeMillis());
                    }
                });
                Queue.singleInstance().addTask(new Task() {
                    @Override
                    public void run() {
                        System.out.println("2  " + System.currentTimeMillis());
                    }
                });
                Queue.singleInstance().addTask(new Task() {
                    @Override
                    public void run() {
                        System.out.println("3  " + System.currentTimeMillis());
                    }
                });
                System.out.println("---  " + System.currentTimeMillis());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Queue.singleInstance().addTask(new Task() {
                    @Override
                    public void run() {
                        System.out.println("4  " + System.currentTimeMillis());
                    }
                });

                Queue.singleInstance().addTask(new Task() {
                    @Override
                    public void run() {
                        System.out.println("5  " + System.currentTimeMillis());
                    }
                });

                System.out.println("---  " + System.currentTimeMillis());

            }
        }).start();

        Thread.sleep(1000 * 999);
    }


    @After
    public void tearDown() throws Exception {

    }
}
