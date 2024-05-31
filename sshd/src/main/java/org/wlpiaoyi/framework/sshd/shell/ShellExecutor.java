package org.wlpiaoyi.framework.sshd.shell;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.wlpiaoyi.framework.sshd.CountDown;
import org.wlpiaoyi.framework.sshd.SshdClient;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;


@Slf4j
public class ShellExecutor extends SshdClient {

    public static ShellExecutor build(String ip, Integer port, String user){
        return new ShellExecutor(ip, port, user);
    }

    private ShellExecutor(String ip, Integer port, String user) {
        super(ip, port, user);
    }

    public CountDown createChannel(@NonNull ShellRequestExec requestExec, @NonNull ShellResponseListener responseSuccessListener, ShellResponseListener responseErrorListener) {
        return createChannel(requestExec, responseSuccessListener, responseErrorListener, 1024, 5000);
    }

    public CountDown createChannel(@NonNull ShellRequestExec requestExec, @NonNull ShellResponseListener responseSuccessListener, ShellResponseListener responseErrorListener, final int bufferLength, int channelTimeOut) {
        CountDownShell countDown = null;
        try {
            this.authSessionIfNeed();
            ClientChannel channel = this.getSession().createShellChannel();
            countDown = new CountDownShell(channel);
            countDown.plusCount();
            PipedOutputStream cmdOs = new PipedOutputStream();
            channel.setIn(new PipedInputStream(cmdOs));
            channel.setOut(new ResponseOutputStream(bufferLength, channel, responseSuccessListener));
            if(responseErrorListener != null){
                channel.setErr(new ResponseOutputStream(bufferLength, channel, responseErrorListener));
            }
            if (!channel.open().verify(10 * 1000).isOpened()) {
                throw new BusinessException("channel auth failed");
            }
            final ClientChannel finalChannel = channel;
            CountDownShell finalCountDown = countDown;
            SshdClient.threadRun(() -> requestExec.exec(finalChannel, cmdOs, finalCountDown));
            waitForChannel(channel, channelTimeOut);
        } catch (Exception e) {
            log.error("channel error!", e);
            throw new BusinessException(e);
        } finally {
            if(countDown != null){
                countDown.minusCount();
            }
        }
        return countDown;
    }

    public static void await(CountDown countDown){
        if(countDown == null){
            return;
        }
        ((CountDownShell) countDown).awaitCount();
    }

    public static void await(CountDown countDown, long timeout, TimeUnit unit){
        if(countDown == null){
            return;
        }
        ((CountDownShell) countDown).awaitCount(timeout, unit);
    }

    public static void waitForChannel(ClientChannel channel, int timeOut){
        channel.waitFor(EnumSet.of(ClientChannelEvent.TIMEOUT), Math.max(Math.min(timeOut, 60 * 60 * 1000), 100));
    }

    public static void execShell(ExecShell execShell){
        SshdClient.threadRun(execShell);
    }
}
