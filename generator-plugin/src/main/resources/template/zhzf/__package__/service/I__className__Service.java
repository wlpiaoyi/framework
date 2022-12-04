/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package __package__.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import __package__.entity.__class_name__Entity;
import __package__.vo.__class_name__VO;
import __package__.ro.__class_name__Ro;

/**
 * __table_comment__ 服务类
 *
 * @author __author__
 * @since __create_time__
 */
public interface I__class_name__Service extends BaseService<__class_name__Entity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	IPage<__class_name__VO> select__class_name__Page(IPage<__class_name__VO> page, __class_name__Ro.List ro);

}
