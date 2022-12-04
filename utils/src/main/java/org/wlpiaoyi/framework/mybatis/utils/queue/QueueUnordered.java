package org.wlpiaoyi.framework.mybatis.utils.queue;

/**
 * 无序队列
 * 同时执行所有任务
 * @Author wlpiaoyi
 * @Date 2022/7/11 20:31
 * @Version 1.0
 */
public class QueueUnordered extends QueueDefault{

    public boolean isSyncForRun(){
        return false;
    }
}

