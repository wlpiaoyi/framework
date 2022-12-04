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
import org.springblade.online.entity.OutFieldClassifyEntity;
import org.springblade.online.vo.OutFieldClassifyVO;
import org.springblade.online.ro.OutFieldClassifyRo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 动态表单字段分类 包装类,返回视图层所需的字段
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
public class OutFieldClassifyWrapper {

	public static OutFieldClassifyVO entityVO(OutFieldClassifyEntity outFieldClassify) {
		OutFieldClassifyVO outFieldClassifyVO = Objects.requireNonNull(BeanUtil.copy(outFieldClassify, OutFieldClassifyVO.class));
		return outFieldClassifyVO;
	}


	public static OutFieldClassifyVO entityVO(OutFieldClassifyRo outFieldClassifyRo) {
		OutFieldClassifyVO outFieldClassifyVO = Objects.requireNonNull(BeanUtil.copy(outFieldClassifyRo, OutFieldClassifyVO.class));
		return outFieldClassifyVO;
	}

	public static OutFieldClassifyRo entityRo(OutFieldClassifyEntity outFieldClassify) {
		OutFieldClassifyRo outFieldClassifyRo = Objects.requireNonNull(BeanUtil.copy(outFieldClassify, OutFieldClassifyRo.class));
		return outFieldClassifyRo;
	}

	public static OutFieldClassifyRo entityRo(OutFieldClassifyVO outFieldClassifyVO) {
		OutFieldClassifyRo outFieldClassifyRo = Objects.requireNonNull(BeanUtil.copy(outFieldClassifyVO, OutFieldClassifyRo.class));
		return outFieldClassifyRo;
	}

	public static OutFieldClassifyEntity entity(OutFieldClassifyRo.List outFieldClassifyRo) {
		OutFieldClassifyEntity outFieldClassify = Objects.requireNonNull(BeanUtil.copy(outFieldClassifyRo, OutFieldClassifyEntity.class));
		return outFieldClassify;
	}

	public static OutFieldClassifyEntity entity(OutFieldClassifyRo.Submit outFieldClassifyRo) {
		OutFieldClassifyEntity outFieldClassify = Objects.requireNonNull(BeanUtil.copy(outFieldClassifyRo, OutFieldClassifyEntity.class));
		return outFieldClassify;
	}


	public static OutFieldClassifyEntity entity(OutFieldClassifyVO outFieldClassifyVO) {
		OutFieldClassifyEntity outFieldClassify = Objects.requireNonNull(BeanUtil.copy(outFieldClassifyVO, OutFieldClassifyEntity.class));
		return outFieldClassify;
	}

	public static List<OutFieldClassifyVO> listVO(List<OutFieldClassifyEntity> list) {
		return list.stream().map(OutFieldClassifyWrapper::entityVO).collect(Collectors.toList());
	}

	public static IPage<OutFieldClassifyVO> pageVO(IPage<OutFieldClassifyEntity> pages) {
		List<OutFieldClassifyVO> records = OutFieldClassifyWrapper.listVO(pages.getRecords());
		IPage<OutFieldClassifyVO> pageVo = new Page(pages.getCurrent(), pages.getSize(), pages.getTotal());
		pageVo.setRecords(records);
		return pageVo;
	}

}
