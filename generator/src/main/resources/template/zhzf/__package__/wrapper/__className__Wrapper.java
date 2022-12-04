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
package __package__.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springblade.core.tool.utils.BeanUtil;
import __package__.entity.__className__Entity;
import __package__.vo.__className__VO;
import __package__.ro.__className__Ro;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * __tableComment__ 包装类,返回视图层所需的字段
 *
 * @author __author__
 * @since __createTime__
 */
public class __className__Wrapper {

	public static __className__VO entityVO(__className__Entity __pClassName__) {
		__className__VO __pClassName__VO = Objects.requireNonNull(BeanUtil.copy(__pClassName__, __className__VO.class));
		return __pClassName__VO;
	}


	public static __className__VO entityVO(__className__Ro __pClassName__Ro) {
		__className__VO __pClassName__VO = Objects.requireNonNull(BeanUtil.copy(__pClassName__Ro, __className__VO.class));
		return __pClassName__VO;
	}

	public static __className__Ro entityRo(__className__Entity __pClassName__) {
		__className__Ro __pClassName__Ro = Objects.requireNonNull(BeanUtil.copy(__pClassName__, __className__Ro.class));
		return __pClassName__Ro;
	}

	public static __className__Ro entityRo(__className__VO __pClassName__VO) {
		__className__Ro __pClassName__Ro = Objects.requireNonNull(BeanUtil.copy(__pClassName__VO, __className__Ro.class));
		return __pClassName__Ro;
	}

	public static __className__Entity entity(__className__Ro.List __pClassName__Ro) {
		__className__Entity __pClassName__ = Objects.requireNonNull(BeanUtil.copy(__pClassName__Ro, __className__Entity.class));
		return __pClassName__;
	}

	public static __className__Entity entity(__className__Ro.Submit __pClassName__Ro) {
		__className__Entity __pClassName__ = Objects.requireNonNull(BeanUtil.copy(__pClassName__Ro, __className__Entity.class));
		return __pClassName__;
	}


	public static __className__Entity entity(__className__VO __pClassName__VO) {
		__className__Entity __pClassName__ = Objects.requireNonNull(BeanUtil.copy(__pClassName__VO, __className__Entity.class));
		return __pClassName__;
	}

	public static List<__className__VO> listVO(List<__className__Entity> list) {
		return list.stream().map(__className__Wrapper::entityVO).collect(Collectors.toList());
	}

	public static IPage<__className__VO> pageVO(IPage<__className__Entity> pages) {
		List<__className__VO> records = __className__Wrapper.listVO(pages.getRecords());
		IPage<__className__VO> pageVo = new Page(pages.getCurrent(), pages.getSize(), pages.getTotal());
		pageVo.setRecords(records);
		return pageVo;
	}

}
