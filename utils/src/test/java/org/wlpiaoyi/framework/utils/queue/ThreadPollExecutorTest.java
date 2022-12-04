package org.wlpiaoyi.framework.utils.queue;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @Author wlpiaoyi
 * @Date 2022/9/15 11:21
 * @Version 1.0
 */
@Slf4j
public class ThreadPollExecutorTest {

    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            5, 50, 300, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(50),
            new ThreadFactory(){ public Thread newThread(Runnable r) {
                return new Thread(r, "schema_task_pool_" + r.hashCode());
            }}, new ThreadPoolExecutor.DiscardOldestPolicy());

    private static volatile int a = 0;

    public static synchronized int plusA(){
        a ++;
        return a;
    }

    public void callableTest(){
        Future<Boolean> future = threadPool.submit(new Callable<Boolean>(){
            @Override
            public Boolean call() throws Exception {
                int b = plusA() + 100;
                System.out.println("run:" + b + " " + Thread.currentThread());
                return true;
            }
        });
        try {
            System.out.println("feature.get");
            Boolean boolean1 = future.get();
            System.out.println(boolean1);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException...");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("execute exception...");
            e.printStackTrace();
        }
    }

    static class ResultThread<T> implements Runnable {
        T data;

        public ResultThread(T name) {
            this.data = name;
        }

        @Override
        public void run() {
            int b = plusA() + 100;
            System.out.println("run2:" + b + " " + Thread.currentThread());
        }
    }
    public <T> void callableTest2(T result){
        Future<T> future = (Future<T>) threadPool.submit(new ResultThread<T>(result), result);
        try {
            System.out.println("feature.get");
            T boolean1 = future.get();
            System.out.println(boolean1);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException...");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("execute exception...");
            e.printStackTrace();
        }
    }


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test0() throws Exception {

        int i = 100;
        while (i -- > 0){
            this.callableTest2(i);
        }
        Thread.sleep(1000 * 10);
    }

    @After
    public void tearDown() throws Exception {

    }

}
