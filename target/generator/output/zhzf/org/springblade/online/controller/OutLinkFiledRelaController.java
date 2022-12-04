package org.springblade.online.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.online.entity.OutLinkFiledRelaEntity;
import org.springblade.online.service.IOutLinkFiledRelaService;
import org.springblade.online.vo.OutLinkFiledRelaVO;
import org.springblade.online.ro.OutLinkFiledRelaRo;
import org.springblade.online.wrapper.OutLinkFiledRelaWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 动态表单环节字段关联 控制器
 *
 * @author wlpia
 * @since 2022-12-02 18:00:07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/out_link_filed_rela")
@Api(value = "动态表单环节字段关联", tags = "动态表单环节字段关联接口")
public class OutLinkFiledRelaController extends BladeController {

	private final IOutLinkFiledRelaService outLinkFiledRelaService;

	/**
	 * 动态表单环节字段关联 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入OutLinkFiledRela")
	public R<OutLinkFiledRelaVO> detail(@RequestParam Long id) {
		OutLinkFiledRelaEntity detail = outLinkFiledRelaService.getById(id);
		return R.data(OutLinkFiledRelaWrapper.entityVO(detail));
	}

	/**
	 * 动态表单字段分类 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入OutLinkFiledRela")
	public R<IPage<OutLinkFiledRelaVO>> list(@RequestBody OutLinkFiledRelaRo.List body) {
		IPage<OutLinkFiledRelaEntity> pages = outLinkFiledRelaService.page(Condition.getPage(body),
				Condition.getQueryWrapper(OutLinkFiledRelaWrapper.entity(body)));
		return R.data(OutLinkFiledRelaWrapper.pageVO(pages));
	}

//	/**
//	 * 动态表单环节字段关联 自定义分页
//	 */
//	@PostMapping("/list")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入OutLinkFiledRela")
//	public R<IPage<OutLinkFiledRelaVO>> list(@RequestBody OutLinkFiledRelaRo.List body) {
//		IPage<OutLinkFiledRelaVO> pages = outLinkFiledRelaService.selectOutLinkFiledRelaPage(Condition.getPage(body), body);
//		return R.data(pages);
//	}

	/**
	 * 动态表单环节字段关联 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入OutLinkFiledRela")
	public R save(@Valid @RequestBody OutLinkFiledRelaRo.Submit body) {
		return R.status(outLinkFiledRelaService.save(OutLinkFiledRelaWrapper.entity(body)));
	}

	/**
	 * 动态表单环节字段关联 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入OutLinkFiledRela")
	public R update(@Valid @RequestBody OutLinkFiledRelaRo.Submit body) {
		return R.status(outLinkFiledRelaService.updateById(OutLinkFiledRelaWrapper.entity(body)));
	}

	/**
	 * 动态表单环节字段关联 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入OutLinkFiledRela")
	public R submit(@Valid @RequestBody OutLinkFiledRelaRo.Submit body) {
		return R.status(outLinkFiledRelaService.saveOrUpdate(OutLinkFiledRelaWrapper.entity(body)));
	}

	/**
	 * 动态表单环节字段关联 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(outLinkFiledRelaService.deleteLogic(Func.toLongList(ids)));
	}

}
