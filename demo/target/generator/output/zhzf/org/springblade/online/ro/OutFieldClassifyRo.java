package org.springblade.online.ro;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.support.Query;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 动态表单字段分类 请求包装类
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */


public class OutFieldClassifyRo {
    @Data
    @ApiModel("动态表单字段分类 请求实例")
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

    @Data
    @ApiModel("动态表单字段分类 请求实例")
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
}
