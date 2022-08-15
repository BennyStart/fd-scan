package com.fardo.modules.system.sys.api;

import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.common.enums.OperTypeEnum;
import com.fardo.common.util.DateUtils;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.log.service.ISysUserLogService;
import com.fardo.modules.system.sync.service.ITzfwDataSyncService;
import com.fardo.modules.system.sys.service.ApiLoginService;
import com.fardo.modules.system.sys.service.ApiSessionService;
import com.fardo.modules.system.sys.vo.LoginPwdVo;
import com.fardo.modules.system.sys.vo.LoginResult;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录接口
 */
@Slf4j
@Api(tags = "api-登录接口")
@RestController
@RequestMapping("/api/system")
public class ApiLoginController {

    @Autowired
    private ApiLoginService apiLoginService;
    @Autowired
    private ApiSessionService apiSessionService;
    @Autowired
    private ISysUserLogService sysUserLogService;

    @Autowired
    private ITzfwDataSyncService tzfwDataSyncService;

    @RequestAop(value = "用户机构同步")
    @ApiOperation(value = "用户机构同步", notes = "用户机构同步")
    @PostMapping(value = "/sync")
    public ResultVo sync(ParamVo paramVo) {
        ResultVo resultVo = new ResultVo();
        tzfwDataSyncService.syncDepartAndUserData();
        resultVo.setResultMsg("用户机构同步完成,同步详情请查看system相关日志！");
        resultVo.setResultCode(ResultCode.SUCCESS);
        return resultVo;
    }

    @RequestAop(value = "用户名密码登录", clazz = LoginPwdVo.class, operType = OperTypeEnum.YHDL, removeParamField = "password")
    @ApiOperation(value = "用户名密码登录", notes = "用户名密码登录")
    @PostMapping(value = "/loginPwd")
    public ResultVo<LoginResult> loginPwd(ParamVo<LoginPwdVo> paramVo, HttpServletRequest request) {
        ResultVo<LoginResult> resultVo = apiLoginService.checkLogin(paramVo);
        request.setAttribute(SysConstants.LOG_OPER_DESC, "用户密码登录，登录版本号" + paramVo.getClientVersion());
        request.setAttribute(SysConstants.LOG_RESULT_DATA, "用户密码登录，登录版本号" + paramVo.getClientVersion());
        return resultVo;
    }

    @RequestAop(value = "注销登录")
    @ApiOperation(value = "注销登录", notes = "注销登录")
    @PostMapping(value = "/logout")
    public ResultVo logout(ParamVo paramVo) {
        ResultVo rsVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        LoginUserVo userVo = LoginUtil.getLoginUser();
        String operDesc = userVo.getLoginType()+"，登录版本号"+paramVo.getClientVersion()+"，"+ DateUtils.now()+ SysConstants.LOGOUT_TYPE_MANUAL;
        this.sysUserLogService.saveLog(paramVo, OperTypeEnum.YHDC, operDesc, "/api/system/logout");
        if(StringUtils.isNotEmpty(paramVo.getSid())) {
            apiSessionService.removeSession(paramVo.getSid());
        }
        return rsVo;
    }

}
