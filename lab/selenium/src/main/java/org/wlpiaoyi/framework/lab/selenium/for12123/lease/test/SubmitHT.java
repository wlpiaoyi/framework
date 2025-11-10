package org.wlpiaoyi.framework.lab.selenium.for12123.lease.test;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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


    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; border: 1px solid #e74c3c; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 重写toString方法，格式化输出所有字段
     * </div>
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/9 21:01</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("SubmitHT{");
        sb.append("carNo='").append(carNo != null ? carNo : "").append('\'');
        sb.append(", htNo='").append(htNo != null ? htNo : "").append('\'');
        sb.append(", cardId='").append(cardId != null ? cardId : "").append('\'');
        sb.append(", htSignTime=");

        if (htSignTime != null) {
            sb.append(htSignTime.format(formatter));
        } else {
            sb.append("null");
        }

        sb.append(", leaseStartTime=");
        if (leaseStartTime != null) {
            sb.append(leaseStartTime.format(formatter));
        } else {
            sb.append("null");
        }

        sb.append(", leaseEndTime=");
        if (leaseEndTime != null) {
            sb.append(leaseEndTime.format(formatter));
        } else {
            sb.append("null");
        }

        sb.append('}');
        return sb.toString();
    }
}
