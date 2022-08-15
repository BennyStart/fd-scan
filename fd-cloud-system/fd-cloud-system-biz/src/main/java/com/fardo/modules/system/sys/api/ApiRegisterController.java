package com.fardo.modules.system.sys.api;

import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.sys.service.ApiRegisterService;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.system.sys.vo.RegisterUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @(#)ApiRegisterController <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/17 13:48
 * 描　述：
 */
@Slf4j
@Api(tags = "api-注册接口")
@RestController
@RequestMapping("/api/system")
public class ApiRegisterController {

    @Autowired
    private ApiRegisterService apiRegisterService;

    @RequestAop(value = "用户注册", clazz = RegisterUserVo.class)
    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping(value = "/register")
    public ResultVo<?> register(ParamVo<RegisterUserVo> paramVo) {
        return apiRegisterService.register(paramVo);
    }

}
