package __package__.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import __package__.entity.__class_name__Entity;
import __package__.mapper.__class_name__Mapper;
import __package__.service.I__class_name__Service;
import __package__.vo.__class_name__VO;
import __package__.ro.__class_name__Ro;
import org.springframework.stereotype.Service;

/**
 * __table_comment__ 服务实现类
 *
 * @author __author__
 * @since __create_time__
 */
@Service
public class __class_name__ServiceImpl extends BaseServiceImpl<__class_name__Mapper, __class_name__Entity> implements I__class_name__Service {

	@Override
	public IPage<__class_name__VO> select__class_name__Page(IPage<__class_name__VO> page, __class_name__Ro.List ro) {
		return page.setRecords(baseMapper.select__class_name__Page(page, ro));
	}


}
