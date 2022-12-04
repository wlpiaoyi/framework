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
import __package__.entity.__class_name__Entity;
import __package__.service.I__class_name__Service;
import __package__.vo.__class_name__VO;
import __package__.ro.__class_name__Ro;
import __package__.wrapper.__class_name__Wrapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * __table_comment__ 控制器
 *
 * @author __author__
 * @since __create_time__
 */
@RestController
@AllArgsConstructor
@RequestMapping("/__object_name__")
@Api(value = "__table_comment__", tags = "__table_comment__接口")
public class __class_name__Controller extends BladeController {

	private final I__class_name__Service __class_var_name__Service;

	/**
	 * __table_comment__ 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入__class_name__")
	public R<__class_name__VO> detail(@RequestParam Long id) {
		__class_name__Entity detail = __class_var_name__Service.getById(id);
		return R.data(__class_name__Wrapper.entityVO(detail));
	}

	/**
	 * 动态表单字段分类 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入__class_name__")
	public R<IPage<__class_name__VO>> list(@RequestBody __class_name__Ro.List body) {
		IPage<__class_name__Entity> pages = __class_var_name__Service.page(Condition.getPage(body),
				Condition.getQueryWrapper(__class_name__Wrapper.entity(body)));
		return R.data(__class_name__Wrapper.pageVO(pages));
	}

//	/**
//	 * __table_comment__ 自定义分页
//	 */
//	@PostMapping("/list")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入__class_name__")
//	public R<IPage<__class_name__VO>> list(@RequestBody __class_name__Ro.List body) {
//		IPage<__class_name__VO> pages = __class_var_name__Service.select__class_name__Page(Condition.getPage(body), body);
//		return R.data(pages);
//	}

	/**
	 * __table_comment__ 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入__class_name__")
	public R save(@Valid @RequestBody __class_name__Ro.Submit body) {
		return R.status(__class_var_name__Service.save(__class_name__Wrapper.entity(body)));
	}

	/**
	 * __table_comment__ 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入__class_name__")
	public R update(@Valid @RequestBody __class_name__Ro.Submit body) {
		return R.status(__class_var_name__Service.updateById(__class_name__Wrapper.entity(body)));
	}

	/**
	 * __table_comment__ 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入__class_name__")
	public R submit(@Valid @RequestBody __class_name__Ro.Submit body) {
		return R.status(__class_var_name__Service.saveOrUpdate(__class_name__Wrapper.entity(body)));
	}

	/**
	 * __table_comment__ 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(__class_var_name__Service.deleteLogic(Func.toLongList(ids)));
	}

}
