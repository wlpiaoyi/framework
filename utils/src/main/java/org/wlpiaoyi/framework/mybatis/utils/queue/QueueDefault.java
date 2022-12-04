package org.wlpiaoyi.framework.mybatis.utils.queue;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/11 16:09
 * @Version 1.0
 */
@Slf4j
class QueueDefault implements Queue, RunnableDefault.Progress, Runnable {

    //任务数组
    private final List<Task> waitingTasks = new ArrayList<>();
    private final Map<Task, RunnableDefault> waitingRunnable = new HashMap<>();
    private final List<Task> doingTasks = new ArrayList<>();
    private final Map<Task, RunnableDefault> doingRunnable = new HashMap<>();

    private final Object taskSynTag = new Object();

    private QueueProgress queueProgress;
    @Override
    public void setQueueProgress(QueueProgress queueProgress) {
        this.queueProgress = queueProgress;
    }

    @Override
    public List<Task> getWaitingTasks() {
        synchronized (this.taskSynTag){
            List<Task> list = this.waitingTasks;
            return new ArrayList(){{
                addAll(list);
            }};
        }
    }

    @Override
    public List<Task> getDoingTasks() {
        synchronized (this.taskSynTag){
            List<Task> list = this.doingTasks;
            return new ArrayList(){{
                addAll(list);
            }};
        }
    }

    @Getter
    private volatile int taskingCount = 0;

    public int getUndoTaskCount(){
        int count;
        synchronized (this.taskSynTag){
            count = this.waitingTasks.size();
        }
        return count;
    }

    public Thread runTask(Task task) {
        RunnableDefault run = this.nextQueueRunnable(task);
        if(run == null) throw new RuntimeException("Runnable Object is null");
        if(this.waitingTasks.contains(task) || !this.doingTasks.contains(task)){
            synchronized (this.taskSynTag){
                this.remove(task);
                if(!this.doingTasks.contains(task)){
                    this.doingTasks.add(task);
                }
            }
        }
        return this.execRun(run);
    }

    @Override
    public void safeOption(Task task) {
        synchronized (this.taskSynTag){
            task.run();
        }
    }

    private Thread execRun(RunnableDefault run) {
        Thread thread = new Thread(run);
        thread.start();
        return thread;
    }

    private Thread threadStart = null;
    private volatile boolean isLoadQueue = false;
    public Thread start() {
        synchronized (this.taskSynTag){
            if(this.isNullTask()){
                log.info("The task is null, not need to start");
                return null;
            }
            if(this.isLoadQueue){
                log.info("The task is loading, not need to start");
                return threadStart;
            }
            this.beginQueue();
            Thread thread = new Thread(this);
            thread.start();
            threadStart = thread;
            return threadStart;
        }
    }

    protected boolean isSyncForRun(){
        throw new RuntimeException("error");
    }
    public void run() {

        try{
            Task task;
            RunnableDefault run;
            synchronized (this.taskSynTag){
                task = this.nextTask();
                if(task == null){
                    log.info("No task for run");
                    return;
                }
                run = this.nextQueueRunnable(task);
                if(run == null){
                    new RuntimeException("task(" + task + ") not fund QueueRunnable").printStackTrace();
                }
            }
            do{
                Thread thread = this.execRun(run);
                if(this.isSyncForRun())
                    try {thread.join();} catch (InterruptedException e) {e.printStackTrace();}

                synchronized (this.taskSynTag){
                    task = this.nextTask();
                    if(task == null) return;
                    run = this.nextQueueRunnable(task);
                    if(run == null){
                        new RuntimeException("task(" + task + ") not fund QueueRunnable").printStackTrace();
                    }
                }
            }while (task != null);
        }finally {
            synchronized (this.taskSynTag){
                this.isLoadQueue = false;
            }
        }
    }



    private Task nextTask(){
        synchronized (this.taskSynTag){
            if(this.waitingTasks.isEmpty()) return null;
            Task task = this.waitingTasks.get(0);
            this.waitingTasks.remove(task);
            this.doingTasks.add(task);
            return task;
        }
    }

    private RunnableDefault nextQueueRunnable(Task task){
        synchronized (this.taskSynTag){
            if(this.waitingRunnable.isEmpty()) return null;
            RunnableDefault run = this.waitingRunnable.get(task);
            this.waitingRunnable.remove(task);
            this.doingRunnable.put(task, run);
            return run;
        }
    }

    /**
     * 删除任务
     * @param task
     */
    public void remove(Task task){
        synchronized (this.taskSynTag){
            this.waitingTasks.remove(task);
            this.waitingRunnable.remove(task);
        }
    }

    /**
     * 添加任务
     * @param task
     */
    public void addTask(Task task){
        synchronized (this.taskSynTag){
            this.waitingTasks.add(task);
            RunnableDefault queueRunnable = new RunnableDefault(task, this);
            this.waitingRunnable.put(task, queueRunnable);
        }
    }


    private RunnableDefault getRunObj(){

        synchronized (this.taskSynTag){
            boolean isEmpty = this.doingTasks.isEmpty() && this.waitingTasks.isEmpty();
            if(isEmpty) return null;

            RunnableDefault runObj  = null;
            if(!this.doingTasks.isEmpty()){
                Task task = this.doingTasks.get(0);
                RunnableDefault queueRunnable = this.doingRunnable.get(task);
                if(queueRunnable == null){
                    log.warn("There has task object in doing task list but the runnable object is null");
                    return null;
                }
                runObj = queueRunnable;
            }
            if(runObj != null) return runObj;


            if(!this.waitingTasks.isEmpty()){
                Task task = this.waitingTasks.get(0);
                RunnableDefault queueRunnable = this.waitingRunnable.get(task);
                if(queueRunnable == null){
                    log.warn("There has task object in waiting task list but the runnable object is null");
                    return null;
                }
                runObj = queueRunnable;
            }

            if(runObj != null) return runObj;
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void await() {
        if(this.isNullTask()){
            log.info("Has no await, because it's not in queue");
            return;
        }
        RunnableDefault runObj = this.getRunObj();
        if(runObj == null){
            log.info("Has no await, because it is null for CountDownLatch");
            return;
        }
        do{
            if(runObj.getRunStatus() >= 0){
                runObj.await();
            }
            runObj = this.getRunObj();
        }while (runObj != null);
    }

    @Override
    public void beginQueueRunnable(Task task) {
        synchronized (this.taskSynTag){
            this.taskingCount ++;
            if(this.queueProgress != null)
                this.queueProgress.beginTask(task);
        }
    }

    @Override
    public void endQueueRunnable(Task task) {
        synchronized (this.taskSynTag){
            this.doingTasks.remove(task);
            this.doingRunnable.remove(task);
            this.taskingCount --;
            if(this.queueProgress != null)
                this.queueProgress.endTask(task);
            if(this.isNullTask()){
                this.endQueue();
            }
        }

    }

    private boolean isNullTask(){
        synchronized (this.taskSynTag){
            return this.waitingTasks.isEmpty() && this.doingTasks.isEmpty();
        }
    }

    public void beginQueue(){
        synchronized (this.taskSynTag){
            this.taskingCount = 0;
            if(this.queueProgress != null)
                this.queueProgress.beginQueue(this);
        }
    }
    private void endQueue(){
        synchronized (this.taskSynTag){
            if(this.queueProgress != null)
                this.queueProgress.endQueue(this);
        }
    }
}
