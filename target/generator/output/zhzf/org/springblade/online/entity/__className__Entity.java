package org.springblade.online.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


/**
 * 动态表单字段关联 实体类
 *
 * @author wlpia
 * @since 2022-12-04 17:02:40
 */
@Data
@TableName("dym_out_field_rela")
@ApiModel(value = "OutFieldRela对象", description = "动态表单字段关联")
@EqualsAndHashCode(callSuper = true)
public class OutFieldRelaEntity extends BaseEntity {
	
	/**
	 * 名称
	 */
	@ApiModelProperty(value = "名称")
	@NotBlank(message = "名称不能为空")
	private String name;
	/**
	 * 字段名
	 */
	@ApiModelProperty(value = "字段名")
	@NotBlank(message = "字段名不能为空")
	private String fieldName;
	/**
	 * 表明
	 */
	@ApiModelProperty(value = "表明")
	@NotBlank(message = "表明不能为空")
	private String tableName;
	/**
	 * 字段库ID
	 */
	@ApiModelProperty(value = "字段库ID")
	@NotNull(message = "字段库ID不能为空")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long fieldId;
	/**
	 * 表类型 0:业务系表 1:动态表单
	 */
	@ApiModelProperty(value = "表类型 0:业务系表 1:动态表单")
	@NotNull(message = "表类型 0:业务系表 1:动态表单不能为空")
	private Integer tableType;
	/**
	 * 使用类型 0:关联字段 1：展示字段
	 */
	@ApiModelProperty(value = "使用类型 0:关联字段 1：展示字段")
	@NotNull(message = "使用类型 0:关联字段 1：展示字段不能为空")
	private Integer useType;

}
