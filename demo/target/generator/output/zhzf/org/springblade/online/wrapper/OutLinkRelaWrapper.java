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
import org.springblade.online.entity.OutLinkRelaEntity;
import org.springblade.online.vo.OutLinkRelaVO;
import org.springblade.online.ro.OutLinkRelaRo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 动态表单环节关联 包装类,返回视图层所需的字段
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
public class OutLinkRelaWrapper {

	public static OutLinkRelaVO entityVO(OutLinkRelaEntity outLinkRela) {
		OutLinkRelaVO outLinkRelaVO = Objects.requireNonNull(BeanUtil.copy(outLinkRela, OutLinkRelaVO.class));
		return outLinkRelaVO;
	}


	public static OutLinkRelaVO entityVO(OutLinkRelaRo outLinkRelaRo) {
		OutLinkRelaVO outLinkRelaVO = Objects.requireNonNull(BeanUtil.copy(outLinkRelaRo, OutLinkRelaVO.class));
		return outLinkRelaVO;
	}

	public static OutLinkRelaRo entityRo(OutLinkRelaEntity outLinkRela) {
		OutLinkRelaRo outLinkRelaRo = Objects.requireNonNull(BeanUtil.copy(outLinkRela, OutLinkRelaRo.class));
		return outLinkRelaRo;
	}

	public static OutLinkRelaRo entityRo(OutLinkRelaVO outLinkRelaVO) {
		OutLinkRelaRo outLinkRelaRo = Objects.requireNonNull(BeanUtil.copy(outLinkRelaVO, OutLinkRelaRo.class));
		return outLinkRelaRo;
	}

	public static OutLinkRelaEntity entity(OutLinkRelaRo.List outLinkRelaRo) {
		OutLinkRelaEntity outLinkRela = Objects.requireNonNull(BeanUtil.copy(outLinkRelaRo, OutLinkRelaEntity.class));
		return outLinkRela;
	}

	public static OutLinkRelaEntity entity(OutLinkRelaRo.Submit outLinkRelaRo) {
		OutLinkRelaEntity outLinkRela = Objects.requireNonNull(BeanUtil.copy(outLinkRelaRo, OutLinkRelaEntity.class));
		return outLinkRela;
	}


	public static OutLinkRelaEntity entity(OutLinkRelaVO outLinkRelaVO) {
		OutLinkRelaEntity outLinkRela = Objects.requireNonNull(BeanUtil.copy(outLinkRelaVO, OutLinkRelaEntity.class));
		return outLinkRela;
	}

	public static List<OutLinkRelaVO> listVO(List<OutLinkRelaEntity> list) {
		return list.stream().map(OutLinkRelaWrapper::entityVO).collect(Collectors.toList());
	}

	public static IPage<OutLinkRelaVO> pageVO(IPage<OutLinkRelaEntity> pages) {
		List<OutLinkRelaVO> records = OutLinkRelaWrapper.listVO(pages.getRecords());
		IPage<OutLinkRelaVO> pageVo = new Page(pages.getCurrent(), pages.getSize(), pages.getTotal());
		pageVo.setRecords(records);
		return pageVo;
	}

}
