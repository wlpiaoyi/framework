package __package__.controller;

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
import __package__.entity.__className__Entity;
import __package__.service.I__className__Service;
import __package__.vo.__className__VO;
import __package__.ro.__className__Ro;
import __package__.wrapper.__className__Wrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * __tableComment__ 控制器
 *
 * @author __author__
 * @since __createTime__
 */
@RestController
@AllArgsConstructor
@RequestMapping("/__suffixName__")
@Api(value = "__tableComment__", tags = "__tableComment__接口")
public class __className__Controller extends BladeController {

	private final I__className__Service __pClassName__Service;

	/**
	 * __tableComment__ 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入__className__")
	public R<__className__VO> detail(@RequestParam Long id) {
		__className__Entity detail = __pClassName__Service.getById(id);
		return R.data(__className__Wrapper.entityVO(detail));
	}

	/**
	 * 动态表单字段分类 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入__className__")
	public R<IPage<__className__VO>> list(@RequestBody __className__Ro.List body) {
		IPage<__className__Entity> pages = __pClassName__Service.page(Condition.getPage(body),
				Condition.getQueryWrapper(__className__Wrapper.entity(body)));
		return R.data(__className__Wrapper.pageVO(pages));
	}

//	/**
//	 * __tableComment__ 自定义分页
//	 */
//	@PostMapping("/list")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入__className__")
//	public R<IPage<__className__VO>> list(@RequestBody __className__Ro.List body) {
//		IPage<__className__VO> pages = __pClassName__Service.select__className__Page(Condition.getPage(body), body);
//		return R.data(pages);
//	}

	/**
	 * __tableComment__ 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入__className__")
	public R save(@Valid @RequestBody __className__Ro.Submit body) {
		return R.status(__pClassName__Service.save(__className__Wrapper.entity(body)));
	}

	/**
	 * __tableComment__ 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入__className__")
	public R update(@Valid @RequestBody __className__Ro.Submit body) {
		return R.status(__pClassName__Service.updateById(__className__Wrapper.entity(body)));
	}

	/**
	 * __tableComment__ 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入__className__")
	public R submit(@Valid @RequestBody __className__Ro.Submit body) {
		return R.status(__pClassName__Service.saveOrUpdate(__className__Wrapper.entity(body)));
	}

	/**
	 * __tableComment__ 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(__pClassName__Service.deleteLogic(Func.toLongList(ids)));
	}

}
