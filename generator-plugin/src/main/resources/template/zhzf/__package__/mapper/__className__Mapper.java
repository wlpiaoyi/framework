package __package__.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import __package__.entity.__class_name__Entity;
import __package__.ro.__class_name__Ro;
import __package__.vo.__class_name__VO;

import java.util.List;

/**
 * __table_comment__ Mapper 接口
 *
 * @author __author__
 * @since __create_time__
 */
public interface __class_name__Mapper extends BaseMapper<__class_name__Entity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	List<__class_name__VO> select__class_name__Page(IPage page, __class_name__Ro.List ro);


}
