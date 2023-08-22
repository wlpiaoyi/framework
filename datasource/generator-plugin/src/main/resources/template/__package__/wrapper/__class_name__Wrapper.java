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
import __package__.entity.__class_name__;
import __package__.vo.__class_name__Vo;
import __package__.ro.__class_name__Ro;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@code @author:} 		__author__
 * {@code @description:} 	__table_comment__ 包装类,返回视图层所需的字段
 * {@code @date:} 			__create_time__
 * {@code @version:}: 		__version__
 */
public class __class_name__Wrapper {

	public static __class_name__Vo entityVO(__class_name__ __class_var_name__) {
		__class_name__Vo __class_var_name__Vo = Objects.requireNonNull(BeanUtil.copy(__class_var_name__, __class_name__Vo.class));
		return __class_var_name__Vo;
	}


	public static __class_name__Vo entityVO(__class_name__Ro __class_var_name__Ro) {
		__class_name__Vo __class_var_name__Vo = Objects.requireNonNull(BeanUtil.copy(__class_var_name__Ro, __class_name__Vo.class));
		return __class_var_name__Vo;
	}

	public static __class_name__Ro entityRo(__class_name__ __class_var_name__) {
		__class_name__Ro __class_var_name__Ro = Objects.requireNonNull(BeanUtil.copy(__class_var_name__, __class_name__Ro.class));
		return __class_var_name__Ro;
	}

	public static __class_name__Ro entityRo(__class_name__Vo __class_var_name__Vo) {
		__class_name__Ro __class_var_name__Ro = Objects.requireNonNull(BeanUtil.copy(__class_var_name__Vo, __class_name__Ro.class));
		return __class_var_name__Ro;
	}

	public static __class_name__ entity(__class_name__Ro.Query __class_var_name__Ro) {
		__class_name__ __class_var_name__ = Objects.requireNonNull(BeanUtil.copy(__class_var_name__Ro, __class_name__.class));
		return __class_var_name__;
	}

	public static __class_name__ entity(__class_name__Ro.Submit __class_var_name__Ro) {
		__class_name__ __class_var_name__ = Objects.requireNonNull(BeanUtil.copy(__class_var_name__Ro, __class_name__.class));
		return __class_var_name__;
	}


	public static __class_name__ entity(__class_name__Vo __class_var_name__Vo) {
		__class_name__ __class_var_name__ = Objects.requireNonNull(BeanUtil.copy(__class_var_name__Vo, __class_name__.class));
		return __class_var_name__;
	}
	public static List<__class_name__> list(List<__class_name__Ro.Submit> list) {
		return list.stream().map(__class_name__Wrapper::entity).collect(Collectors.toList());
	}

	public static List<__class_name__Vo> listVO(List<__class_name__> list) {
		return list.stream().map(__class_name__Wrapper::entityVO).collect(Collectors.toList());
	}

	public static IPage<__class_name__Vo> pageVO(IPage<__class_name__> pages) {
		List<__class_name__Vo> records = __class_name__Wrapper.listVO(pages.getRecords());
		IPage<__class_name__Vo> pageVo = new Page(pages.getCurrent(), pages.getSize(), pages.getTotal());
		pageVo.setRecords(records);
		return pageVo;
	}

}
