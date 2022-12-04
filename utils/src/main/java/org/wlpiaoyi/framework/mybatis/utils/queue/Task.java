package org.wlpiaoyi.framework.mybatis.utils.queue;

@FunctionalInterface
public interface Task extends Runnable {
    void run();
}
