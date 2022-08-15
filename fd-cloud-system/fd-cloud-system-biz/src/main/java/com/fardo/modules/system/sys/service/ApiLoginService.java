package com.fardo.modules.system.sys.service;

import com.fardo.common.api.vo.ResultVo;
import com.fardo.modules.system.sys.vo.LoginPwdVo;
import com.fardo.modules.system.sys.vo.LoginResult;
import com.fardo.modules.system.sys.vo.LoginSsoVo;
import com.fardo.modules.system.sys.vo.ParamVo;

import java.util.HashMap;
import java.util.Map;

public interface ApiLoginService {

    Map<String,Object> CacheAllUserInfoMap = new HashMap<>();
    String KEY_OF_LAST_UPDATE = "lastUpdate";
    String KEY_OF_ALL_USER_KEY = "ALL_USER_LIST";
    String KEY_OF_ALL_USER_MD5_KEY= "ALL_USER_LIST_MD5";

    ResultVo<LoginResult> checkLogin(ParamVo<LoginPwdVo> paramVo);

    /**
     * 切换当前用户角色
     * @param userDepartRolesId
     */
    LoginResult checkRole(String userDepartRolesId);

    LoginResult loginSSO(ParamVo<LoginSsoVo> paramVo);

}
