package org.wlpiaoyi.framework.lab.selenium.for12123.zllist;

import lombok.Builder;
import lombok.Data;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class RentalContract {

    /** 合同编号 */
    private String contractNo;

    /** 号牌号码 */
    private String carNo;

    /** 号牌种类 */
    private String plateType;

    /** 合同有效期始 */
    private String validityStart;

    /** 合同有效期止 */
    private String validityEnd;

    /** 未处理违法数 */
    private String violationCount;

    /** 承租人 */
    private String lessee;

    /** 合同签订时间 */
    private String signTime;

    /** 状态 */
    private String status;

    /** 详情页数据 */
    private RentalContractDetail detail;

    public void applyDetail(RentalContractDetail detail) {
        this.detail = detail;
        if (detail == null) {
            return;
        }
        if (ValueUtils.isNotBlank(detail.getContractNo())) {
            this.contractNo = detail.getContractNo();
        }
        if (ValueUtils.isNotBlank(detail.getCarNo())) {
            this.carNo = detail.getCarNo();
        }
        if (ValueUtils.isNotBlank(detail.getPlateType())) {
            this.plateType = detail.getPlateType();
        }
        if (ValueUtils.isNotBlank(detail.getValidityStart())) {
            this.validityStart = detail.getValidityStart();
        }
        if (ValueUtils.isNotBlank(detail.getValidityEnd())) {
            this.validityEnd = detail.getValidityEnd();
        }
        if (ValueUtils.isNotBlank(detail.getSignDate())) {
            this.signTime = detail.getSignDate();
        }
        if (ValueUtils.isBlank(this.lessee) && ValueUtils.isNotBlank(detail.getIdCardNo())) {
            this.lessee = detail.getIdCardNo();
        }
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("合同编号", ValueUtils.isBlank(contractNo) ? "" : contractNo);
        map.put("号牌号码", ValueUtils.isBlank(carNo) ? "" : carNo);
        map.put("号牌种类", ValueUtils.isBlank(plateType) ? "" : plateType);
        map.put("合同有效期始", ValueUtils.isBlank(validityStart) ? "" : validityStart);
        map.put("合同有效期止", ValueUtils.isBlank(validityEnd) ? "" : validityEnd);
        map.put("未处理违法数", ValueUtils.isBlank(violationCount) ? "" : violationCount);
        map.put("承租人", ValueUtils.isBlank(lessee) ? "" : lessee);
        map.put("合同签订时间", ValueUtils.isBlank(signTime) ? "" : signTime);
        map.put("状态", ValueUtils.isBlank(status) ? "" : status);
        if (detail != null) {
            map.put("身份证明号码", ValueUtils.isBlank(detail.getIdCardNo()) ? "" : detail.getIdCardNo());
            map.put("租赁类型", ValueUtils.isBlank(detail.getLeaseType()) ? "" : detail.getLeaseType());
        } else {
            map.put("身份证明号码", "");
            map.put("租赁类型", "");
        }
        return map;
    }
}
