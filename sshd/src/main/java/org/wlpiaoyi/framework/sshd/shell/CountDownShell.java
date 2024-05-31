package org.wlpiaoyi.framework.sshd.shell;

import lombok.NonNull;
import org.apache.sshd.client.channel.ClientChannel;
import org.wlpiaoyi.framework.sshd.CountDown;
import org.wlpiaoyi.framework.utils.MapUtils;
import java.util.concurrent.TimeUnit;

class CountDownShell extends CountDown {

    static void putCountDown(ClientChannel key, CountDownShell value){
        CountDown.CHANNEL_MAP.put(key, value);
    }
    static CountDownShell getCountDown(ClientChannel key){
        return MapUtils.get(CountDown.CHANNEL_MAP, key);
    }

    CountDownShell(@NonNull ClientChannel channel){
        super(channel);
    }

    public void awaitCount(){
        super.awaitCount();
    }

    public boolean awaitCount(long timeout, TimeUnit unit){
        return super.awaitCount(timeout, unit);
    }

    public void plusCount(){
        super.plusCount();
    }

    @Override
    protected void firstPlusCount() {

    }

    public boolean minusCount(){
        return super.minusCount();
    }

    @Override
    protected void lastPlusCount() {

    }
}