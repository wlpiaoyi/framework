package org.wlpiaoyi.framework.utils.encrypt.aes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class AesTest {

    volatile boolean isWait = false;
    Object tobj = new Object();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() throws Exception {

//        Thread thread1 = new Thread(() -> {
//            while (true){
//                synchronized (tobj){
//                    try {
//                        if(isWait){
//                            System.out.println("thread1 wait->");
//                            tobj.wait();
//                            System.out.println("thread1 wait<-");
//                        }
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("thread1:" + System.currentTimeMillis());
//                }
//            }
//        });
//        thread1.start();
//        Thread.sleep(2000);
//        isWait = true;
//        Thread.sleep(4000);
//        isWait = false;
//        synchronized (tobj){
//            tobj.notify();
//        }
//        Thread.sleep(5000);

//        Aes aes = Aes.create().setKey("abcd567890ABCDEF1234567890ABCDEF").setIV("abcd567890123456").load();

        Aes aes = Aes.create().setKey(StringUtils.getUUID32()).setIV(StringUtils.getUUID32().substring(0, 16)).load();
        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗这是一行没有任何意义的文字，你看完了等于没看，不是吗";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = aes.encrypt(data);
        String pstr = new String(StringUtils.base64Encode(encodedData));
        System.out.println("加密后文字：\r\n" + pstr);
        byte[] decodedData = aes.decrypt(StringUtils.base64DecodeToBytes(pstr));
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);

    }

    @Test
    public void test2() throws Exception {
        String path = DataUtils.USER_DIR + "/target/test/aes_simple.mp4";
        File file = new File(path);
        Aes aes = Aes.create().setKey(StringUtils.getUUID32()).setIV(StringUtils.getUUID32().substring(0, 16)).load();
        FileInputStream in = new FileInputStream(file);
        File dFile = new File(DataUtils.USER_DIR + "/target/test/d_data.mp4");
        if(dFile.exists()){
            dFile.delete();
        }
        FileOutputStream out = new FileOutputStream(dFile);
        try{
            aes.encryptSection(in, out);
        }finally {
            in.close();
            out.flush();
            out.close();
        }

        in = new FileInputStream(dFile);
        File eFile = new File(DataUtils.USER_DIR + "/target/test/e_data.mp4");
        if(eFile.exists()){
            eFile.delete();
        }
        out = new FileOutputStream(eFile);
        try{
            aes.decryptSection(in, out);
        }finally {
            in.close();
            out.flush();
            out.close();
        }


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
