package org.springblade.online.ro;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.support.Query;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


/**
 * 动态表单字段关联 请求包装类
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */


public class OutFieldRelaRo {
    @Data
    @ApiModel("动态表单字段关联 请求实例")
    public static class List extends Query{
        
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

    @Data
    @ApiModel("动态表单字段关联 请求实例")
    public static class Submit{
        
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
}
