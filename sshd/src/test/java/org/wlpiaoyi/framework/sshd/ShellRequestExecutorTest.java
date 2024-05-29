package org.wlpiaoyi.framework.sshd;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ClientChannel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.sshd.shell.ExecShell;
import org.wlpiaoyi.framework.sshd.shell.ShellExecutor;
import org.wlpiaoyi.framework.sshd.shell.ShellRequestExec;
import org.wlpiaoyi.framework.sshd.shell.ShellResponseListener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class ShellRequestExecutorTest {
    private ShellExecutor shellExecutor;

    @Before
    public void setUp() throws Exception {
        this.shellExecutor = ShellExecutor.build("172.16.22.118", 22, "root");
        this.shellExecutor.connectSession("000000");
    }

    @SneakyThrows
    @Test
    public void test1(){
        ShellExecutor shellExecutor = this.shellExecutor;
        log.info("start exec");
        shellExecutor.createChannel((channel, cmdOs) -> {
            List<String> shells = new ArrayList<>();
            shells.add("pwd");
            shells.add("cd ../");
            shells.add("pwd");
            shells.add("ls");
            shells.add("cd ~");
            shells.add("pwd");
            shells.add("ls");
            shells.add("date");
            Random random = new Random();
            for (String shell : shells) {
                try {
                    Thread.sleep(Math.abs(random.nextInt() % 1000) + 1000);
                    ShellExecutor.execShell(ExecShell.build(shell, channel, cmdOs));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },
                (channelShell, buffer, bufferLength) -> System.out.printf(new String(buffer, 0, bufferLength)),
                (channelShell, buffer, bufferLength) -> System.out.printf(new String(buffer, 0, bufferLength)),
                1024);

        log.info("end exec");
        while (true){
            Thread.sleep(1000);
        }
    }

    @After
    public void tearDown() throws Exception {
        this.shellExecutor.destroyed();
    }

}
