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
 * 动态表单环节字段关联 请求包装类
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */


public class OutLinkFiledRelaRo {
    @Data
    @ApiModel("动态表单环节字段关联 请求实例")
    public static class List extends Query{
        
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

    @Data
    @ApiModel("动态表单环节字段关联 请求实例")
    public static class Submit{
        
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
}
