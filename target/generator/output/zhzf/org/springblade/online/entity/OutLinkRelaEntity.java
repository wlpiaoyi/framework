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
 * 动态表单环节关联 实体类
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
@Data
@TableName("dym_out_link_rela")
@ApiModel(value = "OutLinkRela对象", description = "动态表单环节关联")
@EqualsAndHashCode(callSuper = true)
public class OutLinkRelaEntity extends BaseEntity {
	
	/**
	 * 环节id
	 */
	@ApiModelProperty(value = "环节id")
	@NotNull(message = "环节id不能为空")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long linkId;
	/**
	 * 流程id
	 */
	@ApiModelProperty(value = "流程id")
	@NotNull(message = "流程id不能为空")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long progressId;
	/**
	 * 监听api
	 */
	@ApiModelProperty(value = "监听api")
	private String listenerApi;

}
