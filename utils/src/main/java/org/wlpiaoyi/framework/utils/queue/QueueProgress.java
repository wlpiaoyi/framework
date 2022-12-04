package org.wlpiaoyi.framework.utils.queue;

/**
 * @Author wlpiaoyi
 * @Date 2022/7/12 12:49
 * @Version 1.0
 */
public interface QueueProgress {

    /**
     * 队列开始
     * @param queue
     */
    void beginQueue(Queue queue);

    /**
     * 任务开始
     * @param task
     */
    void beginTask(Task task);

    /**
     * 任务结束
     * @param task
     */
    void endTask(Task task);

    /**
     * 队列结束
     * @param queue
     */
    void endQueue(Queue queue);

}
