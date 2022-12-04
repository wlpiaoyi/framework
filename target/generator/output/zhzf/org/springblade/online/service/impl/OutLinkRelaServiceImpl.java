package org.springblade.online.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.online.entity.OutLinkRelaEntity;
import org.springblade.online.mapper.OutLinkRelaMapper;
import org.springblade.online.service.IOutLinkRelaService;
import org.springblade.online.vo.OutLinkRelaVO;
import org.springblade.online.ro.OutLinkRelaRo;
import org.springframework.stereotype.Service;

/**
 * 动态表单环节关联 服务实现类
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
@Service
public class OutLinkRelaServiceImpl extends BaseServiceImpl<OutLinkRelaMapper, OutLinkRelaEntity> implements IOutLinkRelaService {

	@Override
	public IPage<OutLinkRelaVO> selectOutLinkRelaPage(IPage<OutLinkRelaVO> page, OutLinkRelaRo.List ro) {
		return page.setRecords(baseMapper.selectOutLinkRelaPage(page, ro));
	}


}
