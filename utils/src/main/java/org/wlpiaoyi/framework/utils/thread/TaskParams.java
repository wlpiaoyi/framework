package org.wlpiaoyi.framework.utils.thread;

import lombok.Builder;
import lombok.Getter;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * 任务参数配置类，用于定义线程池任务的执行参数
 * 支持任务ID管理和延迟执行功能
 * </p>
 * <p><b>{@code @date:}</b>           2025/11/6 13:00</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 */
@Builder
@Getter
public class TaskParams {

    /**
     * <p><b>{@code @description:}</b>
     * 任务唯一标识符，用于任务管理和跟踪
     * 不能为0，确保任务的可识别性
     * </p>
     */
    private String taskId;

    /**
     * <p><b>{@code @description:}</b>
     * 任务延迟执行时间（单位：秒）
     * 大于0时表示需要延迟执行，任务提交后会先休眠指定时间再执行
     * 等于0时表示立即执行
     * </p>
     */
    private double durationSecond;
}