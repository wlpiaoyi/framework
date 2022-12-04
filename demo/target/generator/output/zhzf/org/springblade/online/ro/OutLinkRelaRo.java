package org.springblade.online.ro;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.support.Query;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


/**
 * 动态表单环节关联 请求包装类
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */


public class OutLinkRelaRo {
    @Data
    @ApiModel("动态表单环节关联 请求实例")
    public static class List extends Query{
        
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

    @Data
    @ApiModel("动态表单环节关联 请求实例")
    public static class Submit{
        
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
}
