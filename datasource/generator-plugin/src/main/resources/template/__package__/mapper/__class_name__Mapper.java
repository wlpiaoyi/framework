package __package__.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import __package__.entity.__class_name__;
import __package__.ro.__class_name__Ro;
import __package__.vo.__class_name__Vo;

import java.util.List;

/**
 * {@code @author:} 		__author__
 * {@code @description:} 	__table_comment__ Mapper 接口
 * {@code @date:} 			__create_time__
 * {@code @version:}: 		__version__
 */
public interface __class_name__Mapper extends BaseMapper<__class_name__> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	List<__class_name__Vo> select__class_name__Page(IPage page, __class_name__Ro.Query ro);


}
