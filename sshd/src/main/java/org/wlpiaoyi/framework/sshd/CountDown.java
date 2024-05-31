package org.wlpiaoyi.framework.sshd;


import lombok.NonNull;
import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class CountDown {

    private int count = 0;

    private CountDownLatch countDownLatch;

    private final Object synTag = new Object();

    private Object channel;

    protected static final Map<Object, CountDown> CHANNEL_MAP = new HashMap<>();

    protected CountDown(@NonNull Object channel){
        this.channel = channel;
    }

    @SneakyThrows
    protected void awaitCount(){
        this.countDownLatch.await();
    }

    @SneakyThrows
    protected boolean awaitCount(long timeout, TimeUnit unit){
        return this.countDownLatch.await(timeout, unit);
    }

    protected void plusCount(){
        synchronized (this.synTag){
            if(this.channel == null){
                throw new BusinessException("channel key is null, may be channel is stop");
            }
            if(count == 0){
                this.firstPlusCount();
            }
            count ++;
            if(countDownLatch == null){
                countDownLatch = new CountDownLatch(1);
            }
            if(!CHANNEL_MAP.containsKey(this.channel)){
                CHANNEL_MAP.put(this.channel, this);
            }
        }
    }

    protected abstract void firstPlusCount();

    protected boolean minusCount(){
        if(this.countDownLatch == null){
            return false;
        }
        synchronized (this.synTag){
            if(this.countDownLatch == null){
                return false;
            }
            if(this.channel == null){
                throw new BusinessException("channel key is null, may be channel is stop");
            }
            count --;
            if(count <= 0){
                this.countDownLatch.countDown();
                if(this.channel != null) CHANNEL_MAP.remove(this.channel);
                this.channel = null;
                this.countDownLatch = null;
                this.lastPlusCount();
            }
        }
        return true;
    }



    protected abstract void lastPlusCount();

}
