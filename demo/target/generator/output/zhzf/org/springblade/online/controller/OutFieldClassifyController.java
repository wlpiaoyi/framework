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
import org.springblade.online.entity.OutFieldClassifyEntity;
import org.springblade.online.service.IOutFieldClassifyService;
import org.springblade.online.vo.OutFieldClassifyVO;
import org.springblade.online.ro.OutFieldClassifyRo;
import org.springblade.online.wrapper.OutFieldClassifyWrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 动态表单字段分类 控制器
 *
 * @author wlpia
 * @since 2022-12-04 19:38:11
 */
@RestController
@AllArgsConstructor
@RequestMapping("/out_field_classify")
@Api(value = "动态表单字段分类", tags = "动态表单字段分类接口")
public class OutFieldClassifyController extends BladeController {

	private final IOutFieldClassifyService outFieldClassifyService;

	/**
	 * 动态表单字段分类 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入OutFieldClassify")
	public R<OutFieldClassifyVO> detail(@RequestParam Long id) {
		OutFieldClassifyEntity detail = outFieldClassifyService.getById(id);
		return R.data(OutFieldClassifyWrapper.entityVO(detail));
	}

	/**
	 * 动态表单字段分类 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入OutFieldClassify")
	public R<IPage<OutFieldClassifyVO>> list(@RequestBody OutFieldClassifyRo.List body) {
		IPage<OutFieldClassifyEntity> pages = outFieldClassifyService.page(Condition.getPage(body),
				Condition.getQueryWrapper(OutFieldClassifyWrapper.entity(body)));
		return R.data(OutFieldClassifyWrapper.pageVO(pages));
	}

//	/**
//	 * 动态表单字段分类 自定义分页
//	 */
//	@PostMapping("/list")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入OutFieldClassify")
//	public R<IPage<OutFieldClassifyVO>> list(@RequestBody OutFieldClassifyRo.List body) {
//		IPage<OutFieldClassifyVO> pages = outFieldClassifyService.selectOutFieldClassifyPage(Condition.getPage(body), body);
//		return R.data(pages);
//	}

	/**
	 * 动态表单字段分类 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入OutFieldClassify")
	public R save(@Valid @RequestBody OutFieldClassifyRo.Submit body) {
		return R.status(outFieldClassifyService.save(OutFieldClassifyWrapper.entity(body)));
	}

	/**
	 * 动态表单字段分类 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入OutFieldClassify")
	public R update(@Valid @RequestBody OutFieldClassifyRo.Submit body) {
		return R.status(outFieldClassifyService.updateById(OutFieldClassifyWrapper.entity(body)));
	}

	/**
	 * 动态表单字段分类 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入OutFieldClassify")
	public R submit(@Valid @RequestBody OutFieldClassifyRo.Submit body) {
		return R.status(outFieldClassifyService.saveOrUpdate(OutFieldClassifyWrapper.entity(body)));
	}

	/**
	 * 动态表单字段分类 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(outFieldClassifyService.deleteLogic(Func.toLongList(ids)));
	}

}
