package org.springblade.online.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.online.entity.OutFieldClassifyEntity;
import org.springblade.online.ro.OutFieldClassifyRo;
import org.springblade.online.vo.OutFieldClassifyVO;

import java.util.List;

/**
 * 动态表单字段分类 Mapper 接口
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
public interface OutFieldClassifyMapper extends BaseMapper<OutFieldClassifyEntity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	List<OutFieldClassifyVO> selectOutFieldClassifyPage(IPage page, OutFieldClassifyRo.List ro);


}
