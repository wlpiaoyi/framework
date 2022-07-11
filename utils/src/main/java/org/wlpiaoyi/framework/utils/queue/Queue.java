package org.wlpiaoyi.framework.utils.queue;

import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/11 15:45
 * @Version 1.0
 */
public interface Queue {


    /**
     * 未执行的任务
     * @return
     */
    List<Task> getWaitingTasks();

    /**
     * 正在执行的任务
     * @return
     */
    List<Task> getDoingTasks();

    /**
     * 是否正在执行任务队列
     * @return
     */
    boolean inQueue();

    /**
     * 执行任务
     * @return
     */
    Thread runTask(Task task) ;

    /**
     * 立即启动执行队列
     * 如果已启动则忽略
     */
    int start();

    /**
     * 删除任务
     * @param task
     */
    void remove(Task task);

    /**
     * 添加任务
     * @param task
     */
    void addTask(Task task);

    /**
     * 等待完成
     */
    void await();

}
