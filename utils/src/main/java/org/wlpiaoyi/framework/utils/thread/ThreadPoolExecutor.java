package org.wlpiaoyi.framework.utils.thread;

import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 自定义线程池执行器，支持任务管理、参数传递和任务生命周期回调
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/17 14:53</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
public class ThreadPoolExecutor {

    /** 默认核心线程数 */
    public static final int CORE_POOL_SIZE = 5;

    /** 默认最大线程数 */
    public static final int MAXIMUM_POOL_SIZE = 50;

    /** 默认线程空闲存活时间 */
    public static final long KEEP_ALIVE_TIME = 300;

    /** 默认工作队列容量 */
    public static final int WORK_QUEUE_COUNT = 50;

    /** 底层线程池执行器 */
    private final java.util.concurrent.ThreadPoolExecutor threadPool;

    /** 任务ID与Future的映射关系，用于任务管理 */
    private final Map<String, Future<?>> futureMap = new ConcurrentHashMap<>();

    /**
     * <p><b>{@code @description:}</b>
     * 构造线程池执行器
     * </p>
     *
     * <p><b>{@code @param}</b> <b>corePoolSize</b>
     * {@link int} 核心线程数
     * </p>
     *
     * <p><b>{@code @param}</b> <b>maximumPoolSize</b>
     * {@link int} 最大线程数
     * </p>
     *
     * <p><b>{@code @param}</b> <b>keepAliveTime</b>
     * {@link long} 线程空闲存活时间
     * </p>
     *
     * <p><b>{@code @param}</b> <b>unit</b>
     * {@link TimeUnit} 时间单位
     * </p>
     *
     * <p><b>{@code @param}</b> <b>workQueueCount</b>
     * {@link int} 工作队列容量
     * </p>
     *
     * <p><b>{@code @param}</b> <b>threadNamePrefix</b>
     * {@link String} 线程名前缀
     * </p>
     *
     * <p><b>{@code @param}</b> <b>handler</b>
     * {@link RejectedExecutionHandler} 拒绝策略处理器
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/6 12:01</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                       int workQueueCount, String threadNamePrefix, RejectedExecutionHandler handler) {
        if(corePoolSize < 1) corePoolSize = CORE_POOL_SIZE;
        if(maximumPoolSize < 1) maximumPoolSize = MAXIMUM_POOL_SIZE;
        if(keepAliveTime < 1) keepAliveTime = KEEP_ALIVE_TIME;
        if(workQueueCount < 1) workQueueCount = WORK_QUEUE_COUNT;
        ThreadFactory threadFactory = r -> new Thread(r, threadNamePrefix + "_" + r.hashCode());
        this.threadPool = new java.util.concurrent.ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepAliveTime, unit,
                new ArrayBlockingQueue<>(workQueueCount), threadFactory, handler);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 提交带任务参数的可管理任务（支持任务取消和状态跟踪）
     * </p>
     *
     * <p><b>{@code @param}</b> <b>taskParams</b>
     * {@link TaskParams} 任务参数，包含任务ID、延迟执行时间等
     * </p>
     *
     * <p><b>{@code @param}</b> <b>runnable</b>
     * {@link Runnable<P, R>} 可执行任务接口
     * </p>
     *
     * <p><b>{@code @param}</b> <b>param</b>
     * {@link P} 任务参数
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/6 12:02</p>
     * <p><b>{@code @return:}</b>{@link Future<R>} 返回Future对象，用于获取任务执行结果</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     *
     * <p><b>{@code @throws}</b> <b>RuntimeException</b> 当taskId为0时抛出异常</p>
     */
    public <P, R> Future<R> submit(TaskParams taskParams, Runnable<P, R> runnable, P param) {
        String taskId = taskParams.getTaskId();
        if(ValueUtils.isBlank(taskId)){
            throw new RuntimeException("taskId can not be empty");
        }
        // 如果存在相同taskId的未完成任务，先取消
        if(futureMap.containsKey(taskId)){
            if(!taskParams.isForcible()){
                throw new RuntimeException("taskId " + taskId + " is already exists");
            }
            Future<?> future = futureMap.get(taskId);
            if(future != null){
                if(!future.isDone()){
                    future.cancel(true);
                }
                futureMap.remove(taskId);
            }
        }
        final String[] atomicTaskId = new String[]{taskId};
        // 提交任务，并设置完成回调
        Future<R> future = this.pSubmit(runnable, param, taskParams, tId -> {
            synchronized (atomicTaskId){
                atomicTaskId[0] = null;
                futureMap.remove(tId);
            }
        });
        // 将任务添加到管理映射中
        synchronized (atomicTaskId){
            if(ValueUtils.isNotBlank(atomicTaskId[0])){
                futureMap.put(taskId, future);
            }
        }
        return future;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 提交简单任务（无需任务管理的快速提交）
     * </p>
     *
     * <p><b>{@code @param}</b> <b>runnable</b>
     * {@link Runnable<P, R>} 可执行任务接口
     * </p>
     *
     * <p><b>{@code @param}</b> <b>param</b>
     * {@link P} 任务参数
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/6 12:02</p>
     * <p><b>{@code @return:}</b>{@link Future<R>} 返回Future对象，用于获取任务执行结果</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public <P, R> Future<R> submit(Runnable<P, R> runnable, P param) {
        return this.pSubmit(runnable, param, null, null);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 内部任务提交方法
     * </p>
     *
     * <p><b>{@code @param}</b> <b>runnable</b>
     * {@link Runnable<P, R>} 可执行任务接口
     * </p>
     *
     * <p><b>{@code @param}</b> <b>param</b>
     * {@link P} 任务参数
     * </p>
     *
     * <p><b>{@code @param}</b> <b>taskParams</b>
     * {@link TaskParams} 任务参数（可为null）
     * </p>
     *
     * <p><b>{@code @param}</b> <b>runEnd</b>
     * {@link RunEnd} 任务结束回调（可为null）
     * </p>
     *
     * <p><b>{@code @return:}</b>{@link Future<R>} 返回Future对象</p>
     */
    private <P, R> Future<R> pSubmit(Runnable<P, R> runnable, P param, TaskParams taskParams, RunEnd runEnd) {
        return this.threadPool.submit(new ParamCallable<>(runnable, taskParams, param, runEnd));
    }

    /**
     * <p><b>{@code @description:}</b>
     * 关闭线程池，等待已提交的任务执行完成
     * </p>
     */
    public void shutdown() {
        this.threadPool.shutdown();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 立即关闭线程池，尝试中断所有正在执行的任务
     * </p>
     *
     * <p><b>{@code @return:}</b>{@link List<Runnable>} 返回未执行的任务列表</p>
     */
    public List<java.lang.Runnable> shutdownNow() {
        return this.threadPool.shutdownNow();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 等待线程池中的所有任务完成
     * </p>
     *
     * <p><b>{@code @param}</b> <b>timeout</b>
     * {@link long} 等待超时时间
     * </p>
     *
     * <p><b>{@code @param}</b> <b>unit</b>
     * {@link TimeUnit} 时间单位
     * </p>
     *
     * <p><b>{@code @return:}</b>{@link boolean} true-所有任务完成，false-等待超时</p>
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.threadPool.awaitTermination(timeout, unit);
    }



    /**
     * <p><b>{@code @description:}</b>
     * 判断线程池是否已关闭
     * </p>
     *
     * <p><b>{@code @return:}</b>{@link boolean} true-已关闭，false-未关闭</p>
     */
    public boolean isShutdown() {
        return this.threadPool.isShutdown();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 判断线程池中的所有任务是否已完成
     * </p>
     *
     * <p><b>{@code @return:}</b>{@link boolean} true-所有任务已完成，false-还有任务在执行</p>
     */
    public boolean isTerminated() {
        return this.threadPool.isTerminated();
    }

    /**
     * <p><b>{@code @description:}</b>
     * 根据任务ID取消指定任务
     * </p>
     *
     * <p><b>{@code @param}</b> <b>taskId</b>
     * {@link long} 任务ID
     * </p>
     *
     * <p><b>{@code @return:}</b>{@link boolean} true-取消成功，false-任务不存在或已完成</p>
     */
    public boolean cancelTask(String taskId) {
        Future<?> future = futureMap.get(taskId);
        if (future != null && !future.isDone()) {
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                futureMap.remove(taskId);
            }
            return cancelled;
        }
        return false;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取当前活跃的任务数量
     * </p>
     *
     * <p><b>{@code @return:}</b>{@link int} 活跃任务数量</p>
     */
    public int getActiveTaskCount() {
        return futureMap.size();
    }

    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * 线程任务结束回调接口
     * </p>
     * <p><b>{@code @date:}</b>           2025/11/6 11:25</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     */
    public interface RunEnd{

        /**
         * <p><b>{@code @description:}</b>
         * 任务执行结束时的回调方法
         * </p>
         *
         * <p><b>{@code @param}</b> <b>taskId</b>
         * {@link String} 任务ID
         * </p>
         */
        void run(String taskId);
    }

    /**
     * <p><b>{@code @author:}</b>         wlpiaoyi</p>
     * <p><b>{@code @description:}</b>
     * 带参数的可调用任务包装类，支持延迟执行和任务结束回调
     * </p>
     * <p><b>{@code @date:}</b>           2025/11/6 11:25</p>
     * <p><b>{@code @version:}</b>       1.0</p>
     */
    static class ParamCallable<P,R> implements Callable<R> {

        /** 实际执行的任务 */
        private final Runnable<P, R> runnable;

        /** 任务参数（包含任务ID、延迟时间等） */
        private final TaskParams taskParams;

        /** 业务参数 */
        private final P param;

        /** 任务结束回调 */
        private final RunEnd runEnd;

        /**
         * <p><b>{@code @description:}</b>
         * 构造参数化可调用任务
         * </p>
         *
         * <p><b>{@code @param}</b> <b>runnable</b>
         * {@link Runnable<P, R>} 实际执行的任务
         * </p>
         *
         * <p><b>{@code @param}</b> <b>taskParams</b>
         * {@link TaskParams} 任务参数
         * </p>
         *
         * <p><b>{@code @param}</b> <b>param</b>
         * {@link P} 业务参数
         * </p>
         *
         * <p><b>{@code @param}</b> <b>runEnd</b>
         * {@link RunEnd} 任务结束回调
         * </p>
         */
        ParamCallable(Runnable<P, R> runnable, TaskParams taskParams, P param, RunEnd runEnd) {
            this.runnable = runnable;
            this.taskParams = taskParams;
            this.param = param;
            this.runEnd = runEnd;
        }

        /**
         * <p><b>{@code @description:}</b>
         * 执行任务，支持延迟执行和异常处理
         * </p>
         *
         * <p><b>{@code @return:}</b>{@link R} 任务执行结果</p>
         *
         * <p><b>{@code @throws}</b> <b>Exception</b> 任务执行过程中可能抛出的异常</p>
         */
        @Override
        public R call() throws Exception {
            // 如果没有任务参数，直接执行任务
            if(this.taskParams == null){
                return this.runnable.run(null, this.param);
            }
            try {
                // 如果设置了延迟执行时间，先休眠
                if(this.taskParams.getDurationSecond() > 0) {
                    Thread.sleep((long) (this.taskParams.getDurationSecond() * 1000.d));
                }
                // 执行实际任务
                return this.runnable.run(this.taskParams.getTaskId(), this.param);
            } finally {
                // 任务执行完成后调用结束回调
                if(this.runEnd != null) {
                    this.runEnd.run(this.taskParams.getTaskId());
                }
            }
        }
    }

}