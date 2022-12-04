package org.wlpiaoyi.framework.mybatis.utils.queue;

/**
 * 顺序队列
 * 按照任务加入顺序依次执行任务,任务之间必须执行完成才能进行下一个
 * @Author wlpiaoyi
 * @Date 2022/7/11 20:29
 * @Version 1.0
 */
public class QueueSequence extends QueueDefault{

    @Override
    public boolean isSyncForRun(){
        return true;
    }
}
