package org.wlpiaoyi.framework.sshd;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.buffer.ByteArrayBuffer;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public abstract class SshdClient {

    private final String        ip;
    private final Integer       port;
    private final String        user;

    private final SshClient     client;
    private ClientSession session;
    private boolean isAuthedForSession;

    public static int SESSION_TIME_OUT = 5 * 60 * 1000;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();

    protected SshdClient(String ip, Integer port, String user) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        client = SshClient.setUpDefaultClient();
        client.start();
    }

    public void connectSession(){
        synchronized (this.client){
            this.disconnectSession();
            try {
                session = client.connect(this.user, this.ip, this.port).verify(SESSION_TIME_OUT).getSession();
                this.isAuthedForSession = false;
            } catch (IOException e) {
                log.error("init session error!", e);
                throw new BusinessException(e);
            }
        }
    }
    public void connectSession(String password){
        this.connectSession();
        this.getSession().addPasswordIdentity(password);
    }

    public void connectSession(String keyName, String publicKey){
        this.connectSession();
        try {
            this.getSession().addPublicKeyIdentity(SecurityUtils.extractEDDSAKeyPair(new ByteArrayBuffer(publicKey.getBytes()), keyName));
        } catch (GeneralSecurityException e) {
            log.error("init session error!", e);
            throw new BusinessException(e);
        }
    }

    protected void authSessionIfNeed() throws IOException {
        if(this.isAuthedForSession){
            return;
        }
        if (!this.getSession().auth().verify(10 * 1000).isSuccess()) {
            throw new BusinessException("auth failed");
        }
        this.isAuthedForSession = true;
    }

    public void disconnectSession(){
        synchronized (this.client){
            if(this.session != null){
                try{
                    this.session.close();
                } catch (IOException e) {
                    log.error("session close error!", e);
                }
                this.session = null;
            }
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

    protected ClientSession getSession() {
        return session;
    }

    public static void threadRun(Runnable runnable){
        EXECUTOR_SERVICE.submit(runnable);
    }
}
