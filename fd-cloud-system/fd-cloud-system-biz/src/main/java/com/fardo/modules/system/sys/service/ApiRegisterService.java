package com.fardo.modules.system.sys.service;

import com.fardo.common.api.vo.ResultVo;
import com.fardo.modules.system.sys.vo.LoginPwdVo;
import com.fardo.modules.system.sys.vo.LoginResult;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.system.sys.vo.RegisterUserVo;

/**
 * @(#)ApiRegisterService <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/17 14:03
 * 描　述：
 */
public interface ApiRegisterService {

    /**
     * 用户注册
     * @param paramVo
     * @return
     */
    ResultVo<?> register(ParamVo<RegisterUserVo> paramVo);
}
