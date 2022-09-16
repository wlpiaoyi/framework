package org.wlpiaoyi.framework.utils.queue;

@FunctionalInterface
public interface Task extends Runnable {
    void run();
}
