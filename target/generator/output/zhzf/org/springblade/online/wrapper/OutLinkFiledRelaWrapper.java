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
import org.springblade.online.entity.OutLinkFiledRelaEntity;
import org.springblade.online.vo.OutLinkFiledRelaVO;
import org.springblade.online.ro.OutLinkFiledRelaRo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 动态表单环节字段关联 包装类,返回视图层所需的字段
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
public class OutLinkFiledRelaWrapper {

	public static OutLinkFiledRelaVO entityVO(OutLinkFiledRelaEntity outLinkFiledRela) {
		OutLinkFiledRelaVO outLinkFiledRelaVO = Objects.requireNonNull(BeanUtil.copy(outLinkFiledRela, OutLinkFiledRelaVO.class));
		return outLinkFiledRelaVO;
	}


	public static OutLinkFiledRelaVO entityVO(OutLinkFiledRelaRo outLinkFiledRelaRo) {
		OutLinkFiledRelaVO outLinkFiledRelaVO = Objects.requireNonNull(BeanUtil.copy(outLinkFiledRelaRo, OutLinkFiledRelaVO.class));
		return outLinkFiledRelaVO;
	}

	public static OutLinkFiledRelaRo entityRo(OutLinkFiledRelaEntity outLinkFiledRela) {
		OutLinkFiledRelaRo outLinkFiledRelaRo = Objects.requireNonNull(BeanUtil.copy(outLinkFiledRela, OutLinkFiledRelaRo.class));
		return outLinkFiledRelaRo;
	}

	public static OutLinkFiledRelaRo entityRo(OutLinkFiledRelaVO outLinkFiledRelaVO) {
		OutLinkFiledRelaRo outLinkFiledRelaRo = Objects.requireNonNull(BeanUtil.copy(outLinkFiledRelaVO, OutLinkFiledRelaRo.class));
		return outLinkFiledRelaRo;
	}

	public static OutLinkFiledRelaEntity entity(OutLinkFiledRelaRo.List outLinkFiledRelaRo) {
		OutLinkFiledRelaEntity outLinkFiledRela = Objects.requireNonNull(BeanUtil.copy(outLinkFiledRelaRo, OutLinkFiledRelaEntity.class));
		return outLinkFiledRela;
	}

	public static OutLinkFiledRelaEntity entity(OutLinkFiledRelaRo.Submit outLinkFiledRelaRo) {
		OutLinkFiledRelaEntity outLinkFiledRela = Objects.requireNonNull(BeanUtil.copy(outLinkFiledRelaRo, OutLinkFiledRelaEntity.class));
		return outLinkFiledRela;
	}


	public static OutLinkFiledRelaEntity entity(OutLinkFiledRelaVO outLinkFiledRelaVO) {
		OutLinkFiledRelaEntity outLinkFiledRela = Objects.requireNonNull(BeanUtil.copy(outLinkFiledRelaVO, OutLinkFiledRelaEntity.class));
		return outLinkFiledRela;
	}

	public static List<OutLinkFiledRelaVO> listVO(List<OutLinkFiledRelaEntity> list) {
		return list.stream().map(OutLinkFiledRelaWrapper::entityVO).collect(Collectors.toList());
	}

	public static IPage<OutLinkFiledRelaVO> pageVO(IPage<OutLinkFiledRelaEntity> pages) {
		List<OutLinkFiledRelaVO> records = OutLinkFiledRelaWrapper.listVO(pages.getRecords());
		IPage<OutLinkFiledRelaVO> pageVo = new Page(pages.getCurrent(), pages.getSize(), pages.getTotal());
		pageVo.setRecords(records);
		return pageVo;
	}

}
