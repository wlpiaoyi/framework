package org.springblade.online.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.online.entity.OutFieldRelaEntity;
import org.springblade.online.mapper.OutFieldRelaMapper;
import org.springblade.online.service.IOutFieldRelaService;
import org.springblade.online.vo.OutFieldRelaVO;
import org.springblade.online.ro.OutFieldRelaRo;
import org.springframework.stereotype.Service;

/**
 * 动态表单字段关联 服务实现类
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
@Service
public class OutFieldRelaServiceImpl extends BaseServiceImpl<OutFieldRelaMapper, OutFieldRelaEntity> implements IOutFieldRelaService {

	@Override
	public IPage<OutFieldRelaVO> selectOutFieldRelaPage(IPage<OutFieldRelaVO> page, OutFieldRelaRo.List ro) {
		return page.setRecords(baseMapper.selectOutFieldRelaPage(page, ro));
	}


}
