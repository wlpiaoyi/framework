package __package__.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseServiceImpl;
import __package__.entity.__class_name__;
import __package__.mapper.__class_name__Mapper;
import __package__.service.I__class_name__Service;
import __package__.vo.__class_name__Vo;
import __package__.ro.__class_name__Ro;
import org.springframework.stereotype.Service;


/**
 * {@code @author:} 		__author__
 * {@code @description:} 	__table_comment__ 服务类实现
 * {@code @date:} 			__create_time__
 * {@code @version:}: 		__version__
 */
@Service
public class __class_name__ServiceImpl extends BaseServiceImpl<__class_name__Mapper, __class_name__> implements I__class_name__Service {

	@Override
	public IPage<__class_name__Vo> select__class_name__Page(IPage<__class_name__Vo> page, __class_name__Ro.Query ro) {
		return page.setRecords(baseMapper.select__class_name__Page(page, ro));
	}


}
