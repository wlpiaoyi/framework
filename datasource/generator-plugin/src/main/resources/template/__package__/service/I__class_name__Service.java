package __package__.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import __package__.entity.__class_name__;
import __package__.vo.__class_name__Vo;
import __package__.ro.__class_name__Ro;


/**
 * {@code @author:} 		__author__
 * {@code @description:} 	__table_comment__ 服务类接口
 * {@code @date:} 			__create_time__
 * {@code @version:}: 		__version__
 */
public interface I__class_name__Service extends BaseService<__class_name__> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	IPage<__class_name__Vo> select__class_name__Page(IPage<__class_name__Vo> page, __class_name__Ro.Query ro);

}
