package com.fardo.modules.system.config.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fardo.common.api.vo.Result;
import com.fardo.common.constant.CacheConstant;
import com.fardo.common.system.query.QueryGenerator;
import com.fardo.common.util.DateUtils;
import com.fardo.modules.system.config.entity.SysDictItemEntity;
import com.fardo.modules.system.config.service.ISysDictItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@RestController
@RequestMapping("/sys/dictItem")
@Slf4j
@Api(tags="数据字典")
public class SysDictItemController {

	@Autowired
	private ISysDictItemService sysDictItemService;
	
	/**
	 * @功能：查询字典数据
	 * @param sysDictItemEntity
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysDictItemEntity>> queryPageList(SysDictItemEntity sysDictItemEntity, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                          @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
		Result<IPage<SysDictItemEntity>> result = new Result<IPage<SysDictItemEntity>>();
		QueryWrapper<SysDictItemEntity> queryWrapper = QueryGenerator.initQueryWrapper(sysDictItemEntity, req.getParameterMap());
		queryWrapper.orderByAsc("sort_order");
		Page<SysDictItemEntity> page = new Page<SysDictItemEntity>(pageNo, pageSize);
		IPage<SysDictItemEntity> pageList = sysDictItemService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 * @功能：新增
	 * @return
	 */
    @ApiOperation(value="数据字典类型新增", notes="数据字典类型新增")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@CacheEvict(value= CacheConstant.SYS_DICT_CACHE, allEntries=true)
	public Result<SysDictItemEntity> add(@RequestBody SysDictItemEntity sysDictItemEntity) {
		Result<SysDictItemEntity> result = new Result<SysDictItemEntity>();
		try {
			sysDictItemEntity.setCreateTime(DateUtils.getCurrentTime());
			sysDictItemService.save(sysDictItemEntity);
			result.success("保存成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	 * @功能：编辑
	 * @param sysDictItemEntity
	 * @return
	 */
    @ApiOperation(value="数据字典类型修改", notes="数据字典类型修改")
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@CacheEvict(value=CacheConstant.SYS_DICT_CACHE, allEntries=true)
	public Result<SysDictItemEntity> edit(@RequestBody SysDictItemEntity sysDictItemEntity) {
		Result<SysDictItemEntity> result = new Result<SysDictItemEntity>();
		SysDictItemEntity sysdict = sysDictItemService.getById(sysDictItemEntity.getId());
		if(sysdict==null) {
			result.error500("未找到对应实体");
		}else {
			sysDictItemEntity.setUpdateTime(DateUtils.getCurrentTime());
			boolean ok = sysDictItemService.updateById(sysDictItemEntity);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("编辑成功!");
			}
		}
		return result;
	}
	
	/**
	 * @功能：删除字典数据
	 * @param id
	 * @return
	 */
    @ApiOperation(value="数据字典类型删除", notes="数据字典类型删除")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@CacheEvict(value=CacheConstant.SYS_DICT_CACHE, allEntries=true)
	public Result<SysDictItemEntity> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysDictItemEntity> result = new Result<SysDictItemEntity>();
		SysDictItemEntity joinSystem = sysDictItemService.getById(id);
		if(joinSystem==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = sysDictItemService.removeById(id);
			if(ok) {
				result.success("删除成功!");
			}
		}
		return result;
	}
	
	/**
	 * @功能：批量删除字典数据
	 * @param ids
	 * @return
	 */
    @ApiOperation(value="数据字典类型批量新增", notes="数据字典类型批量新增")
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	@CacheEvict(value=CacheConstant.SYS_DICT_CACHE, allEntries=true)
	public Result<SysDictItemEntity> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysDictItemEntity> result = new Result<SysDictItemEntity>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.sysDictItemService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
}
