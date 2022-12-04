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
import org.springblade.online.entity.OutLinkRelaEntity;
import org.springblade.online.service.IOutLinkRelaService;
import org.springblade.online.vo.OutLinkRelaVO;
import org.springblade.online.ro.OutLinkRelaRo;
import org.springblade.online.wrapper.OutLinkRelaWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 动态表单环节关联 控制器
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
@RestController
@AllArgsConstructor
@RequestMapping("/out_link_rela")
@Api(value = "动态表单环节关联", tags = "动态表单环节关联接口")
public class OutLinkRelaController extends BladeController {

	private final IOutLinkRelaService outLinkRelaService;

	/**
	 * 动态表单环节关联 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入OutLinkRela")
	public R<OutLinkRelaVO> detail(@RequestParam Long id) {
		OutLinkRelaEntity detail = outLinkRelaService.getById(id);
		return R.data(OutLinkRelaWrapper.entityVO(detail));
	}

	/**
	 * 动态表单字段分类 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入OutLinkRela")
	public R<IPage<OutLinkRelaVO>> list(@RequestBody OutLinkRelaRo.List body) {
		IPage<OutLinkRelaEntity> pages = outLinkRelaService.page(Condition.getPage(body),
				Condition.getQueryWrapper(OutLinkRelaWrapper.entity(body)));
		return R.data(OutLinkRelaWrapper.pageVO(pages));
	}

//	/**
//	 * 动态表单环节关联 自定义分页
//	 */
//	@PostMapping("/list")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入OutLinkRela")
//	public R<IPage<OutLinkRelaVO>> list(@RequestBody OutLinkRelaRo.List body) {
//		IPage<OutLinkRelaVO> pages = outLinkRelaService.selectOutLinkRelaPage(Condition.getPage(body), body);
//		return R.data(pages);
//	}

	/**
	 * 动态表单环节关联 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入OutLinkRela")
	public R save(@Valid @RequestBody OutLinkRelaRo.Submit body) {
		return R.status(outLinkRelaService.save(OutLinkRelaWrapper.entity(body)));
	}

	/**
	 * 动态表单环节关联 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入OutLinkRela")
	public R update(@Valid @RequestBody OutLinkRelaRo.Submit body) {
		return R.status(outLinkRelaService.updateById(OutLinkRelaWrapper.entity(body)));
	}

	/**
	 * 动态表单环节关联 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入OutLinkRela")
	public R submit(@Valid @RequestBody OutLinkRelaRo.Submit body) {
		return R.status(outLinkRelaService.saveOrUpdate(OutLinkRelaWrapper.entity(body)));
	}

	/**
	 * 动态表单环节关联 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(outLinkRelaService.deleteLogic(Func.toLongList(ids)));
	}

}
