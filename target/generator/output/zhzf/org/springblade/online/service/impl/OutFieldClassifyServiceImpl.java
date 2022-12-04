package org.springblade.online.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.online.entity.OutFieldClassifyEntity;
import org.springblade.online.mapper.OutFieldClassifyMapper;
import org.springblade.online.service.IOutFieldClassifyService;
import org.springblade.online.vo.OutFieldClassifyVO;
import org.springblade.online.ro.OutFieldClassifyRo;
import org.springframework.stereotype.Service;

/**
 * 动态表单字段分类 服务实现类
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
@Service
public class OutFieldClassifyServiceImpl extends BaseServiceImpl<OutFieldClassifyMapper, OutFieldClassifyEntity> implements IOutFieldClassifyService {

	@Override
	public IPage<OutFieldClassifyVO> selectOutFieldClassifyPage(IPage<OutFieldClassifyVO> page, OutFieldClassifyRo.List ro) {
		return page.setRecords(baseMapper.selectOutFieldClassifyPage(page, ro));
	}


}
