package org.wlpiaoyi.framework.utils.queue;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务队列
 * @Author wlpiaoyi
 * @Date 2022/2/9 12:02 PM
 * @Version 1.0
 */
public class Queue{

    private static Queue xQueueSync;

    public final static int DEFAULT_HEARTBEAT_TIMER = 50;
    //队列心跳默认50毫秒
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
        new Thread(() -> {
            System.out.println("start execute");
            while (this.tasks.isEmpty() == false){
                try{
                    Queue.singleInstance().executeNext();
                    try {
                        Thread.sleep(heartbeatTimer <= 0 ? DEFAULT_HEARTBEAT_TIMER : heartbeatTimer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("executing");
                }finally {
                    synchronized (this.isInTaskSynTag){
                        this.isInTask = false;
                    }
                }
            }
        }).start();
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

}
