package org.wlpiaoyi.framework.sshd.shell;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.buffer.ByteArrayBuffer;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class ShellExecutor {

    private final SshClient     client;
    private final String        ip;
    private final Integer       port;
    private final String        user;

    private ClientSession       session;

    public static int SESSION_TIME_OUT = 5 * 60 * 1000;
    public static int CHANNEL_TIME_OUT = 60 * 1000;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();

    public static ShellExecutor build(String ip, Integer port, String user){
        return new ShellExecutor(ip, port, user);
    }

    private ShellExecutor(String ip, Integer port, String user) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        client = SshClient.setUpDefaultClient();
        client.start();
    }

    public void connectSession(){
        this.disconnectSession();
        try {
            session = client.connect(this.user, this.ip, this.port).verify(SESSION_TIME_OUT).getSession();
        } catch (IOException e) {
            log.error("init session error!", e);
            throw new BusinessException(e);
        }
    }

    public void connectSession(String password){
        this.connectSession();
        session.addPasswordIdentity(password);
    }

    public void connectSession(String keyName, String publicKey){
        this.connectSession();
        try {
            session.addPublicKeyIdentity(SecurityUtils.extractEDDSAKeyPair(new ByteArrayBuffer(publicKey.getBytes()), keyName));
        } catch (GeneralSecurityException e) {
            log.error("init session error!", e);
            throw new BusinessException(e);
        }
    }

    public void disconnectSession(){
        if(this.session != null){
            try{
                this.session.close();
            } catch (IOException e) {
                log.error("session close error!", e);
            }
            this.session = null;
        }
    }

    public void destroyed(){
        this.disconnectSession();
        if (client.isStarted()) {
            try {
                client.stop();
                client.close();
            } catch (IOException e) {
                log.error("client close error!", e);
            }
        }
    }

    public void createChannel(ShellRequestExec requestExec, ShellResponseListener responseSuccessListener, ShellResponseListener responseErrorListener, final int bufferLength) {
        createChannel(requestExec, responseSuccessListener, responseErrorListener, bufferLength, CHANNEL_TIME_OUT);
    }

    public void createChannel(ShellRequestExec requestExec, ShellResponseListener responseSuccessListener, ShellResponseListener responseErrorListener, final int bufferLength, int channelTimeOut) {
        try {
            if (!session.auth().verify(10 * 1000).isSuccess()) {
                throw new BusinessException("auth failed");
            }
            PipedOutputStream cmdOs = new PipedOutputStream();
            ClientChannel channel = session.createShellChannel();
            channel.setIn(new PipedInputStream(cmdOs));
            channel.setOut(new ResponseOutputStream(bufferLength, channel, responseSuccessListener));
            channel.setErr(new ResponseOutputStream(bufferLength, channel, responseErrorListener));
            if (!channel.open().verify(1000).isOpened()) {
                throw new BusinessException("auth failed");
            }
            final ClientChannel finalChannel = channel;
            EXECUTOR_SERVICE.submit(() -> requestExec.exec(finalChannel, cmdOs));
            waitForChannel(channel, channelTimeOut);
        } catch (Exception e) {
            log.error("channel error!", e);
            throw new BusinessException(e);
        }
    }

    public static void waitForChannel(ClientChannel channel, int timeOut){
        channel.waitFor(EnumSet.of(ClientChannelEvent.TIMEOUT), timeOut);
    }

    public static Future<?> execShell(ExecShell execShell){
        return EXECUTOR_SERVICE.submit(execShell);
    }
}
