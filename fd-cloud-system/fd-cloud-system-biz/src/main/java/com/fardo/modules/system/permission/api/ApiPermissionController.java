package com.fardo.modules.system.permission.api;

import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.permission.model.SysPermissionTreeModel;
import com.fardo.modules.system.permission.service.ISysPermissionService;
import com.fardo.modules.system.permission.vo.PermissionIdsVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @(#)ApiPermissionController <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/12 9:18
 * 描　述：
 */
@Slf4j
@Api(tags = "api-菜单管理接口")
@RestController
@RequestMapping("/api/system/permission")
public class ApiPermissionController {

    @Autowired
    private ISysPermissionService sysPermissionService;

    @RequestAop(value = "获取全部菜单数据")
    @ApiOperation("获取全部菜单数据")
    @PostMapping(value = "/listAll")
    public ResultVo<List<SysPermissionTreeModel>> listAll(ParamVo paramVo) {
        ResultVo<List<SysPermissionTreeModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysPermissionTreeModel> treeList = sysPermissionService.listAll(true);
        resultVo.setResults(treeList);
        return resultVo;
    }

    @RequestAop(value = "保存启用菜单", clazz = PermissionIdsVo.class)
    @ApiOperation("保存启用菜单")
    @PostMapping(value = "/save")
    public ResultVo save(ParamVo<PermissionIdsVo> paramVo) {
        ResultVo resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        sysPermissionService.savePermissionEnable(paramVo.getData().getPermissionIds());
        return resultVo;
    }

    @RequestAop(value = "获取启用菜单数据")
    @ApiOperation("获取启用菜单数据")
    @PostMapping(value = "/list")
    public ResultVo<List<SysPermissionTreeModel>> list(ParamVo paramVo) {
        ResultVo<List<SysPermissionTreeModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysPermissionTreeModel> treeList = sysPermissionService.listAll(false);
        resultVo.setResults(treeList);
        return resultVo;
    }

    @RequestAop(value = "获取用户菜单数据")
    @ApiOperation("获取用户菜单数据")
    @PostMapping(value = "/listForUser")
    public ResultVo<List<SysPermissionTreeModel>> listForUser(ParamVo paramVo) {
        ResultVo<List<SysPermissionTreeModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysPermissionTreeModel> treeList = sysPermissionService.listForUser();
        resultVo.setResults(treeList);
        return resultVo;
    }
}
