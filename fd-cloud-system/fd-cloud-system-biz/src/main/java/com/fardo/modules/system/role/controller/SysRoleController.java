package com.fardo.modules.system.role.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.Result;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.system.query.QueryGenerator;
import com.fardo.common.system.vo.LoginUser;
import com.fardo.common.util.oConvertUtils;
import com.fardo.modules.system.base.model.TreeModel;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import com.fardo.modules.system.permission.service.ISysPermissionService;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.modules.system.role.entity.SysRolePermissionEntity;
import com.fardo.modules.system.role.model.SysRoleModel;
import com.fardo.modules.system.role.service.ISysRolePermissionService;
import com.fardo.modules.system.role.service.ISysRoleService;
import com.fardo.modules.system.role.vo.SysRoleParamVo;
import com.fardo.modules.system.role.vo.SysRoleUserVo;
import com.fardo.modules.system.role.vo.SysRoleVo;
import com.fardo.modules.system.user.service.ISysUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/sys/role")
@Slf4j
public class SysRoleController {
	@Autowired
	private ISysRoleService sysRoleService;
	
	@Autowired
	private ISysRolePermissionService sysRolePermissionService;
	
	@Autowired
	private ISysPermissionService sysPermissionService;

	@Autowired
	private ISysUserRoleService sysUserRoleService;

	@ApiOperation("分页获取角色列表")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysRoleEntity>> queryPageList(SysRoleParamVo roleVo) {
		Result<IPage<SysRoleEntity>> result = new Result<IPage<SysRoleEntity>>();
		IPage<SysRoleEntity> pageList = sysRoleService.queryList(roleVo);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	@ApiOperation("添加角色")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<?> add(@Valid @RequestBody SysRoleVo roleVo) {
		Result<SysRoleEntity> result = new Result<SysRoleEntity>();
		SysRoleEntity role = new SysRoleEntity();
		BeanUtils.copyProperties(roleVo,role);
		sysRoleService.save(role);
		result.success("添加成功！");
		return result;
	}

	@ApiOperation("修改角色")
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public Result<?> update(@Valid @RequestBody SysRoleModel sysRoleModel) {
		Result<SysRoleEntity> result = new Result<SysRoleEntity>();
		SysRoleEntity sysrole = sysRoleService.getById(sysRoleModel.getId());
		if(sysrole==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = sysRoleService.updateRoleAuth(sysRoleModel);
			if(ok) {
				result.success("修改成功!");
			}else{
				result.error500("修改失败");
			}
		}
		return result;
	}

	@ApiOperation("查看角色")
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Result<SysRoleModel> get(@ApiParam(value = "角色id") @RequestParam(name="id") String id) {
		Result<SysRoleModel> result = new Result<>();
		SysRoleModel sysRoleModel = sysRoleService.getRoleDetail(id);
		if(sysRoleModel == null) {
			result.error500("未找到对应实体");
		}
		result.setResult(sysRoleModel);
		result.setSuccess(true);
		return result;
	}

	@ApiOperation("删除角色")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<?> delete(@ApiParam(value = "角色id") @RequestParam(name="id") String id) {
		sysRoleService.deleteRole(id);
		return Result.ok("删除角色成功");
	}

	@ApiOperation("添加用户")
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public Result<?> addUser(@Valid @RequestBody SysRoleUserVo sysRoleUserVo) {
		Result<SysRoleEntity> result = new Result<SysRoleEntity>();
		sysUserRoleService.saveRoleUsers(sysRoleUserVo.getRoleId(),sysRoleUserVo.getUserIds());
		result.success("添加成功！");
		return result;
	}

	@ApiOperation("删除用户")
	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public Result<?> deleteUser(@Valid @RequestBody SysRoleUserVo sysRoleUserVo) {
		Result<SysRoleEntity> result = new Result<SysRoleEntity>();
		sysUserRoleService.deleteRoleUsers(sysRoleUserVo.getRoleId(),sysRoleUserVo.getUserIds());
		result.success("删除成功！");
		return result;
	}

	@ApiOperation("数据权限设置详情")
	@RequestMapping(value = "/dataAuthDetail", method = RequestMethod.GET)
	public Result<SysRoleModel> dataAuthDetail(@ApiParam(value = "角色id") @RequestParam(name="id") String id) {
		Result<SysRoleModel> result = new Result<>();
		SysRoleModel sysRoleModel = sysRoleService.getRoleDetail(id);
		result.setSuccess(true);
		result.setResult(sysRoleModel);
		return result;
	}

	@ApiOperation("更新数据权限设置")
	@RequestMapping(value = "/dataAuthUpdate", method = RequestMethod.PUT)
	public Result<?> dataAuthUpdate(@Valid @RequestBody SysRoleModel sysRoleModel) {
		sysRoleService.updateRoleAuth(sysRoleModel);
		return Result.ok();
	}
	
}
