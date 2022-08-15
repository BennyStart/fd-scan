package com.fardo.modules.system.role.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.modules.system.role.model.SysRoleModel;
import com.fardo.modules.system.role.model.SysUserRolesModel;
import com.fardo.modules.system.role.service.ISysRoleService;
import com.fardo.modules.system.role.vo.SysRoleIdParamVo;
import com.fardo.modules.system.role.vo.SysRoleParamVo;
import com.fardo.modules.system.role.vo.SysRoleUserVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.system.user.service.ISysUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @(#)ApiRoleController <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/12 13:33
 * 描　述：
 */
@Api(tags = "api-角色管理接口")
@RestController
@RequestMapping("/api/system/role")
public class ApiRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @RequestAop(value = "分页获取角色列表", clazz = SysRoleParamVo.class)
    @ApiOperation("分页获取角色列表")
    @PostMapping(value = "/list")
    public ResultVo<IPage<SysRoleEntity>> list(ParamVo<SysRoleParamVo> paramVo) {
        ResultVo<IPage<SysRoleEntity>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        IPage<SysRoleEntity> pageList = sysRoleService.queryList(paramVo.getData());
        resultVo.setResults(pageList);
        return resultVo;
    }

    @RequestAop(value = "添加角色", clazz = SysRoleModel.class)
    @ApiOperation("添加角色")
    @PostMapping(value = "/add")
    public ResultVo<SysRoleIdParamVo> add(ParamVo<SysRoleModel> paramVo) {
        ResultVo<SysRoleIdParamVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysRoleModel sysRoleModel = sysRoleService.saveRoleAuth(paramVo.getData());
        SysRoleIdParamVo sysRoleIdParamVo = new SysRoleIdParamVo();
        sysRoleIdParamVo.setId(sysRoleModel.getId());
        resultVo.setResults(sysRoleIdParamVo);
        return resultVo;
    }

    @RequestAop(value = "修改角色", clazz = SysRoleModel.class)
    @ApiOperation("修改角色")
    @PostMapping(value = "/edit")
    public ResultVo edit(ParamVo<SysRoleModel> paramVo) {
        sysRoleService.updateRoleAuth(paramVo.getData());
        return ResultVo.getResultVo(ResultCode.SUCCESS);
    }

    @RequestAop(value = "查看角色", clazz = SysRoleIdParamVo.class)
    @ApiOperation("查看角色")
    @PostMapping(value = "/get")
    public ResultVo<SysRoleModel> get(ParamVo<SysRoleIdParamVo> paramVo) {
        ResultVo<SysRoleModel> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysRoleModel sysRoleModel = sysRoleService.getRoleDetail(paramVo.getData().getId());
        resultVo.setResults(sysRoleModel);
        return resultVo;
    }

    @RequestAop(value = "删除角色", clazz = SysRoleIdParamVo.class)
    @ApiOperation("删除角色")
    @PostMapping(value = "/delete")
    public ResultVo delete(ParamVo<SysRoleIdParamVo> paramVo) {
        sysRoleService.deleteRole(paramVo.getData().getId());
        return ResultVo.getResultVo(ResultCode.SUCCESS);
    }

    @RequestAop(value = "绑定角色用户关系", clazz = SysRoleUserVo.class)
    @ApiOperation("绑定角色用户关系")
    @PostMapping(value = "/addUser")
    public ResultVo addUser(ParamVo<SysRoleUserVo> paramVo) {
        sysUserRoleService.saveRoleUsers(paramVo.getData().getRoleId(),paramVo.getData().getUserIds());
        return ResultVo.getResultVo(ResultCode.SUCCESS);
    }

    @RequestAop(value = "删除角色用户关系", clazz = SysRoleUserVo.class)
    @ApiOperation("删除角色用户关系")
    @PostMapping(value = "/deleteUser")
    public ResultVo deleteUser(ParamVo<SysRoleUserVo> paramVo) {
        sysUserRoleService.deleteRoleUsers(paramVo.getData().getRoleId(),paramVo.getData().getUserIds());
        return ResultVo.getResultVo(ResultCode.SUCCESS);
    }

    @RequestAop(value = "获取当前登录用户角色信息")
    @ApiOperation("获取当前登录用户角色信息")
    @PostMapping(value = "/getRolesForUser")
    public ResultVo<List<SysUserRolesModel>> getUserRoles(ParamVo paramVo) {
        ResultVo<List<SysUserRolesModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysUserRolesModel> list = sysUserRoleService.getRolesForLoginUser();
        resultVo.setResults(list);
        return resultVo;
    }
}
