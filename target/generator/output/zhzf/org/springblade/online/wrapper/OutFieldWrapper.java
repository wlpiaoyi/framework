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
package org.springblade.online.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.online.entity.OutFieldEntity;
import org.springblade.online.vo.OutFieldVO;
import org.springblade.online.ro.OutFieldRo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 动态表单字段库 包装类,返回视图层所需的字段
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
public class OutFieldWrapper {

	public static OutFieldVO entityVO(OutFieldEntity outField) {
		OutFieldVO outFieldVO = Objects.requireNonNull(BeanUtil.copy(outField, OutFieldVO.class));
		return outFieldVO;
	}


	public static OutFieldVO entityVO(OutFieldRo outFieldRo) {
		OutFieldVO outFieldVO = Objects.requireNonNull(BeanUtil.copy(outFieldRo, OutFieldVO.class));
		return outFieldVO;
	}

	public static OutFieldRo entityRo(OutFieldEntity outField) {
		OutFieldRo outFieldRo = Objects.requireNonNull(BeanUtil.copy(outField, OutFieldRo.class));
		return outFieldRo;
	}

	public static OutFieldRo entityRo(OutFieldVO outFieldVO) {
		OutFieldRo outFieldRo = Objects.requireNonNull(BeanUtil.copy(outFieldVO, OutFieldRo.class));
		return outFieldRo;
	}

	public static OutFieldEntity entity(OutFieldRo.List outFieldRo) {
		OutFieldEntity outField = Objects.requireNonNull(BeanUtil.copy(outFieldRo, OutFieldEntity.class));
		return outField;
	}

	public static OutFieldEntity entity(OutFieldRo.Submit outFieldRo) {
		OutFieldEntity outField = Objects.requireNonNull(BeanUtil.copy(outFieldRo, OutFieldEntity.class));
		return outField;
	}


	public static OutFieldEntity entity(OutFieldVO outFieldVO) {
		OutFieldEntity outField = Objects.requireNonNull(BeanUtil.copy(outFieldVO, OutFieldEntity.class));
		return outField;
	}

	public static List<OutFieldVO> listVO(List<OutFieldEntity> list) {
		return list.stream().map(OutFieldWrapper::entityVO).collect(Collectors.toList());
	}

	public static IPage<OutFieldVO> pageVO(IPage<OutFieldEntity> pages) {
		List<OutFieldVO> records = OutFieldWrapper.listVO(pages.getRecords());
		IPage<OutFieldVO> pageVo = new Page(pages.getCurrent(), pages.getSize(), pages.getTotal());
		pageVo.setRecords(records);
		return pageVo;
	}

}
