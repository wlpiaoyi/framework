package org.wlpiaoyi.framework.utils.thread;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * 支持参数和返回值的可运行任务接口
 * 函数式接口，用于定义可在线程池中执行的带参数任务
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/6 11:30</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 */
@FunctionalInterface
public interface Runnable<P,R> {

    /**
     * <p><b>{@code @description:}</b>
     * 执行任务的核心方法，支持任务ID和自定义参数，并返回执行结果
     * 此方法将在线程池的工作线程中异步执行
     * </p>
     *
     * <p><b>{@code @param}</b> <b>taskId</b>
     * {@link Long} 任务唯一标识符
     * <ul>
     *   <li>当使用{@link TaskParams}提交任务时，为实际的任务ID</li>
     *   <li>当使用简单提交方式时，为0L</li>
     *   <li>可用于任务跟踪、日志记录和状态管理</li>
     * </ul>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>param</b>
     * {@link P} 任务执行所需的业务参数
     * <ul>
     *   <li>支持任意类型的参数传递</li>
     *   <li>可为基本类型、对象、集合等</li>
     *   <li>在任务执行过程中使用</li>
     * </ul>
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/6 11:44</p>
     * <p><b>{@code @return:}</b>{@link R} 任务执行结果，支持任意类型返回值</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     *
     * <p><b>{@code @throws}</b> <b>Exception</b> 任务执行过程中可能抛出的异常</p>
     *
     * <p><b>{@code @see}</b> ThreadPoolExecutor#submit(TaskParams, Runnable, Object)</p>
     * <p><b>{@code @see}</b> ThreadPoolExecutor#submit(Runnable, Object)</p>
     *
     * <p><b>{@code @example:}</b>
     * <pre>{@code
     * // 示例1：字符串处理任务
     * Runnable<String, Integer> stringTask = (taskId, text) -> {
     *     System.out.println("任务[" + taskId + "]处理文本: " + text);
     *     return text.length();
     * };
     *
     * // 示例2：对象处理任务
     * Runnable<User, Boolean> userTask = (taskId, user) -> {
     *     System.out.println("任务[" + taskId + "]处理用户: " + user.getName());
     *     return userService.process(user);
     * };
     * }</pre>
     * </p>
     */
    R run(Long taskId, P param) throws Exception;
}