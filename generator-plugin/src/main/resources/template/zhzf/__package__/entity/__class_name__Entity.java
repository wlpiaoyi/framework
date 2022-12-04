package __package__.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springblade.core.mp.base.BaseEntity;
/*__import__*/

/**
 * __table_comment__ 实体类
 *
 * @author __author__
 * @since __create_time__
 */
@Data
@TableName("__table_name__")
@ApiModel(value = "__class_name__对象", description = "__table_comment__")
@EqualsAndHashCode(callSuper = true)
public class __class_name__Entity extends BaseEntity {
	/*__fields__*/
}
