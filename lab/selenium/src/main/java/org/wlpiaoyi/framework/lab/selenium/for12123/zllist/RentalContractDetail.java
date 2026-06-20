package org.wlpiaoyi.framework.lab.selenium.for12123.zllist;

import lombok.Builder;
import lombok.Data;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class RentalContractDetail {

    /** 合同编号 */
    private String contractNo;

    /** 身份证明号码 */
    private String idCardNo;

    /** 合同有效期始 */
    private String validityStart;

    /** 租赁类型 */
    private String leaseType;

    /** 合同有效期止 */
    private String validityEnd;

    /** 合同签订日期 */
    private String signDate;

    /** 号牌种类 */
    private String plateType;

    /** 号牌号码 */
    private String carNo;

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("合同编号", ValueUtils.isBlank(contractNo) ? "" : contractNo);
        map.put("身份证明号码", ValueUtils.isBlank(idCardNo) ? "" : idCardNo);
        map.put("合同有效期始", ValueUtils.isBlank(validityStart) ? "" : validityStart);
        map.put("租赁类型", ValueUtils.isBlank(leaseType) ? "" : leaseType);
        map.put("合同有效期止", ValueUtils.isBlank(validityEnd) ? "" : validityEnd);
        map.put("合同签订日期", ValueUtils.isBlank(signDate) ? "" : signDate);
        map.put("号牌种类", ValueUtils.isBlank(plateType) ? "" : plateType);
        map.put("号牌号码", ValueUtils.isBlank(carNo) ? "" : carNo);
        return map;
    }
}
