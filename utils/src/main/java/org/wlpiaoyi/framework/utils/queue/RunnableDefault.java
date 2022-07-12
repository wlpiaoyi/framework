package org.wlpiaoyi.framework.utils.queue;

import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

/**
 * 队列容器
 * @Author wlpiaoyi
 * @Date 2022/7/11 15:43
 * @Version 1.0
 */
class RunnableDefault implements Runnable {

    interface Progress{
        void beginQueueRunnable(Task task);
        void endQueueRunnable(Task task);
    }

    private final Task task;
    @Getter
    private final CountDownLatch countDownLatch;

    private WeakReference<Progress> progressWeakReference;

    RunnableDefault(final Task task, Progress progress){
        this.task = task;
        this.countDownLatch = new CountDownLatch(1);
        this.progressWeakReference = new WeakReference<>(progress);
    }

    @SneakyThrows
    @Override
    public void run() {

        if(this.progressWeakReference != null && this.progressWeakReference.get() != null){
            this.progressWeakReference.get().beginQueueRunnable(this.task);
        }
        this.task.run();
        if(this.progressWeakReference != null && this.progressWeakReference.get() != null){
            this.progressWeakReference.get().endQueueRunnable(this.task);
        }

        this.countDownLatch.countDown();
    }
}
