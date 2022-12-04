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
 * 动态表单字段库 实体类
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
@Data
@TableName("dym_out_field")
@ApiModel(value = "OutField对象", description = "动态表单字段库")
@EqualsAndHashCode(callSuper = true)
public class OutFieldEntity extends BaseEntity {
	
	/**
	 * 名称
	 */
	@ApiModelProperty(value = "名称")
	@NotBlank(message = "名称不能为空")
	private String name;
	/**
	 * 描述信息
	 */
	@ApiModelProperty(value = "描述信息")
	private String info;
	/**
	 * 分类ID
	 */
	@ApiModelProperty(value = "分类ID")
	@NotNull(message = "分类ID不能为空")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long classifyId;
	/**
	 * type
	 */
	@ApiModelProperty(value = "type")
	@NotNull(message = "type不能为空")
	private Integer type;
	/**
	 * 是否必填
	 */
	@ApiModelProperty(value = "是否必填")
	@NotNull(message = "是否必填不能为空")
	private Integer isNeed;
	/**
	 * 是否不能为空
	 */
	@ApiModelProperty(value = "是否不能为空")
	@NotNull(message = "是否不能为空不能为空")
	private Integer isNotNull;
	/**
	 * 是否常用
	 */
	@ApiModelProperty(value = "是否常用")
	@NotNull(message = "是否常用不能为空")
	private Integer isCommon;
	/**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	@NotNull(message = "排序不能为空")
	private Integer sort;
	/**
	 * 是否唯一
	 */
	@ApiModelProperty(value = "是否唯一")
	@NotNull(message = "是否唯一不能为空")
	private Integer isUnique;
	/**
	 * 数据api
	 */
	@ApiModelProperty(value = "数据api")
	private String dataApi;

}
