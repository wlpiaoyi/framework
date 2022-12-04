package __package__.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import __package__.entity.__className__Entity;
import __package__.ro.__className__Ro;
import __package__.vo.__className__VO;

import java.util.List;

/**
 * __tableComment__ Mapper 接口
 *
 * @author __author__
 * @since __createTime__
 */
public interface __className__Mapper extends BaseMapper<__className__Entity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	List<__className__VO> select__className__Page(IPage page, __className__Ro.List ro);


}
