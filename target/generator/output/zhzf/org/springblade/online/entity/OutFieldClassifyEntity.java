package org.springblade.online.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 动态表单字段分类 实体类
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
@Data
@TableName("dym_out_field_classify")
@ApiModel(value = "OutFieldClassify对象", description = "动态表单字段分类")
@EqualsAndHashCode(callSuper = true)
public class OutFieldClassifyEntity extends BaseEntity {
	
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
	 * 编码
	 */
	@ApiModelProperty(value = "编码")
	@NotBlank(message = "编码不能为空")
	private String code;
	/**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	@NotNull(message = "排序不能为空")
	private Integer sort;

}
