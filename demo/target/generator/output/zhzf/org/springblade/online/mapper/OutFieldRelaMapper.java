package org.springblade.online.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.online.entity.OutFieldRelaEntity;
import org.springblade.online.ro.OutFieldRelaRo;
import org.springblade.online.vo.OutFieldRelaVO;

import java.util.List;

/**
 * 动态表单字段关联 Mapper 接口
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
public interface OutFieldRelaMapper extends BaseMapper<OutFieldRelaEntity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	List<OutFieldRelaVO> selectOutFieldRelaPage(IPage page, OutFieldRelaRo.List ro);


}
