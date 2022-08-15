package com.fardo.modules.shiro.authc;

import com.alibaba.fastjson.JSONObject;
import com.fardo.common.system.util.JwtUtil;
import com.fardo.common.system.vo.LoginUser;
import com.fardo.common.util.SpringContextUtils;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.system.sys.service.ApiSessionService;
import com.fardo.modules.system.sys.vo.DepartVo;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;

public class LoginUtil {

    /**
     * 获取登录用户
     * @return
     */
    public static LoginUserVo getLoginUser() {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        if(request==null){
            return null;
        }
        String sid = request.getParameter("sid");
        LoginUserVo userVo;
        if(StringUtil.isNotEmpty(sid)) {
            userVo = getUserBySid(sid);
            if(userVo != null) {
                return userVo;
            }
        }
        LoginUser sysUser = JwtUtil.getLoginUser();
        if(sysUser == null) {
            return null;
        }
        userVo = new LoginUserVo();
        userVo.setId(sysUser.getId());
        userVo.setUsername(sysUser.getUsername());
        userVo.setRealname(sysUser.getRealname());
        userVo.setIdcard(sysUser.getUserIdentity()+"");
        userVo.setSex(sysUser.getSex()+"");
        userVo.setMobilePhone(sysUser.getPhone());
        userVo.setEmail(sysUser.getEmail());
        DepartVo departVo = new DepartVo();
        departVo.setDepartCode(sysUser.getOrgCode());
        departVo.setId(sysUser.getLoginDepartId());
        return userVo;
    }


    public static LoginUserVo getUserBySid(String sid) {
        if(StringUtil.isEmpty(sid)) {
            return null;
        }
        ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
        if(applicationContext == null) {
            return null;
        }
        ApiSessionService apiSessionService = applicationContext.getBean(ApiSessionService.class);
        String loginUserStr = apiSessionService.getLoginUser(sid);
        if(StringUtil.isEmpty(loginUserStr)) {
            return null;
        }
        LoginUserVo userVo = JSONObject.parseObject(loginUserStr, LoginUserVo.class);
        return userVo;
    }


}
