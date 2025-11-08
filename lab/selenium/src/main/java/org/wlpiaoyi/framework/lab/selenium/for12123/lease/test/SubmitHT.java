package org.wlpiaoyi.framework.lab.selenium.for12123.lease.test;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2025-11-07 16:11:46</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
@Data
@Builder
public class SubmitHT {
    /**
     * 车牌号
     */
    private String carNo;

    /**
     * 合同号
     */
    private String htNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号码
     */
    private String cardId;

    /**
     * 签合同时间
     */
    private LocalDateTime htSignTime;

    /**
     * 租借开始时间
     */
    private LocalDateTime leaseStartTime;

    /**
     * 租借结束时间
     */
    private LocalDateTime leaseEndTime;
}
