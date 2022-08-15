package com.fardo.modules.system.user.api;

import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.system.user.model.UserFunctionModel;
import com.fardo.modules.system.user.service.ISysUserPermissionService;
import com.fardo.modules.system.user.vo.UserFunctionSaveVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @(#)ApiUserConfigController <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/17 13:47
 * 描　述：
 */
@Slf4j
@Api(tags = "api-个人设置中心")
@RestController
@RequestMapping("/api/system/userConfig")
public class ApiUserConfigController {

    @Autowired
    private ISysUserPermissionService userPermissionService;

    @RequestAop(value = "获取全部功能和常见功能和业务功能")
    @ApiOperation("获取全部功能和常见功能和业务功能")
    @PostMapping(value = "/getFunction")
    public ResultVo<UserFunctionModel> getFunction(ParamVo paramVo) {
        ResultVo<UserFunctionModel> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        UserFunctionModel model = userPermissionService.getUserFunction();
        resultVo.setResults(model);
        return resultVo;
    }

    @RequestAop(value = "保存常见功能和业务功能设置", clazz = UserFunctionSaveVo.class)
    @ApiOperation("保存常见功能和业务功能设置")
    @PostMapping(value = "/saveFunction")
    public ResultVo saveFunction(ParamVo<UserFunctionSaveVo> paramVo) {
        ResultVo resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        userPermissionService.saveUserFunction(paramVo.getData());
        return resultVo;
    }

}
