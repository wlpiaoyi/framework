package org.wlpiaoyi.framework.shell;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class SshdExecutorTest {
    private SshdExecutor sshdExecutor;

    @Before
    public void setUp() throws Exception {
        this.sshdExecutor = SshdExecutor.build("172.16.22.118", 22, "root", "000000");
    }

    @SneakyThrows
    @Test
    public void test1(){
        byte[] bytes = new byte[1024];
        int lineEnd = '\n';
        final int[] index = {0};
        OutputStream repOs = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                bytes[index[0]] = (byte) b;
                index[0]++;
                if(b == lineEnd){
                    System.out.printf(new String(bytes, 0, index[0]));
                    index[0] = 0;
                }
            }
        };
        ByteArrayOutputStream errorOs = new ByteArrayOutputStream();
        SshdExecutor sshdExecutor = this.sshdExecutor;
        log.info("start exec");
        sshdExecutor.createChannel(repOs, errorOs, new SshdExecutor.ExecShell() {
            @Override
            public void exec(ClientChannel channel, PipedOutputStream cmdOs, OutputStream repOs, OutputStream errOs) {
                new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        Thread.sleep(1000);
                        cmdOs.write("cd ../ \n\r".getBytes());
                        log.debug("channel shell start 1");
                        SshdExecutor.waitForChannel(channel);
                        log.debug("channel shell end 1");
                    }
                }).start();

                new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        Thread.sleep(2000);
                        cmdOs.write("pwd \n\r".getBytes());
                        log.debug("channel shell start 2");
                        SshdExecutor.waitForChannel(channel);
                        log.debug("channel shell end 2");
                    }
                }).start();

                new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        Thread.sleep(3000);
                        cmdOs.write("ls \n\r".getBytes());
                        log.debug("channel shell start 3");
                        SshdExecutor.waitForChannel(channel);
                        log.debug("channel shell end 3");
                    }
                }).start();

                new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        Thread.sleep(4000);
                        cmdOs.write("cd /root \n\r".getBytes());
                        log.debug("channel shell start 4");
                        SshdExecutor.waitForChannel(channel);
                        log.debug("channel shell end 4");
                    }
                }).start();
                new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        Thread.sleep(5000);
                        cmdOs.write("ls \n\r".getBytes());
                        log.debug("channel shell start 5");
                        SshdExecutor.waitForChannel(channel);
                        log.debug("channel shell end 5");
                    }
                }).start();

                new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        Thread.sleep(10000);
                        cmdOs.write("date \n\r".getBytes());
                        log.debug("channel shell start 6");
                        SshdExecutor.waitForChannel(channel);
                        log.debug("channel shell end 6");
                    }
                }).start();

                new Thread(new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        Thread.sleep(20000);
                        cmdOs.write("date \n\r".getBytes());
                        log.debug("channel shell start 6");
                        SshdExecutor.waitForChannel(channel);
                        log.debug("channel shell end 6");
                    }
                }).start();
            }
        });
        log.info("end exec");
        while (true){
            Thread.sleep(1000);
        }
    }

    @After
    public void tearDown() throws Exception {
        this.sshdExecutor.sessionDisconnect();
    }

}
