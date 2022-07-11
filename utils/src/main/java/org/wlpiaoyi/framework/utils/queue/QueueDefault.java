package org.wlpiaoyi.framework.utils.queue;

import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/11 16:09
 * @Version 1.0
 */
class QueueDefault implements Queue, RunnableDefault.Progress {

    //任务数组
    private final List<Task> waitingTasks = new ArrayList<>();
    private final Map<Task, RunnableDefault> waitingRunnable = new HashMap<>();
    private final List<Task> doingTasks = new ArrayList<>();
    private final Map<Task, RunnableDefault> doingRunnable = new HashMap<>();
    private Object taskSynTag = new Object();


    @Getter
    private volatile boolean isInQueue = false;

    public boolean inQueue(){
        synchronized (this.waitingTasks){
            return this.isInQueue;
        }
    }

    public void beginQueue(){
        synchronized (this.waitingTasks){
            this.isInQueue = true;
        }
    }
    private void endQueue(){
        synchronized (this.waitingTasks) {
            this.isInQueue = false;
            this.doingTasks.clear();
            this.doingRunnable.clear();
            this.waitingTasks.clear();
            this.waitingRunnable.clear();
        }
    }

    @Override
    public List<Task> getWaitingTasks() {
        synchronized (this.waitingTasks){
            List<Task> list = this.waitingTasks;
            return new ArrayList(){{
                addAll(list);
            }};
        }
    }

    @Override
    public List<Task> getDoingTasks() {
        synchronized (this.waitingTasks){
            List<Task> list = this.doingTasks;
            return new ArrayList(){{
                addAll(list);
            }};
        }
    }

    public Thread runTask(Task task) {
        RunnableDefault run = this.nextQueueRunnable(task);
        this.remove(task);
        Thread thread = new Thread(run);
        synchronized (this.taskSynTag){
            thread.start();
        }
        return thread;
    }


    @Override
    public int start() {
        synchronized (this.waitingTasks){

            if(this.inQueue()) return this.waitingTasks.size();

            this.beginQueue();
            if(this.waitingTasks.isEmpty()){
                this.endQueue();
                return 0;
            }

            QueueDefault queue = this;
            new Thread(() -> {
                Task task = queue.nextTask();
                if(task == null) return;
                do{
                    queue.runTask(task);
//                    try {
//                        queue.runTask(task).join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    task = queue.nextTask();
                }while (task != null);
                this.endQueue();
            }).start();

            return this.waitingTasks.size();
        }
    }



    private Task nextTask(){

        synchronized (this.waitingTasks){
            if(this.waitingTasks.isEmpty()) return null;
            Task task = this.waitingTasks.get(0);
            synchronized (this.waitingTasks){
                this.waitingTasks.remove(task);
            }
            this.doingTasks.add(task);
            return task;
        }
    }

    private RunnableDefault nextQueueRunnable(Task task){
        synchronized (this.waitingTasks){
            if(this.waitingRunnable.isEmpty()) return null;
            RunnableDefault run = this.waitingRunnable.get(task);
            synchronized (this.waitingTasks){
                this.waitingRunnable.remove(task);
                this.doingRunnable.put(task, run);
            }
            return run;
        }
    }

    /**
     * 删除任务
     * @param task
     */
    public void remove(Task task){
        synchronized (this.waitingTasks){
            this.waitingTasks.remove(task);
            this.waitingRunnable.remove(task);
        }
    }

    /**
     * 添加任务
     * @param task
     */
    public void addTask(Task task){
        synchronized (this.waitingTasks){
            this.waitingTasks.add(task);
            RunnableDefault queueRunnable = new RunnableDefault(task, this.taskSynTag, this);
            this.waitingRunnable.put(task, queueRunnable);
        }
    }


    @SneakyThrows
    @Override
    public void await() {
        if(this.isInQueue == false) return;
        while (!this.doingTasks.isEmpty()){
            CountDownLatch countDownLatch;
            synchronized (this.waitingTasks){
                if(this.doingTasks.isEmpty()){
                    return;
                }
                Task task = this.doingTasks.get(0);
                RunnableDefault queueRunnable = this.doingRunnable.get(task);
                countDownLatch = queueRunnable.getCountDownLatch();
            }
            if(countDownLatch.getCount() > 0){
                countDownLatch.await();
            }
        }
        while (!this.waitingTasks.isEmpty()){
            CountDownLatch countDownLatch;
            synchronized (this.waitingTasks){
                if(this.waitingTasks.isEmpty()){
                    return;
                }
                Task task = this.waitingTasks.get(0);
                RunnableDefault queueRunnable = this.waitingRunnable.get(task);
                countDownLatch = queueRunnable.getCountDownLatch();
            }
            if(countDownLatch.getCount() > 0){
                countDownLatch.await();
            }
        }
    }

    @Override
    public void queueRunnableEnd(Task task) {
        synchronized (this.waitingTasks){
            this.doingTasks.remove(task);
            this.doingRunnable.remove(task);
        }
    }
}
