package org.wlpiaoyi.framework.utils.queue;

import lombok.SneakyThrows;

import java.lang.ref.WeakReference;

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


    private final Object synTagObj = new Object();
    private int runStatus = 0;

    private WeakReference<Progress> progressWeakReference;

    RunnableDefault(final Task task, Progress progress){
        this.task = task;
        this.progressWeakReference = new WeakReference<>(progress);
    }

    @SneakyThrows
    @Override
    public void run() {
        this.setRunStatus(1);
        if(this.progressWeakReference != null && this.progressWeakReference.get() != null){
            this.progressWeakReference.get().beginQueueRunnable(this.task);
        }
        this.task.run();
        if(this.progressWeakReference != null && this.progressWeakReference.get() != null){
            this.progressWeakReference.get().endQueueRunnable(this.task);
        }
        synchronized (this.synTagObj){
            this.setRunStatus(-1);
            this.synTagObj.notify();
        }
    }

    @SneakyThrows
    public final void await(){
        if(this.getRunStatus() < 0){
            return;
        }
        synchronized (this.synTagObj){
            this.synTagObj.wait();
        }
    }

    private final Object synRunStatus = new Object();
    private volatile boolean isInRunStatusSetting;
    public int getRunStatus() {
        if(!this.isInRunStatusSetting) {
            return this.runStatus;
        }
        synchronized (this.synRunStatus){
            return runStatus;
        }
    }

    private void setRunStatus(int runStatus) {
        synchronized (this.synRunStatus){
            this.isInRunStatusSetting = true;
            this.runStatus = runStatus;
        }
        this.isInRunStatusSetting = false;
    }
}
