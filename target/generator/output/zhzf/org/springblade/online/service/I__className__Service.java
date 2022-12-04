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
package org.springblade.online.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.online.entity.OutFieldRelaEntity;
import org.springblade.online.vo.OutFieldRelaVO;
import org.springblade.online.ro.OutFieldRelaRo;

/**
 * 动态表单字段关联 服务类
 *
 * @author wlpia
 * @since 2022-12-04 17:02:40
 */
public interface IOutFieldRelaService extends BaseService<OutFieldRelaEntity> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param ro
	 * @return
	 */
	IPage<OutFieldRelaVO> selectOutFieldRelaPage(IPage<OutFieldRelaVO> page, OutFieldRelaRo.List ro);

}
