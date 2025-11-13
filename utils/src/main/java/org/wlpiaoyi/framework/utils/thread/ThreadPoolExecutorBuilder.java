package org.wlpiaoyi.framework.utils.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * ThreadPoolExecutor 构建器
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/6 11:25</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 */
public class ThreadPoolExecutorBuilder {

    // 线程池参数
    private int corePoolSize = ThreadPoolExecutor.CORE_POOL_SIZE;
    // 最大线程数
    private int maximumPoolSize = ThreadPoolExecutor.MAXIMUM_POOL_SIZE;
    // 线程空闲存活时间
    private long keepAliveTime = ThreadPoolExecutor.KEEP_ALIVE_TIME;
    // 时间单位
    private TimeUnit unit = TimeUnit.SECONDS;
    // 工作队列容量
    private int workQueueCount = ThreadPoolExecutor.WORK_QUEUE_COUNT;
    // 线程名前缀
    private String threadNamePrefix = "custom-thread-";
    // 拒绝策略
    private RejectedExecutionHandler handler = new java.util.concurrent.ThreadPoolExecutor.AbortPolicy();

    /**
     * <p><b>{@code @description:}</b>
     * 设置核心线程数
     * </p>
     */
    public ThreadPoolExecutorBuilder corePoolSize(int corePoolSize) {
        if (corePoolSize > 0) {
            this.corePoolSize = corePoolSize;
        }
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置最大线程数
     * </p>
     */
    public ThreadPoolExecutorBuilder maximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize > 0) {
            this.maximumPoolSize = maximumPoolSize;
        }
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置线程空闲存活时间
     * </p>
     */
    public ThreadPoolExecutorBuilder keepAliveTime(long keepAliveTime, TimeUnit unit) {
        if (keepAliveTime > 0 && unit != null) {
            this.keepAliveTime = keepAliveTime;
            this.unit = unit;
        }
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置工作队列容量
     * </p>
     */
    public ThreadPoolExecutorBuilder workQueueCount(int workQueueCount) {
        if (workQueueCount > 0) {
            this.workQueueCount = workQueueCount;
        }
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置线程名前缀
     * </p>
     */
    public ThreadPoolExecutorBuilder threadNamePrefix(String threadNamePrefix) {
        if (threadNamePrefix != null && !threadNamePrefix.trim().isEmpty()) {
            this.threadNamePrefix = threadNamePrefix;
        }
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置拒绝策略
     * </p>
     */
    public ThreadPoolExecutorBuilder handler(RejectedExecutionHandler handler) {
        if (handler != null) {
            this.handler = handler;
        }
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 使用无界队列（LinkedBlockingQueue）
     * </p>
     */
    public ThreadPoolExecutorBuilder unboundedQueue() {
        this.workQueueCount = Integer.MAX_VALUE;
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 使用同步移交队列（SynchronousQueue）
     * </p>
     */
    public ThreadPoolExecutorBuilder synchronousQueue() {
        this.workQueueCount = 0; // 特殊标记，表示使用 SynchronousQueue
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置拒绝策略为 CallerRunsPolicy
     * </p>
     */
    public ThreadPoolExecutorBuilder callerRunsPolicy() {
        this.handler = new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy();
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置拒绝策略为 DiscardPolicy
     * </p>
     */
    public ThreadPoolExecutorBuilder discardPolicy() {
        this.handler = new java.util.concurrent.ThreadPoolExecutor.DiscardPolicy();
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 设置拒绝策略为 DiscardOldestPolicy
     * </p>
     */
    public ThreadPoolExecutorBuilder discardOldestPolicy() {
        this.handler = new java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy();
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 构建 ThreadPoolExecutor 实例
     * </p>
     */
    public ThreadPoolExecutor build() {
        // 参数验证
        if (corePoolSize < 1) {
            throw new IllegalArgumentException("corePoolSize must be greater than 0");
        }
        if (maximumPoolSize < corePoolSize) {
            throw new IllegalArgumentException("maximumPoolSize must be greater than or equal to corePoolSize");
        }
        if (keepAliveTime < 1) {
            throw new IllegalArgumentException("keepAliveTime must be greater than 0");
        }

        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueueCount,
                threadNamePrefix,
                handler
        );
    }

    /**
     * <p><b>{@code @description:}</b>
     * 创建构建器实例
     * </p>
     */
    public static ThreadPoolExecutorBuilder newBuilder() {
        return new ThreadPoolExecutorBuilder();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 创建默认配置的线程池
     * </p>
     */
    public static ThreadPoolExecutor defaultPool() {
        return newBuilder().build();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 创建IO密集型任务线程池
     * </p>
     */
    public static ThreadPoolExecutor ioIntensivePool() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        return newBuilder()
                .corePoolSize(corePoolSize)
                .maximumPoolSize(corePoolSize * 4)
                .keepAliveTime(60, TimeUnit.SECONDS)
                .workQueueCount(1000)
                .threadNamePrefix("io-pool")
                .callerRunsPolicy()
                .build();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 创建CPU密集型任务线程池
     * </p>
     */
    public static ThreadPoolExecutor cpuIntensivePool() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        return newBuilder()
                .corePoolSize(corePoolSize)
                .maximumPoolSize(corePoolSize)
                .keepAliveTime(0, TimeUnit.SECONDS) // 核心线程永不超时
                .workQueueCount(100)
                .threadNamePrefix("cpu-pool")
                .callerRunsPolicy()
                .build();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 创建定时任务线程池
     * </p>
     */
    public static ThreadPoolExecutor scheduledPool() {
        return newBuilder()
                .corePoolSize(1)
                .maximumPoolSize(5)
                .keepAliveTime(300, TimeUnit.SECONDS)
                .workQueueCount(50)
                .threadNamePrefix("scheduled-pool")
                .discardPolicy() // 定时任务丢弃新任务
                .build();
    }
}