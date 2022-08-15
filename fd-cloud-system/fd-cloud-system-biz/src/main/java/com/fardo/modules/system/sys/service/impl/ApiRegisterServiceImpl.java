package com.fardo.modules.system.sys.service.impl;

import com.fardo.common.api.vo.ResultVo;
import com.fardo.modules.system.sys.service.ApiRegisterService;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.system.sys.vo.RegisterUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @(#)ApiRegisterServiceImpl <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/17 14:04
 * 描　述：
 */
@Slf4j
@Service
public class ApiRegisterServiceImpl implements ApiRegisterService {

    @Override
    public ResultVo<?> register(ParamVo<RegisterUserVo> paramVo) {
        return null;
    }
}
