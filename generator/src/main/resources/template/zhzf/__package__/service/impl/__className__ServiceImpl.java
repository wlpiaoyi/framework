package __package__.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import __package__.entity.__className__Entity;
import __package__.mapper.__className__Mapper;
import __package__.service.I__className__Service;
import __package__.vo.__className__VO;
import __package__.ro.__className__Ro;
import org.springframework.stereotype.Service;

/**
 * __tableComment__ 服务实现类
 *
 * @author __author__
 * @since __createTime__
 */
@Service
public class __className__ServiceImpl extends BaseServiceImpl<__className__Mapper, __className__Entity> implements I__className__Service {

	@Override
	public IPage<__className__VO> select__className__Page(IPage<__className__VO> page, __className__Ro.List ro) {
		return page.setRecords(baseMapper.select__className__Page(page, ro));
	}


}
