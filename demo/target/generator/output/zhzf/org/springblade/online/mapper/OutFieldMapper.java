package org.springblade.online.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.online.entity.OutFieldEntity;
import org.springblade.online.ro.OutFieldRo;
import org.springblade.online.vo.OutFieldVO;

import java.util.List;

/**
 * 动态表单字段库 Mapper 接口
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
public interface OutFieldMapper extends BaseMapper<OutFieldEntity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	List<OutFieldVO> selectOutFieldPage(IPage page, OutFieldRo.List ro);


}
