package com.fardo.modules.system.user.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.api.SysBaseRemoteApi;
import com.fardo.modules.system.depart.vo.SysDepartIdParamVo;
import com.fardo.modules.system.role.model.SysDataPermissionModel;
import com.fardo.modules.system.role.vo.SysUserRolesIdVo;
import com.fardo.modules.system.sys.service.ApiLoginService;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.system.user.model.SysDepartUserModel;
import com.fardo.modules.system.user.model.SysUserModel;
import com.fardo.modules.system.user.service.ISysUnitUserService;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.system.user.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "api-单位管理员接口")
@RestController
@RequestMapping("/api/system/unit/user")
public class ApiUnitUserController {

    @Autowired
    private ISysUnitUserService sysUnitUserService;

    @RequestAop(value = "单位管理员-根据机构id获取用户及下级机构", clazz = SysDepartIdParamVo.class)
    @ApiOperation("单位管理员-根据机构id获取用户及下级机构")
    @PostMapping(value = "/departUserList")
    public ResultVo<List<SysDepartUserModel>> departUserList(ParamVo<SysDepartIdParamVo> paramVo) {
        ResultVo<List<SysDepartUserModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysDepartUserModel> list = sysUnitUserService.getDepartUserList(paramVo.currentLoginUser().getId(),paramVo.getData().getId());
        resultVo.setResults(list);
        return resultVo;
    }

    @RequestAop(value = "单位管理员-分页获取用户列表", clazz = SysUserParamVo.class)
    @ApiOperation("单位管理员-分页获取用户列表")
    @PostMapping(value = "/page")
    public ResultVo<IPage<SysUserModel>> page(ParamVo<SysUserParamVo> paramVo) {
        ResultVo<IPage<SysUserModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        IPage<SysUserModel> modelIPage = sysUnitUserService.getPageModelList(paramVo.currentLoginUser().getId(),paramVo.getData());
        resultVo.setResults(modelIPage);
        return resultVo;
    }

    @RequestAop(value = "单位管理员-查看用户信息", clazz = SysUserIdParamVo.class)
    @ApiOperation("单位管理员-查看用户信息")
    @PostMapping(value = "/get")
    public ResultVo<SysUserVo> get(ParamVo<SysUserIdParamVo> paramVo) {
        ResultVo<SysUserVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysUserVo sysUserVo = sysUnitUserService.getUserDetail(paramVo.getData().getId());
        resultVo.setResults(sysUserVo);
        return resultVo;
    }

    @RequestAop(value = "单位管理员-新增用户", clazz = SysUserVo.class)
    @ApiOperation("单位管理员-新增用户")
    @PostMapping(value = "/add")
    public ResultVo<SysUserIdParamVo> add(ParamVo<SysUserVo> paramVo) {
        SysUserVo sysUserVo = paramVo.getData();
        sysUnitUserService.saveUser(sysUserVo);
        ResultVo<SysUserIdParamVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysUserIdParamVo sysUserIdParamVo = new SysUserIdParamVo();
        sysUserIdParamVo.setId(sysUserVo.getId());
        resultVo.setResults(sysUserIdParamVo);
        return resultVo;
    }

    @RequestAop(value = "单位管理员-修改用户", clazz = SysUserUpdateVo.class)
    @ApiOperation("单位管理员-修改用户")
    @PostMapping(value = "/edit")
    public ResultVo<SysUserIdParamVo> edit(ParamVo<SysUserUpdateVo> paramVo) {
        SysUserUpdateVo sysUserVo = paramVo.getData();
        sysUnitUserService.updateUser(sysUserVo);
        ResultVo<SysUserIdParamVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysUserIdParamVo sysUserIdParamVo = new SysUserIdParamVo();
        sysUserIdParamVo.setId(sysUserVo.getId());
        resultVo.setResults(sysUserIdParamVo);
        return resultVo;
    }

    @RequestAop(value = "单位管理员-删除用户", clazz = SysUserIdParamVo.class)
    @ApiOperation("单位管理员-删除用户")
    @PostMapping(value = "/delete")
    public ResultVo<?> delete(ParamVo<SysUserIdParamVo> paramVo) {
        this.sysUnitUserService.logicDeleteUser(paramVo.getData().getId());
        return ResultVo.getResultVo(ResultCode.SUCCESS);
    }

    @RequestAop(value = "单位管理员-获取当前单位下的用户", clazz = SysUserLikeVo.class)
    @ApiOperation(value = "单位管理员-获取当前单位下的用户", notes = "单位管理员-获取当前登录单位下的用户，当前登录用户不查询出来")
    @PostMapping(value = "/queryUserForDepart")
    public ResultVo<List<SysUserModel>> queryUserForDepart(ParamVo<SysUserLikeVo> paramVo) {
        ResultVo<List<SysUserModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysUserModel> list = sysUnitUserService.queryUserForDepartNoCurrentUser(paramVo.getData());
        resultVo.setResults(list);
        return resultVo;
    }

}
