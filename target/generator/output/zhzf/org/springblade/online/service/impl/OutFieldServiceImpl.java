package org.springblade.online.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.online.entity.OutFieldEntity;
import org.springblade.online.mapper.OutFieldMapper;
import org.springblade.online.service.IOutFieldService;
import org.springblade.online.vo.OutFieldVO;
import org.springblade.online.ro.OutFieldRo;
import org.springframework.stereotype.Service;

/**
 * 动态表单字段库 服务实现类
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
@Service
public class OutFieldServiceImpl extends BaseServiceImpl<OutFieldMapper, OutFieldEntity> implements IOutFieldService {

	@Override
	public IPage<OutFieldVO> selectOutFieldPage(IPage<OutFieldVO> page, OutFieldRo.List ro) {
		return page.setRecords(baseMapper.selectOutFieldPage(page, ro));
	}


}
