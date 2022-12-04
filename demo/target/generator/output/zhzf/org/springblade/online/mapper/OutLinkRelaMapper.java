package org.springblade.online.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.online.entity.OutLinkRelaEntity;
import org.springblade.online.ro.OutLinkRelaRo;
import org.springblade.online.vo.OutLinkRelaVO;

import java.util.List;

/**
 * 动态表单环节关联 Mapper 接口
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
public interface OutLinkRelaMapper extends BaseMapper<OutLinkRelaEntity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	List<OutLinkRelaVO> selectOutLinkRelaPage(IPage page, OutLinkRelaRo.List ro);


}
