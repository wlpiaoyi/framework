package org.wlpiaoyi.framework.shell;

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

@Slf4j
public class SshdExecutor {

    public interface ExecShell{
        void exec(ClientChannel channel, PipedOutputStream cmdOs, OutputStream repOs, OutputStream errOs);
    }

    private final SshClient client;
    private final ClientSession session;
    public static int SESSION_TIME_OUT = 5 * 60 * 1000;

    private SshdExecutor(){
        client = null;
        session = null;
    }

    public static SshdExecutor build(String ip, Integer port, String user){
        return new SshdExecutor(ip, port, user);
    }
    public static SshdExecutor build(String ip, Integer port, String user, String password){
        return new SshdExecutor(ip, port, user, password);
    }
    public static SshdExecutor build(String ip, Integer port, String user, String keyName, String publicKey){
        return new SshdExecutor(ip, port, user, keyName, publicKey);
    }

    private SshdExecutor(String ip, Integer port, String user) {
        client = SshClient.setUpDefaultClient();
        client.start();
        try {
            session = client.connect(user, ip, port).verify(SESSION_TIME_OUT).getSession();
        } catch (IOException e) {
            log.error("init session error!", e);
            throw new BusinessException(e);
        }
    }

    //密码方式
    private SshdExecutor(String ip, Integer port, String user, String password) {
        client = SshClient.setUpDefaultClient();
        client.start();
        try {
            session = client.connect(user, ip, port).verify(SESSION_TIME_OUT).getSession();
        } catch (IOException e) {
            log.error("init session error!", e);
            throw new BusinessException(e);
        }
        session.addPasswordIdentity(password);
    }

    //公钥方式
    private SshdExecutor(String ip, Integer port, String user, String keyName, String publicKey) {
        client = SshClient.setUpDefaultClient();
        client.start();
        try {
            session = client.connect(user, ip, port).verify(SESSION_TIME_OUT).getSession();
        } catch (IOException e) {
            log.error("init session error!", e);
            throw new BusinessException(e);
        }
        try {
            session.addPublicKeyIdentity(SecurityUtils.extractEDDSAKeyPair(new ByteArrayBuffer(publicKey.getBytes()), keyName));
        } catch (GeneralSecurityException e) {
            log.error("init session for add auth key error!", e);
            throw new BusinessException(e);
        }
    }


    public void sessionDisconnect(){
        if(this.session != null){
            try{
                this.session.close();
            } catch (IOException e) {
                log.error("session close error!", e);
            }
        }
        if (client != null) {
            try {
                client.stop();
                client.close();
            } catch (IOException e) {
                log.error("client close error!", e);
            }
        }
    }
    public void createChannel(OutputStream repOs, OutputStream errOs, ExecShell execShell) {
        try {
            if (!session.auth().verify(10 * 1000).isSuccess()) {
                throw new BusinessException("auth failed");
            }
            PipedOutputStream cmdOs = new PipedOutputStream();
            PipedInputStream cmdIo = new PipedInputStream(cmdOs);
            ClientChannel channel = session.createShellChannel();
            channel.setIn(cmdIo);
            channel.setOut(repOs);
            channel.setErr(errOs);
            if (!channel.open().verify(1000).isOpened()) {
                throw new BusinessException("auth failed");
            }
            new Thread(() -> execShell.exec(channel, cmdOs, repOs, errOs)).start();
            waitForChannel(channel);
        } catch (Exception e) {
            log.error("channel error!", e);
            throw new BusinessException(e);
        }
    }

    public static void waitForChannel(ClientChannel channel){
        waitForChannel(channel, 30 * 1000);
    }
    public static void waitForChannel(ClientChannel channel, int timeOut){
        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), timeOut);
    }



}
