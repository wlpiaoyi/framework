package org.springblade.online.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.online.entity.OutLinkFiledRelaEntity;
import org.springblade.online.mapper.OutLinkFiledRelaMapper;
import org.springblade.online.service.IOutLinkFiledRelaService;
import org.springblade.online.vo.OutLinkFiledRelaVO;
import org.springblade.online.ro.OutLinkFiledRelaRo;
import org.springframework.stereotype.Service;

/**
 * 动态表单环节字段关联 服务实现类
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
@Service
public class OutLinkFiledRelaServiceImpl extends BaseServiceImpl<OutLinkFiledRelaMapper, OutLinkFiledRelaEntity> implements IOutLinkFiledRelaService {

	@Override
	public IPage<OutLinkFiledRelaVO> selectOutLinkFiledRelaPage(IPage<OutLinkFiledRelaVO> page, OutLinkFiledRelaRo.List ro) {
		return page.setRecords(baseMapper.selectOutLinkFiledRelaPage(page, ro));
	}


}
