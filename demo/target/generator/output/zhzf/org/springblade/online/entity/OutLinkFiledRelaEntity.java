package org.springblade.online.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


/**
 * 动态表单环节字段关联 实体类
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
@Data
@TableName("dym_out_link_filed_rela")
@ApiModel(value = "OutLinkFiledRela对象", description = "动态表单环节字段关联")
@EqualsAndHashCode(callSuper = true)
public class OutLinkFiledRelaEntity extends BaseEntity {
	
	/**
	 * relaId
	 */
	@ApiModelProperty(value = "relaId")
	@NotNull(message = "relaId不能为空")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long relaId;
	/**
	 * fieldId
	 */
	@ApiModelProperty(value = "fieldId")
	@NotNull(message = "fieldId不能为空")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long fieldId;

}
