package org.springblade.online.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.online.entity.OutLinkFiledRelaEntity;
import org.springblade.online.ro.OutLinkFiledRelaRo;
import org.springblade.online.vo.OutLinkFiledRelaVO;

import java.util.List;

/**
 * 动态表单环节字段关联 Mapper 接口
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
public interface OutLinkFiledRelaMapper extends BaseMapper<OutLinkFiledRelaEntity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	List<OutLinkFiledRelaVO> selectOutLinkFiledRelaPage(IPage page, OutLinkFiledRelaRo.List ro);


}
