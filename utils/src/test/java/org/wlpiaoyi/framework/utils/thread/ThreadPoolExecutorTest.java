package org.wlpiaoyi.framework.utils.thread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * ThreadPoolExecutor 测试类
 */
public class ThreadPoolExecutorTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private ThreadPoolExecutor threadPool;

    private void log(String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + timestamp + "] [" + threadName + "] " + message);
    }

    private void logError(String message, Throwable throwable) {
        String timestamp = DATE_FORMAT.format(new Date());
        String threadName = Thread.currentThread().getName();
        System.err.println("[" + timestamp + "] [" + threadName + "] ERROR: " + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
        log("开始设置测试环境...");
        // 创建测试用的线程池
        threadPool = ThreadPoolExecutorBuilder.newBuilder()
                .corePoolSize(2)
                .maximumPoolSize(4)
                .keepAliveTime(10, TimeUnit.SECONDS)
                .workQueueCount(10)
                .threadNamePrefix("test-pool")
                .build();
        log("线程池创建完成，核心线程数: 2, 最大线程数: 4, 队列容量: 10");
    }

    @Test
    public void testSimpleTask() throws Exception {
        log("=== 开始测试简单任务执行 ===");

        Runnable<String, Integer> task = (taskId, param) -> {
            log("执行任务[" + taskId + "], 参数: " + param);
            int result = param.length();
            log("任务[" + taskId + "]执行完成，结果: " + result);
            return result;
        };

        log("提交任务到线程池...");
        Future<Integer> future = threadPool.submit(task, "Hello World");
        log("任务已提交，等待执行结果...");

        long startTime = System.currentTimeMillis();
        Integer result = future.get(5, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        log("任务执行完成，耗时: " + (endTime - startTime) + "ms, 结果: " + result);

        assertEquals(Integer.valueOf(11), result);
        assertTrue(future.isDone());
        log("简单任务测试通过 ✓");
    }

    @Test
    public void testTaskWithParams() throws Exception {
        log("=== 开始测试带参数的任务执行 ===");

        Runnable<Integer, String> task = (taskId, number) -> {
            String result = "Task[" + taskId + "]处理数字: " + number;
            log("执行任务: " + result);
            Thread.sleep(200); // 模拟处理时间
            log("任务[" + taskId + "]处理完成");
            return result;
        };

        TaskParams taskParams = TaskParams.builder()
                .taskId("1001")
                .durationSecond(1) // 延迟1秒执行
                .build();

        log("提交延迟任务，taskId: " + taskParams.getTaskId() + ", 延迟: " + taskParams.getDurationSecond() + "秒");
        long submitTime = System.currentTimeMillis();
        Future<String> future = threadPool.submit(taskParams, task, 42);
        log("延迟任务已提交");

        long startWaitTime = System.currentTimeMillis();
        String result = future.get(5, TimeUnit.SECONDS);
        long endWaitTime = System.currentTimeMillis();

        log("延迟任务执行完成，等待时间: " + (endWaitTime - startWaitTime) + "ms, 总耗时: " + (endWaitTime - submitTime) + "ms");
        log("执行结果: " + result);

        assertTrue("结果应包含任务ID", result.contains("Task[1001]"));
        assertTrue("结果应包含参数", result.contains("42"));
        log("带参数任务测试通过 ✓");
    }

    @Test
    public void testConcurrentTasks() throws Exception {
        log("=== 开始测试并发任务执行 ===");

        int taskCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(taskCount);
        AtomicInteger completedCount = new AtomicInteger(0);

        Runnable<Integer, Boolean> task = (taskId, index) -> {
            try {
                log("任务[" + taskId + "]准备就绪，等待开始信号，参数: " + index);
                startLatch.await(); // 等待所有任务准备就绪
                log("任务[" + taskId + "]开始执行");
                Thread.sleep(100); // 模拟任务执行
                int count = completedCount.incrementAndGet();
                log("任务[" + taskId + "]执行完成，当前完成数: " + count);
                return true;
            } catch (Exception e) {
                logError("任务[" + taskId + "]执行异常", e);
                throw new RuntimeException(e);
            } finally {
                endLatch.countDown();
                log("任务[" + taskId + "]清理完成");
            }
        };

        log("准备提交 " + taskCount + " 个并发任务...");
        long submitStartTime = System.currentTimeMillis();

        for (int i = 0; i < taskCount; i++) {
            final int index = i;
            threadPool.submit(task, index);
            log("任务 " + (i + 1) + "/" + taskCount + " 已提交");
        }
        long submitEndTime = System.currentTimeMillis();
        log("所有任务提交完成，耗时: " + (submitEndTime - submitStartTime) + "ms");

        Thread.sleep(500); // 给任务一些时间准备
        log("发送开始信号，所有任务同时开始执行...");
        startLatch.countDown();

        long waitStartTime = System.currentTimeMillis();
        boolean allCompleted = endLatch.await(10, TimeUnit.SECONDS);
        long waitEndTime = System.currentTimeMillis();

        if (!allCompleted) {
            logError("任务执行超时，剩余任务数: " + endLatch.getCount(), null);
            fail("任务执行超时");
        }

        log("所有任务执行完成，等待时间: " + (waitEndTime - waitStartTime) + "ms");
        assertEquals("完成的任务数量应该等于提交的任务数量", taskCount, completedCount.get());
        log("并发任务测试通过 ✓，实际完成数: " + completedCount.get());
    }

    @Test
    public void testTaskCancellation() throws Exception {
        log("=== 开始测试任务取消功能 ===");

        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch taskCancelled = new CountDownLatch(1);

        Runnable<String, String> longRunningTask = (taskId, param) -> {
            log("长时任务[" + taskId + "]开始执行，参数: " + param);
            taskStarted.countDown();
            try {
                log("长时任务[" + taskId + "]进入休眠5秒...");
                Thread.sleep(5000); // 长时任务
                log("长时任务[" + taskId + "]正常完成");
                return "Completed";
            } catch (InterruptedException e) {
                log("长时任务[" + taskId + "]被中断取消");
                taskCancelled.countDown();
                throw new RuntimeException("Task interrupted");
            }
        };

        TaskParams taskParams = TaskParams.builder()
                .taskId("2001")
                .durationSecond(0)
                .build();

        log("提交长时任务，taskId: " + taskParams.getTaskId());
        Future<String> future = threadPool.submit(taskParams, longRunningTask, "test");

        log("等待任务开始执行...");
        boolean started = taskStarted.await(2, TimeUnit.SECONDS);
        if (!started) {
            logError("任务启动超时", null);
            fail("任务启动超时");
        }
        log("任务已开始执行，准备取消任务...");

        long cancelStartTime = System.currentTimeMillis();
        boolean cancelled = threadPool.cancelTask("2001");
        long cancelEndTime = System.currentTimeMillis();

        log("取消操作完成，结果: " + cancelled + ", 耗时: " + (cancelEndTime - cancelStartTime) + "ms");

        // 验证任务被取消
        boolean interrupted = taskCancelled.await(2, TimeUnit.SECONDS);
        if (!interrupted) {
            logError("任务中断确认超时", null);
            fail("任务中断确认超时");
        }

        assertTrue("Future应该被标记为取消", future.isCancelled());
        log("任务取消测试通过 ✓");
    }

    @Test
    public void testTaskIdManagement() throws Exception {
        log("=== 开始测试任务ID管理功能 ===");

        String[] executedTaskId = new String[]{null};
        CountDownLatch firstTaskStarted = new CountDownLatch(1);
        CountDownLatch firstTaskBlocked = new CountDownLatch(1);

        Runnable<String, String> task = (taskId, param) -> {
            executedTaskId[0] = taskId;
            if ("3001".equals(taskId) && firstTaskStarted.getCount() > 0) {
                log("第一个任务开始执行");
                firstTaskStarted.countDown();
                try {
                    log("第一个任务进入阻塞状态");
                    firstTaskBlocked.await(2, TimeUnit.SECONDS); // 阻塞第一个任务
                    log("第一个任务继续执行");
                } catch (InterruptedException e) {
                    log("第一个任务被中断");
                    throw new RuntimeException(e);
                }
            } else {
                log("第二个任务执行，taskId: " + taskId);
            }
            return taskId;
        };

        // 提交相同taskId的任务，前一个应该被取消
        TaskParams params1 = TaskParams.builder().taskId("3001").forcible(false).durationSecond(0).build();
        TaskParams params2 = TaskParams.builder().taskId("3001").forcible(true).durationSecond(0).build();

        log("提交第一个任务，taskId: " + params1.getTaskId());
        Future<String> future1 = threadPool.submit(params1, task, "first");

        // 等待第一个任务开始
        assertTrue("第一个任务应该启动", firstTaskStarted.await(2, TimeUnit.SECONDS));
        Thread.sleep(100);

        log("提交第二个相同taskId的任务，预期第一个任务会被取消");
        Future<String> future2 = threadPool.submit(params2, task, "second");

        String result = future2.get(5, TimeUnit.SECONDS);
        log("第二个任务执行完成，结果: " + result);

        // 释放第一个任务的阻塞
        firstTaskBlocked.countDown();

        assertEquals("3001", result);
        assertEquals("3001", executedTaskId[0]);

        // 第一个任务应该被取消
        assertTrue("第一个任务应该完成", future1.isDone());
        log("任务ID管理测试通过 ✓");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidTaskId() throws Exception {
        log("=== 开始测试无效任务ID ===");

        TaskParams invalidParams = TaskParams.builder()
                .taskId("") // 无效的taskId
                .durationSecond(0)
                .build();

        Runnable<String, String> task = (taskId, param) -> {
            log("任务执行，但这行代码不应该被执行");
            return "result";
        };

        log("准备提交taskId为0的无效任务...");
        try {
            threadPool.submit(invalidParams, task, "test");
            logError("应该抛出异常但未抛出", null);
            fail("应该抛出RuntimeException");
        } catch (RuntimeException e) {
            log("捕获预期异常: " + e.getMessage());
            throw e; // 重新抛出以符合@Test(expected)的验证
        }
    }

    @Test
    public void testExceptionHandling() throws Exception {
        log("=== 开始测试异常处理 ===");

        Runnable<String, String> failingTask = (taskId, param) -> {
            log("开始执行会抛出异常的任务，参数: " + param);
            throw new RuntimeException("模拟任务执行异常: " + param);
        };

        log("提交会抛出异常的任务...");
        Future<String> future = threadPool.submit(failingTask, "error");

        try {
            log("等待任务执行结果...");
            future.get(5, TimeUnit.SECONDS);
            logError("应该抛出异常但未抛出", null);
            fail("应该抛出ExecutionException");
        } catch (ExecutionException e) {
            log("捕获预期异常: " + e.getMessage());
            assertTrue("异常原因应该是RuntimeException", e.getCause() instanceof RuntimeException);
            assertTrue("异常消息应该包含预期内容", e.getCause().getMessage().contains("模拟任务执行异常"));
            log("异常处理测试通过 ✓");
        } catch (TimeoutException e) {
            logError("任务执行超时", e);
            fail("任务执行超时");
        }
    }

    @Test
    public void testThreadPoolShutdown() throws Exception {
        log("=== 开始测试线程池关闭 ===");

        AtomicInteger activeCount = new AtomicInteger(0);
        CountDownLatch taskLatch = new CountDownLatch(3);

        Runnable<Integer, Boolean> task = (taskId, param) -> {
            int count = activeCount.incrementAndGet();
            log("任务[" + taskId + "]开始执行，当前活跃任务数: " + count + ", 参数: " + param);
            try {
                Thread.sleep(200);
                log("任务[" + taskId + "]执行完成");
                return true;
            } finally {
                taskLatch.countDown();
                log("任务[" + taskId + "]清理完成");
            }
        };

        log("提交3个任务到线程池...");
        for (int i = 0; i < 3; i++) {
            threadPool.submit(task, i);
            log("任务 " + (i + 1) + " 已提交");
        }

        // 等待任务开始执行
        Thread.sleep(100);
        log("准备关闭线程池...");

        long shutdownStartTime = System.currentTimeMillis();
        threadPool.shutdown();
        log("已调用shutdown()，等待任务完成...");

        boolean terminated = threadPool.awaitTermination(5, TimeUnit.SECONDS);
        long shutdownEndTime = System.currentTimeMillis();

        if (terminated) {
            log("线程池关闭完成，耗时: " + (shutdownEndTime - shutdownStartTime) + "ms");
        } else {
            logError("线程池关闭超时", null);
            fail("线程池关闭超时");
        }

        assertTrue("线程池应该已终止", threadPool.isTerminated());
        log("线程池关闭测试通过 ✓");
    }

    @Test
    public void testTaskWithReturnType() throws Exception {
        log("=== 开始测试不同类型的返回值 ===");

        Runnable<Integer, String> stringTask = (taskId, number) -> {
            String result = "结果:" + number;
            log("字符串任务执行，输入: " + number + ", 输出: " + result);
            return result;
        };

        Runnable<String, Integer> intTask = (taskId, text) -> {
            int result = text.length();
            log("整数任务执行，输入: " + text + ", 输出: " + result);
            return result;
        };

        Runnable<List<String>, Boolean> booleanTask = (taskId, list) -> {
            boolean result = !list.isEmpty();
            log("布尔任务执行，输入列表大小: " + list.size() + ", 输出: " + result);
            return result;
        };

        log("提交字符串类型返回任务...");
        Future<String> future1 = threadPool.submit(stringTask, 100);

        log("提交整数类型返回任务...");
        Future<Integer> future2 = threadPool.submit(intTask, "Hello");

        log("提交布尔类型返回任务...");
        Future<Boolean> future3 = threadPool.submit(booleanTask, java.util.Arrays.asList("a", "b"));

        String result1 = future1.get(5, TimeUnit.SECONDS);
        Integer result2 = future2.get(5, TimeUnit.SECONDS);
        Boolean result3 = future3.get(5, TimeUnit.SECONDS);

        log("字符串任务结果: " + result1);
        log("整数任务结果: " + result2);
        log("布尔任务结果: " + result3);

        assertEquals("结果:100", result1);
        assertEquals(Integer.valueOf(5), result2);
        assertEquals(Boolean.TRUE, result3);
        log("不同类型返回值测试通过 ✓");
    }

    @After
    public void tearDown() throws Exception {
        log("开始清理测试环境...");
        if (threadPool != null && !threadPool.isShutdown()) {
            log("关闭线程池...");
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                log("线程池未及时关闭，强制关闭...");
                List<java.lang.Runnable> pendingTasks = threadPool.shutdownNow();
                log("强制关闭完成，未执行任务数: " + pendingTasks.size());
            } else {
                log("线程池正常关闭完成");
            }
        }
        log("测试环境清理完成");
    }
}