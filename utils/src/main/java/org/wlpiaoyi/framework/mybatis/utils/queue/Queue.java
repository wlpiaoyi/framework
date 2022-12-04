package org.wlpiaoyi.framework.mybatis.utils.queue;

import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/11 15:45
 * @Version 1.0
 */
public interface Queue {

    /**
     * 任务回调
     * @param queueProgress
     */
    void setQueueProgress(QueueProgress queueProgress);

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
     * 未执行的任务数量
     * @return
     */
    int getUndoTaskCount();

    /**
     * 执行中的任务数量
     * @return
     */
    int getTaskingCount();

    /**
     * 执行任务
     * @return
     */
    Thread runTask(Task task);

    /**
     * 在安全状态下进行操作
     * @param task
     */
    void safeOption(Task task);

    /**
     * 立即启动执行队列
     * 如果已启动则忽略
     */
    Thread start();

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
