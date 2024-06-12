package org.wlpiaoyi.framework.sshd.shell;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ClientChannel;

import java.io.OutputStream;

@Slf4j
public class ExecShell implements Runnable{

    private final String shell;
    private final ClientChannel channel;
    private final OutputStream cmdOs;
    private final int timeOut;

//    @Getter
//    private Object userInfo;


    public static ExecShell build(String shell, ClientChannel channel, OutputStream cmdOs){
        return new ExecShell(shell, channel, cmdOs, 5 * 60 * 1000);
    }

    public static ExecShell build(String shell, ClientChannel channel, OutputStream cmdOs, int timeOut){
        return new ExecShell(shell, channel, cmdOs, timeOut);
    }

    private ExecShell(String shell, ClientChannel channel, OutputStream cmdOs, int timeOut){
        this.shell = shell;
        this.channel = channel;
        this.cmdOs = cmdOs;
        this.timeOut = timeOut;
    }

//    public ExecShell setUserInfo(Object userInfo){
//        this.userInfo = userInfo;
//        return this;
//    }


    @Override
    public void run() {
        CountDownShell countDown = CountDownShell.getCountDown(this.channel);
        try {
            if(countDown != null){
                countDown.plusCount();
            }
//            log.info("exec shell start for {}", this.userInfo);
            String cmdShell = shell;
            if(!cmdShell.endsWith("\n") && !cmdShell.endsWith("\n\r") && !cmdShell.endsWith("\r\n")){
                cmdShell = cmdShell + "\n\r";
            }
            cmdOs.write(cmdShell.getBytes());
            ShellExecutor.waitForChannel(channel, timeOut);
//            log.info("exec shell end for {}", this.userInfo);
        } catch (Exception e) {
            log.error("exec shell failed", e);
        } finally {
            if(countDown != null){
                countDown.minusCount();
            }
        }
    }
}
