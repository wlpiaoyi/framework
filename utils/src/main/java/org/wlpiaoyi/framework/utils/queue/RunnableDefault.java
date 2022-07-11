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
        void  queueRunnableEnd(Task task);
    }

    private WeakReference<Queue> queueWeakReference;

    private final Task task;
    private final Object taskSynTag;
    @Getter
    private final CountDownLatch countDownLatch;

    private WeakReference<Progress> progressWeakReference;

    RunnableDefault(final Task task, final Object taskSynTag, Progress progress){
        this.task = task;
        this.taskSynTag = taskSynTag;
        this.countDownLatch = new CountDownLatch(1);
        this.progressWeakReference = new WeakReference<>(progress);
    }

    @SneakyThrows
    @Override
    public void run() {
        this.task.run();
        if(this.progressWeakReference != null && this.progressWeakReference.get() != null){
            this.progressWeakReference.get().queueRunnableEnd(this.task);
        }
        synchronized (taskSynTag){
            this.taskSynTag.notify();
        }
        this.countDownLatch.countDown();
    }
}
