package __package__.ro;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/*__import__*/

/**
 * {@code @author:} 		__author__
 * {@code @description:} 	__table_comment__ 请求包装类
 * {@code @date:} 			__create_time__
 * {@code @version:}: 		__version__
 */
public class __class_name__Ro {
    @Data
    @ApiModel("__table_comment__ 请求实例")
	public static class Query extends org.springblade.core.mp.support.Query{
        /*__fields_2__*/
    }

    @Data
    @ApiModel("__table_comment__ 请求实例")
    public static class Submit{
		@JsonSerialize(
			using = ToStringSerializer.class
		)
		@ApiModelProperty("主键id")
		@TableId(
			value = "id",
			type = IdType.ASSIGN_ID
		)
		private Long id;/*__fields_2__*/
    }
}
