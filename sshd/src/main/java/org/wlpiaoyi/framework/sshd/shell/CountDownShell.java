package org.wlpiaoyi.framework.sshd.shell;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.sshd.client.channel.ClientChannel;
import org.wlpiaoyi.framework.sshd.CountDown;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class CountDownShell implements CountDown {

    private int count;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final Object synTag = new Object();

    private ClientChannel channel;

    static final Map<ClientChannel, CountDownShell> CHANNEL_MAP = new HashMap<>();

    CountDownShell(@NonNull ClientChannel channel){
        this.channel = channel;
    }

    @SneakyThrows
    void awaitCount(){
        this.countDownLatch.await();
    }

    @SneakyThrows
    boolean awaitCount(long timeout, TimeUnit unit){
        return this.countDownLatch.await(timeout, unit);
    }

    void plusCount(){
        synchronized (this.synTag){
            if(this.channel == null){
                throw new BusinessException("channel key is null, may be channel is stop");
            }
            count ++;
            if(!CHANNEL_MAP.containsKey(this.channel)){
               CHANNEL_MAP.put(this.channel, this);
            }
        }
    }

    void minusCount(){
        synchronized (this.synTag){
            count --;
            if(count <= 0){
                this.countDownLatch.countDown();
                if(this.channel != null) CHANNEL_MAP.remove(this.channel);
                this.channel = null;
            }
        }
    }
}