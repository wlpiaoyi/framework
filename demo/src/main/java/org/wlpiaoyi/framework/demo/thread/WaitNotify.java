package org.wlpiaoyi.framework.demo.thread;

/**
 * 线程通信
 * @Author wlpiaoyi
 * @Date 2022/9/16 17:35
 * @Version 1.0
 */
public class WaitNotify {


    private static volatile int tagIndex = 0;
    public static synchronized int NextTagIndex(){
        return tagIndex ++;
    }

    private final static Object SynObj = new Object();
    //等待 wait 和 notify不能在同一线程
    public static final void ForWait(){
        synchronized (SynObj){
            int index = NextTagIndex();
            System.out.println("wait begin for index:" + index + " timer:" + System.currentTimeMillis());
            try {SynObj.wait();} catch (InterruptedException e)
            {e.printStackTrace();}
            System.out.println("wait end for index:" + index + " timer:" + System.currentTimeMillis());
        }
    }
    //唤醒
    public static final void ForNotify(){
        synchronized (SynObj){
            SynObj.notify();
            System.out.println("notify for timer:" + System.currentTimeMillis());
        }
    }

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    System.out.println("coming");
                    ForWait();
                    System.out.println("do something...");
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {Thread.sleep(1000);} catch (InterruptedException e)
                {throw new RuntimeException(e);}
                ForNotify();

                try {Thread.sleep(1200);} catch (InterruptedException e)
                {throw new RuntimeException(e);}
                ForNotify();

                try {Thread.sleep(500);} catch (InterruptedException e)
                {throw new RuntimeException(e);}
                ForNotify();
            }
        }).start();
    }

}
