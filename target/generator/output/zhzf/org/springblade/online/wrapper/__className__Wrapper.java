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
import org.springblade.online.entity.OutFieldRelaEntity;
import org.springblade.online.vo.OutFieldRelaVO;
import org.springblade.online.ro.OutFieldRelaRo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 动态表单字段关联 包装类,返回视图层所需的字段
 *
 * @author wlpia
 * @since 2022-12-04 17:02:40
 */
public class OutFieldRelaWrapper {

	public static OutFieldRelaVO entityVO(OutFieldRelaEntity outFieldRela) {
		OutFieldRelaVO outFieldRelaVO = Objects.requireNonNull(BeanUtil.copy(outFieldRela, OutFieldRelaVO.class));
		return outFieldRelaVO;
	}


	public static OutFieldRelaVO entityVO(OutFieldRelaRo outFieldRelaRo) {
		OutFieldRelaVO outFieldRelaVO = Objects.requireNonNull(BeanUtil.copy(outFieldRelaRo, OutFieldRelaVO.class));
		return outFieldRelaVO;
	}

	public static OutFieldRelaRo entityRo(OutFieldRelaEntity outFieldRela) {
		OutFieldRelaRo outFieldRelaRo = Objects.requireNonNull(BeanUtil.copy(outFieldRela, OutFieldRelaRo.class));
		return outFieldRelaRo;
	}

	public static OutFieldRelaRo entityRo(OutFieldRelaVO outFieldRelaVO) {
		OutFieldRelaRo outFieldRelaRo = Objects.requireNonNull(BeanUtil.copy(outFieldRelaVO, OutFieldRelaRo.class));
		return outFieldRelaRo;
	}

	public static OutFieldRelaEntity entity(OutFieldRelaRo.List outFieldRelaRo) {
		OutFieldRelaEntity outFieldRela = Objects.requireNonNull(BeanUtil.copy(outFieldRelaRo, OutFieldRelaEntity.class));
		return outFieldRela;
	}

	public static OutFieldRelaEntity entity(OutFieldRelaRo.Submit outFieldRelaRo) {
		OutFieldRelaEntity outFieldRela = Objects.requireNonNull(BeanUtil.copy(outFieldRelaRo, OutFieldRelaEntity.class));
		return outFieldRela;
	}


	public static OutFieldRelaEntity entity(OutFieldRelaVO outFieldRelaVO) {
		OutFieldRelaEntity outFieldRela = Objects.requireNonNull(BeanUtil.copy(outFieldRelaVO, OutFieldRelaEntity.class));
		return outFieldRela;
	}

	public static List<OutFieldRelaVO> listVO(List<OutFieldRelaEntity> list) {
		return list.stream().map(OutFieldRelaWrapper::entityVO).collect(Collectors.toList());
	}

	public static IPage<OutFieldRelaVO> pageVO(IPage<OutFieldRelaEntity> pages) {
		List<OutFieldRelaVO> records = OutFieldRelaWrapper.listVO(pages.getRecords());
		IPage<OutFieldRelaVO> pageVo = new Page(pages.getCurrent(), pages.getSize(), pages.getTotal());
		pageVo.setRecords(records);
		return pageVo;
	}

}
