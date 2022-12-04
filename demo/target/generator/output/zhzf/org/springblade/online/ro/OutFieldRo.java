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
 * 动态表单字段库 请求包装类
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */


public class OutFieldRo {
    @Data
    @ApiModel("动态表单字段库 请求实例")
    public static class List extends Query{
        
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

    @Data
    @ApiModel("动态表单字段库 请求实例")
    public static class Submit{
        
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
}
