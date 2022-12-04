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
import org.springblade.online.entity.OutFieldEntity;
import org.springblade.online.service.IOutFieldService;
import org.springblade.online.vo.OutFieldVO;
import org.springblade.online.ro.OutFieldRo;
import org.springblade.online.wrapper.OutFieldWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 动态表单字段库 控制器
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
@RestController
@AllArgsConstructor
@RequestMapping("/out_field")
@Api(value = "动态表单字段库", tags = "动态表单字段库接口")
public class OutFieldController extends BladeController {

	private final IOutFieldService outFieldService;

	/**
	 * 动态表单字段库 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入OutField")
	public R<OutFieldVO> detail(@RequestParam Long id) {
		OutFieldEntity detail = outFieldService.getById(id);
		return R.data(OutFieldWrapper.entityVO(detail));
	}

	/**
	 * 动态表单字段分类 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入OutField")
	public R<IPage<OutFieldVO>> list(@RequestBody OutFieldRo.List body) {
		IPage<OutFieldEntity> pages = outFieldService.page(Condition.getPage(body),
				Condition.getQueryWrapper(OutFieldWrapper.entity(body)));
		return R.data(OutFieldWrapper.pageVO(pages));
	}

//	/**
//	 * 动态表单字段库 自定义分页
//	 */
//	@PostMapping("/list")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入OutField")
//	public R<IPage<OutFieldVO>> list(@RequestBody OutFieldRo.List body) {
//		IPage<OutFieldVO> pages = outFieldService.selectOutFieldPage(Condition.getPage(body), body);
//		return R.data(pages);
//	}

	/**
	 * 动态表单字段库 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入OutField")
	public R save(@Valid @RequestBody OutFieldRo.Submit body) {
		return R.status(outFieldService.save(OutFieldWrapper.entity(body)));
	}

	/**
	 * 动态表单字段库 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入OutField")
	public R update(@Valid @RequestBody OutFieldRo.Submit body) {
		return R.status(outFieldService.updateById(OutFieldWrapper.entity(body)));
	}

	/**
	 * 动态表单字段库 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入OutField")
	public R submit(@Valid @RequestBody OutFieldRo.Submit body) {
		return R.status(outFieldService.saveOrUpdate(OutFieldWrapper.entity(body)));
	}

	/**
	 * 动态表单字段库 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(outFieldService.deleteLogic(Func.toLongList(ids)));
	}

}
