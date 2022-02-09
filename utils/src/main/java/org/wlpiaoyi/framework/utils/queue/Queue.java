package org.wlpiaoyi.framework.utils.queue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务队列
 * @Author wlpiaoyi
 * @Date 2022/2/9 12:02 PM
 * @Version 1.0
 */
public class Queue{

    private class QueueRunnable implements Runnable {

        private WeakReference<Queue> queueWeakReference;

        QueueRunnable(Queue queue){
            this.queueWeakReference = new WeakReference(queue);
        }

        @Override
        public void run() {
            while (this.queueWeakReference.get().tasks.isEmpty() == false){
                try{
                    this.queueWeakReference.get().executeNext();
                    try {
                        Thread.sleep(heartbeatTimer <= 0 ? DEFAULT_HEARTBEAT_TIMER : heartbeatTimer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("QueueRunnable");
                }finally {
                    synchronized (this.queueWeakReference.get().isInTaskSynTag){
                        this.queueWeakReference.get().isInTask = false;
                    }
                }
            }
        }
    }

    private static Queue xQueueSync;

    /**队列心跳默认50毫秒**/
    public final static int DEFAULT_HEARTBEAT_TIMER = 50;
    public static int heartbeatTimer = DEFAULT_HEARTBEAT_TIMER;

    //任务数组
    private List<Task> tasks = new ArrayList<>();

    //=================单例==================>
    private Queue(){
    }
    public final static Queue singleInstance(){
        if(xQueueSync == null){
            synchronized (Queue.class){
                if(xQueueSync == null){
                    xQueueSync = new Queue();
                }
            }
        }
        return xQueueSync;
    }
    //<=================单例==================

    /**
     * 执行下一个任务
     * @return
     */
    private final Task executeNext(){
        if(this.tasks.isEmpty()) return null;
        Task task;
        synchronized (this.tasks){
            task = this.tasks.remove(0);
        }
        task.run();
        return task;
    }

    private boolean isInTask = false;
    private Object isInTaskSynTag = new Object();

    /**
     * 立即启动执行队列
     * 如果已启动则忽略
     */
    private void executeImmediate(){
        synchronized (this.isInTaskSynTag){
            if(this.isInTask) return;
            this.isInTask = true;
        }
        new Thread(new QueueRunnable(this)).start();
    }

    /**
     * 删除任务
     * @param task
     */
    public void remove(Task task){
        synchronized (this.tasks){
            this.tasks.remove(task);
        }
    }

    /**
     * 添加任务
     * @param task
     */
    public void addTask(Task task){
        synchronized (this.tasks){
            this.tasks.add(task);
            this.executeImmediate();
        }
    }

    /**
     * 等待完成
     */
    public synchronized void waitForComplete(){
        while (!this.tasks.isEmpty() && this.isInTask){
            try {
                Thread.sleep(heartbeatTimer <= 0 ? DEFAULT_HEARTBEAT_TIMER : heartbeatTimer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
