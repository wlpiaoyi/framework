package org.wlpiaoyi.framework.mybatis.utils.aes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AesTest {

    volatile boolean isWait = false;
    Object tobj = new Object();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws Exception {

        Thread thread1 = new Thread(() -> {
            while (true){
                synchronized (tobj){
                    try {
                        if(isWait){
                            System.out.println("thread1 wait->");
                            tobj.wait();
                            System.out.println("thread1 wait<-");
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("thread1:" + System.currentTimeMillis());
                }
            }
        });
        thread1.start();
        Thread.sleep(2000);
        isWait = true;
        Thread.sleep(4000);
        isWait = false;
        synchronized (tobj){
            tobj.notify();
        }
        Thread.sleep(5000);

        Aes aes = Aes.create().setKey("abcd567890ABCDEF1234567890ABCDEF").setIV("abcd567890123456").load();
        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = aes.encrypt(data);
        String pstr = new String(encodedData);
        System.out.println("加密后文字：\r\n" + pstr);
        byte[] decodedData = aes.decrypt(encodedData);
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);
        String a = new String(aes.decrypt("R0trXMrMa+M5fvfbpRuiQB9jtTF58Yl66D9ormTT/y9ZJmFZqVx7+8/lc7P7fr5e5Tfl6PwC1pWpuGEqg0jE4w==".getBytes()));
        System.out.println("---" + a);

    }


    @After
    public void tearDown() throws Exception {

    }

    public static void main(String[] args){
        ThreadB b = new ThreadB();
        b.start();
        System.out.println("b is start....");
        synchronized(b)//括号里的b是什么意思,起什么作用?
        {
            try{
                System.out.println("Waiting for b to complete...");
                b.wait();//这一句是什么意思，究竟让谁wait?
                System.out.println("Completed.Now back to main thread");
            }catch (InterruptedException e){}
        }
        System.out.println("Total is :"+b.total);

    }


    static class ThreadB extends Thread {

        int total;

        public void run() {

            synchronized (this) {

                System.out.println("ThreadB is running..");

                for (int i = 0; i < 100; i++) {

                    total += i;

                    System.out.println("total is " + total);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                notify();

            }

        }
    }

}
