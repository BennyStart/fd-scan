package com.fardo.modules.system.sys.service.impl;

import cn.hutool.core.util.IdcardUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.exception.ApiException;
import com.fardo.common.exception.ApiServiceException;
import com.fardo.common.util.IpSectionUtil;
import com.fardo.common.util.PasswordUtil;
import com.fardo.common.util.RedisUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.area.service.ISysAreaService;
import com.fardo.modules.system.config.service.ISysConfigService;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.constant.ClassConstant;
import com.fardo.modules.system.constant.ConfigConstants;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.personal.service.ISysPersonalSettingService;
import com.fardo.modules.system.personal.vo.SysPersonalInfoVo;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.modules.system.role.enums.DataScopeEnum;
import com.fardo.modules.system.role.mapper.SysRoleMapper;
import com.fardo.modules.system.role.model.SysCustomDepartModel;
import com.fardo.modules.system.role.model.SysDataPermissionModel;
import com.fardo.modules.system.role.service.ISysRoleService;
import com.fardo.modules.system.security.entity.SysIpWhileEntity;
import com.fardo.modules.system.security.service.ISysIpWhileService;
import com.fardo.modules.system.sys.enums.LoginResultCodeEnum;
import com.fardo.modules.system.sys.service.ApiLoginService;
import com.fardo.modules.system.sys.service.ApiSessionService;
import com.fardo.modules.system.sys.vo.*;
import com.fardo.modules.system.user.entity.SysUserDepartEntity;
import com.fardo.modules.system.user.entity.SysUserDepartRolesEntity;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.mapper.SysUserDepartRolesMapper;
import com.fardo.modules.system.user.service.ISysUserDepartService;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.system.user.vo.UserDepartRoleDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ApiLoginServiceImpl implements ApiLoginService {

    @Autowired
    private ISysPersonalSettingService sysPersonalSettingService;

    /**
     * 单点登录账号类型
     */
    private static final String ID_TYPE_IDCARD = "idcard";
    private static final String ID_TYPE_POLICE_NO = "policeNo";

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private ISysUserDepartService sysUserDepartService;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ApiSessionService apiSessionService;
    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private ISysIpWhileService sysIpWhileService;
    @Resource
    private SysUserDepartRolesMapper userDepartRolesMapper;
    @Autowired
    private ISysAreaService sysAreaService;
    @Resource
    private SysRoleMapper roleMapper;

    @Value("${user.init.password}")
    private String initPassword;
    @Value("${spring.datasource.dynamic.datasource.master.driver-class-name}")
    private String dbDriver;

    /**
     * 登录校验
     * @param paramVo
     * @return
     */
    @Transactional
    @Override
    public ResultVo<LoginResult> checkLogin(ParamVo<LoginPwdVo> paramVo) {
        ResultVo<LoginResult> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        String password = paramVo.getData().getPassword();
        String appKey = paramVo.getApp_key();
        String loginIp = paramVo.getRemoteIp();
        String sid = paramVo.getSid();
        if(StringUtils.isEmpty(sid)) {
            throw new ApiException(ResultCode.SID_ERROR);
        }
        try {
            // 白名单启动判断
            this.validWhileIp(loginIp);
            //判断用户是否有效
            SysUserEntity sysUser = this.checkUser(paramVo.getData());
            if(StringUtil.isEmpty(sysUser.getLoginModes()) || !sysUser.getLoginModes().contains(SysConstants.LOGIN_MODES_PWD)) {
                return ResultVo.getResultVo(LoginResultCodeEnum.LOGIN_PWD_NO_SUPPORT);
            }
            String username = sysUser.getUsername();
            // 校验用户名或密码是否正确
            String userpassword = PasswordUtil.encrypt(username, password, PasswordUtil.getStaticSalt());
            String syspassword = sysUser.getPassword();
            if (!syspassword.equals(userpassword)) {
                // 密码错误，检验重试次数
                this.addPwdLoginFail(username);
                PwdFailVo pwdFailVo = this.getPwdLoginFailTips(username);
                if(StringUtils.isNotEmpty(pwdFailVo.getMsg())) {
                    return ResultVo.getResultVo(LoginResultCodeEnum.PASSWORD_ERROR.getResultCode(), pwdFailVo.getMsg());
                }
                return ResultVo.getResultVo(LoginResultCodeEnum.PASSWORD_ERROR);
            }
            // 判断用户是否已锁住
            PwdFailVo pwdFailVo = this.getPwdLoginFailTips(username);
            if(pwdFailVo.isUserLocked()) {
                return ResultVo.getResultVo(LoginResultCodeEnum.PASSWORD_ERROR.getResultCode(), pwdFailVo.getMsg());
            }
            // 校验单点登录
            if(!this.checkSSO(loginIp, appKey, sysUser)) {
                return ResultVo.getResultVo(LoginResultCodeEnum.LOGIN_FAIL_USERNAM_EXITS_ERROR);
            }
            // 校验密码是不是初始化密码
            this.checkInitPwd(sysUser);
            // 更新用户
            sysUser.setLoginIp(loginIp);
            if(SysConstants.APP_KEY_OF_PC.equals(appKey)){
                sysUser.setPcSid(sid);
            }else if (SysConstants.APP_KEY_OF_MOBILE.equals(appKey)){
                sysUser.setMobileSid(sid);
            }
            sysUserService.updateById(sysUser);
            // 删除重试次数
            this.resetPwdLoginFail(username);
            // 部门查询
            SysDepartEntity sysDepart;
            if(StringUtil.isEmpty(sysUser.getLoginDepartId())) {
                sysDepart = this.sysDepartService.getById(sysUser.getDepartId());
            }else{
                sysDepart = this.sysDepartService.getById(sysUser.getLoginDepartId());
            }
            // 构造返回结果
            LoginResult resultJo = this.setResultData(sysUser, sysDepart);
            // 创建会话
            this.startSession(paramVo, sysUser, sysDepart, SysConstants.LOGIN_TYPE_USERPASS);
            resultVo.setResults(resultJo);
        } catch (ApiServiceException e) {
            return ResultVo.getResultVo(e.getStatus(), e.getMessage());
        }
        return resultVo;
    }

    @Override
    public LoginResult checkRole(String userDepartRolesId) {
        SysUserDepartRolesEntity po = userDepartRolesMapper.selectById(userDepartRolesId);
        if(po == null) {
            throw new ApiException(ResultCode.INVALIDPARAMETER);
        }
        LoginUserVo loginUserVo = LoginUtil.getLoginUser();
        // 用户信息设值
        SysUserEntity sysUser = sysUserService.getById(loginUserVo.getId());
        SysDepartEntity sysDepart = sysDepartService.getById(po.getDepId());
        UserVo userVo = this.setUserVo(sysUser, sysDepart);
        // 部门信息设值
        DepartVo departVo = this.setDepartVo(sysDepart);
        LoginResult loginResult = new LoginResult();
        loginResult.setCheckRole(false);
        loginResult.setUser(userVo);
        loginResult.setOrg(departVo);
        if(!po.getDepId().equals(loginUserVo.getCurDepartId()) || !po.getRoleId().equals(loginUserVo.getCurRoleId())) {
            SysUserEntity userEntity = new SysUserEntity();
            userEntity.setId(loginUserVo.getId());
            userEntity.setLoginRoleId(po.getRoleId());
            userEntity.setLoginDepartId(po.getDepId());
            //更新表
            sysUserService.updateById(userEntity);
            loginUserVo.setDepartVo(departVo);
            loginUserVo.setCurRoleId(po.getRoleId());
            loginUserVo.setCurDepartId(po.getDepId());
            wrapOrgPaths(sysDepart, loginUserVo);
            //更新缓存
            apiSessionService.updateLoginUser(loginUserVo.getSid(),loginUserVo);
        }
        return loginResult;
    }

    /**
     * 开启会话
     * @param sysUser
     * @param sysDepart
     */
    private void startSession(ParamVo paramVo, SysUserEntity sysUser, SysDepartEntity sysDepart, String loginType) {
        LoginUserVo loginUser = new LoginUserVo();
        loginUser.setId(sysUser.getId());
        loginUser.setIdcard(sysUser.getIdcard());
        loginUser.setPoliceNo(sysUser.getPoliceNo());
        loginUser.setRealname(sysUser.getRealname());
        loginUser.setSid(paramVo.getSid());
        loginUser.setUsername(sysUser.getUsername());
        //缓存用户身份
        loginUser.setUserIdentity(sysUser.getUserIdentity());
        loginUser.setDepartVo((this.setDepartVo(sysDepart)));
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setCurDepartId(sysUser.getLoginDepartId());
        loginUser.setCurRoleId(sysUser.getLoginRoleId());
        loginUser.setLoginType(loginType);
        if(SysConstants.APP_KEY_OF_PC.equals(paramVo.getApp_key())){
            loginUser.setDeviceFlag(ClassConstant.SUB_VERSION_CLIENT);
        }else if(SysConstants.APP_KEY_OF_MOBILE.equals(paramVo.getApp_key())){
            loginUser.setDeviceFlag(ClassConstant.SUB_VERSION_MOBILE);
        }
        wrapOrgPaths(sysDepart, loginUser);
        apiSessionService.setLoginUser(paramVo.getSid(), loginUser, paramVo.getRemoteIp(), paramVo.getClientVersion());
    }

    /**
     * 计算用户权限范围
     * @param sysDepart
     * @param loginUser
     */
    private void wrapOrgPaths(SysDepartEntity sysDepart, LoginUserVo loginUser) {
        SysDataPermissionModel dataPermissionModel = roleService.getUserDataScope(loginUser);
        if(!ObjectUtils.isEmpty(dataPermissionModel)) {
            List<String> orgPaths = new ArrayList<>();
            if(DataScopeEnum.DEPART.getCode().equals(dataPermissionModel.getDataScope())) {
                orgPaths.add(sysDepart.getPath());
            }else if(DataScopeEnum.DEPART_WITH_SUB.getCode().equals(dataPermissionModel.getDataScope())) {
                orgPaths.add(String.format(SysConstants.FORMAT_LIKE_RIGHT,sysDepart.getPath()));
            }else if(DataScopeEnum.CUSTOM.getCode().equals(dataPermissionModel.getDataScope())) {
                List<SysCustomDepartModel> customDepartModels = dataPermissionModel.getCustomDepartModels();
                if(com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(customDepartModels)) {
                    for(SysCustomDepartModel customDepartModel : customDepartModels) {
                        SysDepartEntity sysDepartEntity = sysDepartService.getById(customDepartModel.getDepartId());
                        if(DataScopeEnum.DEPART.getCode().equals(customDepartModel.getDataScope())) {
                            orgPaths.add(sysDepartEntity.getPath());
                        } else if(DataScopeEnum.DEPART_WITH_SUB.getCode().equals(customDepartModel.getDataScope())) {
                            orgPaths.add(String.format(SysConstants.FORMAT_LIKE_RIGHT,sysDepartEntity.getPath()));
                        }
                    }
                }
            }
            loginUser.setOrgPaths(orgPaths);
        }
    }


    /**
     * 返回结果赋值
     * @return
     */
    private LoginResult setResultData(SysUserEntity sysUser, SysDepartEntity sysDepart) {
        // 用户信息设值
        UserVo userVo = this.setUserVo(sysUser, sysDepart);
        // 部门信息设值
        DepartVo departVo = this.setDepartVo(sysDepart);
        // 查找用户-部门-角色关系
        List<UserDepartRoleDetailVo> departRoleDetailVos = userDepartRolesMapper.selectByUserId(sysUser.getId());
        //用户没有配置角色，直接抛出异常
        if(CollectionUtils.isEmpty(departRoleDetailVos)) {
            throw new ApiException(LoginResultCodeEnum.USER_WITHOUT_ROLE);
        }
        LoginResult loginResult = new LoginResult();
        loginResult.setCheckRole(true);
        //查找默认登录的部门、角色是否存在用户-部门-角色关系中
        long count = departRoleDetailVos.stream().filter(rd -> rd.getDepId().equals(sysUser.getLoginDepartId()) && rd.getRoleId().equals(sysUser.getLoginRoleId())).count();
        if(count > 0) {
            loginResult.setCheckRole(false);
        }else{
            if(departRoleDetailVos.size() == 1) {
                sysUser.setLoginDepartId(departRoleDetailVos.get(0).getDepId());
                sysUser.setLoginRoleId(departRoleDetailVos.get(0).getRoleId());
                sysUserService.updateById(sysUser);
                loginResult.setCheckRole(false);
            }
        }
        loginResult.setUser(userVo);
        loginResult.setOrg(departVo);
        //根据登录的角色和用户id获取默认地址
        SysPersonalInfoVo sysPersonalInfo = sysPersonalSettingService.getSysPersonalInfo(sysUser.getId(), sysUser.getLoginRoleId(),sysUser.getDepartId());
        if (!ObjectUtils.isEmpty(sysPersonalInfo)){
            loginResult.setAddress(sysPersonalInfo);
        }
        //判断当前登录用户是否需要设置默认登录部门和角色
        return loginResult;
    }


    /**
     * 校验密码是不是初始密码
     *
     * @param
     */
    private void checkInitPwd(SysUserEntity sysUser) {
        // NeedChangePwd为空，对NeedChangePwd字段进行初始化，若用户密码跟原始密码一致，该字段值为1，否则为0.
        if (sysUser.getNeedChangePwd() == null) {
            String encryptPassword = PasswordUtil.encrypt(sysUser.getUsername(),initPassword, PasswordUtil.getStaticSalt());
            if (encryptPassword.equals(sysUser.getPassword())) {
                sysUser.setNeedChangePwd(SysConstants.YES);
            } else {
                sysUser.setNeedChangePwd(SysConstants.NO);
            }
        }
    }

    /**
     * 白名单校验
     * @param loginIp
     */
    private void validWhileIp(String loginIp) throws ApiServiceException {
        boolean whileConfig = sysConfigService.getBoolSysParam(ConfigConstants.WHILE_IS_OPEN);
        if(!whileConfig) {
            return;
        }
        boolean exist = false;
        String[] ips = loginIp.split(",");// 172.17.20.228,
        // 192.168.30.1
        if (ips.length > 0) {
            for (String ip : ips) {
                ip = ip.trim();
                long checkIp = IpSectionUtil.getIp2long(ip);
                if (checkWhileIp(new Object[] { "0", "2", checkIp,checkIp })) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                throw new ApiServiceException(LoginResultCodeEnum.LOGIN_FAIL_IP_AUTH.getResultCode(), "该IP:"
                        + loginIp + "不在访问名单中!");
            }
        }
    }

    private boolean checkWhileIp(Object[] objects) {
        List<SysIpWhileEntity> ipWhileEntityList = sysIpWhileService.query().
                in("accessType", objects[0], objects[1]).
                le("startLongIp", objects[2]).
                ge("endLongIp", objects[3]).list();
        if(CollectionUtils.isEmpty(ipWhileEntityList)) {
            return false;
        }
        return true;
    }


    /**
     * 校验单点登录（是否多个ip同时登录同一个账号）
     * @param loginIp
     * @param appKey
     * @param sysUser
     */
    private boolean checkSSO(String loginIp, String appKey, SysUserEntity sysUser) {
        // 是否单点登录
        boolean ssoConfig = sysConfigService.getBoolSysParam(ConfigConstants.CLIENT_IS_SSO);
        if(!ssoConfig) {
            return true;
        }
        LoginUserVo userVo = null;
        String tempSid = "";
        if(SysConstants.APP_KEY_OF_MOBILE.equals(appKey) && StringUtils.isNotEmpty(sysUser.getMobileSid())){
            tempSid = sysUser.getMobileSid();
        }else if (SysConstants.APP_KEY_OF_PC.equals(appKey) && StringUtils.isNotEmpty(sysUser.getPcSid())) {
            tempSid = sysUser.getPcSid();
        }
        if(StringUtils.isNotEmpty(tempSid)) {
            String userStr = apiSessionService.getLoginUser(tempSid);
            userVo = JSONObject.parseObject(userStr, LoginUserVo.class);
        }
        if (null != userVo) {
            if (!sysUser.getLoginIp().equals(loginIp)) {
                return false;
            } else {
                if(apiSessionService.hasSession(tempSid)) {
                    apiSessionService.removeSession(tempSid);
                    String pcname = SysConstants.APP_KEY_OF_MOBILE.equals(appKey) ? "手机端" : "客户端";
                    log.info("同IP下登录踢出，" + pcname + "IP：{}，被踢用户：{}", loginIp, JSONObject.toJSONString(userVo));
                }
            }
        }
        return true;
    }


    /**
     * 校验登录重试次数
     * @param userName
     * @return
     */
    private PwdFailVo getPwdLoginFailTips(String userName) {
        PwdFailVo failVo = new PwdFailVo(false,"");
        String loginConfig = sysConfigService.getSysParam(ConfigConstants.loginParamConfig);
        LoginConfigVo configVo =LoginConfigVo.getLoginConfigVo(loginConfig);
        if(configVo != null && configVo.getRetryCount() != null && configVo.getRetryCount() > 0) {
            String key = getPwdLoginCacheKey(userName);
            if(redisUtil.hasKey(key)) {
                Integer cwcs = (Integer) redisUtil.get(key);
                if(cwcs >= configVo.getRetryCount()) {
                    failVo.setUserLocked(true);
                    failVo.setMsg("用户名或密码错误超过" + configVo.getRetryCount() + "次，已锁住账号，"+configVo.getLockSecend()+"s后重新输入");
                }else{
                    failVo.setMsg("用户名或密码错误"+cwcs+"次，还剩"+(configVo.getRetryCount() - cwcs )+"次，错误超过"+configVo.getRetryCount()+"次将锁住账号");
                }
            }
        }
        return failVo;
    }

    public void addPwdLoginFail(String userName) {
        String key = getPwdLoginCacheKey(userName);
        if(redisUtil.hasKey(key)) {
            Integer cwcs = (Integer) redisUtil.get(key);
            redisUtil.set(key, cwcs + 1);
        }else{
            redisUtil.set(key, 1);
        }
        String loginConfig = sysConfigService.getSysParam(ConfigConstants.loginParamConfig);
        LoginConfigVo configVo =LoginConfigVo.getLoginConfigVo(loginConfig);
        if(configVo != null && configVo.getLockSecend() != null && configVo.getLockSecend() > 0) {
            redisUtil.expire(key, configVo.getLockSecend());
        }else{
            redisUtil.expire(key, 30);
        }
    }

    private void resetPwdLoginFail(String userName) {
        String key = getPwdLoginCacheKey(userName);
        if(redisUtil.hasKey(key)) {
            redisUtil.del(key);
        }
    }


    /**
     * 判断用户是否有效
     * @param loginPwdVo
     */
    private SysUserEntity checkUser(LoginPwdVo loginPwdVo) throws ApiServiceException {
        SysUserEntity sysUser;
        if(IdcardUtil.isValidCard(loginPwdVo.getLoginId())) {
            // sysUser = sysUserService.getUserByIdcard(loginPwdVo.getLoginId());
            throw new ApiServiceException(LoginResultCodeEnum.LOGIN_IDCARD_ERROR);
        }else{
            sysUser = sysUserService.getUserByName(loginPwdVo.getLoginId());
        }
        //情况1：根据用户信息查询，该用户不存在
        if (sysUser == null) {
            throw new ApiServiceException(LoginResultCodeEnum.USER_NOT_EXIST);
        }
        //情况2：根据用户信息查询，该用户已注销
        if (CommonConstant.DEL_FLAG_1.toString().equals(sysUser.getDelFlag())) {
            throw new ApiServiceException(LoginResultCodeEnum.USER_CANCELLED);
        }
        //情况3：根据用户信息查询，该用户已冻结
        if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
            throw new ApiServiceException(LoginResultCodeEnum.USER_FREEZE);
        }
        return sysUser;
    }

    private List<DepartVo> getUserDepart(String userId) {
        List<SysDepartEntity> departs = sysUserDepartService.queryDepartOfUser(userId);
        List<DepartVo> departVos = new ArrayList<>();
        for(SysDepartEntity sysDepartEntity : departs) {
            DepartVo departVo = new DepartVo();
            departVo.setId(sysDepartEntity.getId());
            departVo.setName(sysDepartEntity.getDepartName());
            departVo.setDepartCode(sysDepartEntity.getOrgCode());
            departVo.setOrgType(sysDepartEntity.getOrgType());
            departVos.add(departVo);
        }
        return departVos;
    }

    private DepartVo setDepartVo(SysDepartEntity sysDepart) {
        DepartVo departVo = new DepartVo();
        departVo.setId(sysDepart.getId());
        departVo.setDepartCode(sysDepart.getOrgCode());
        departVo.setName(sysDepart.getDepartName());
        departVo.setOrgType(sysDepart.getOrgType());
        departVo.setPath(sysDepart.getPath());
        departVo.setSysCode(sysDepart.getSysCode());
        departVo.setAreaCode(sysDepart.getAreaCode());
        if(org.apache.commons.lang3.StringUtils.isNotBlank(departVo.getAreaCode())){
            departVo.setAreaName(sysAreaService.getFullAreaNameByAreaCode(departVo.getAreaCode()));
        }

        SysDepartEntity topDept = sysDepartService.getTopDepart(sysDepart.getId());
        if(topDept != null){
            departVo.setTopDeptName(topDept.getDepartName());
            departVo.setCurrentDeptIsTopDept(false);
            if (topDept.getId().equals(sysDepart.getId())){
                departVo.setCurrentDeptIsTopDept(true);
            }
        }

        return departVo;
    }


    private UserVo setUserVo(SysUserEntity sysUser, SysDepartEntity sysDepart) {
        UserVo userVo = new UserVo();
        userVo.setUserId(sysUser.getId());
        userVo.setName(sysUser.getRealname());
        userVo.setUserName(sysUser.getUsername());
        userVo.setPoliceNo(sysUser.getPoliceNo());
        userVo.setIdCard(sysUser.getIdcard());
        userVo.setDeptId(sysDepart.getId());
        userVo.setDeptCode(sysDepart.getOrgCode());
        String topDeptId = sysDepartService.getTopDepartId(sysDepart.getId());
        userVo.setTopDeptId(topDeptId);
        userVo.setMobilePhone(sysUser.getMobilephone());
        userVo.setPwd(sysUser.getPassword());
        userVo.setNeedChangePwd(sysUser.getNeedChangePwd());
        if(SysConstants.YES.equals(sysUser.getNeedChangePwd())) {
            userVo.setNeedChangePwdInfo(SysConstants.CHANGE_PWD_WARN);
        }
        userVo.setSex(sysUser.getSex());
        return userVo;
    }

    private String getPwdLoginCacheKey(String userName) {
        return CacheKeyConstants.PwdLoginFail + userName;
    }


    private String getDbType() {
        if(this.dbDriver.contains("mysql")) {
            return "mysql";
        }
        if(this.dbDriver.contains("oracle")) {
            return "oracle";
        }
        return "mysql";
    }

    /**
     * 单点登录
     * @param paramVo
     * @return
     */
    @Override
    public LoginResult loginSSO(ParamVo<LoginSsoVo> paramVo) {
        LoginSsoVo vo = paramVo.getData();
        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(ID_TYPE_IDCARD.equals(vo.getIdType())) {
            queryWrapper.eq(SysUserEntity::getIdcard, vo.getIdValue());
        } else if (ID_TYPE_POLICE_NO.equals(vo.getIdType())) {
            queryWrapper.eq(SysUserEntity::getPoliceNo, vo.getIdValue());
        } else {
            throw new ApiException(LoginResultCodeEnum.LOGIN_SSO_TYPE_ERROR);
        }
        SysUserEntity userEntity = this.sysUserService.getOne(queryWrapper, false);
        if(userEntity == null) {
            throw new ApiException(LoginResultCodeEnum.LOGIN_SSO_NOT_USER);
        }
        SysDepartEntity departEntity;
        if(StringUtil.isNotEmpty(paramVo.getData().getOrgCode())) {
            departEntity = sysDepartService.findDepartByOrgCode(paramVo.getData().getOrgCode());
            if(!ObjectUtils.isEmpty(departEntity)){
                List<SysUserDepartEntity> udList = sysUserDepartService.queryListByUserId(userEntity.getId());
                if(CollectionUtils.isEmpty(udList) || udList.stream().filter(ud -> ud.getDepId().equals(departEntity.getId())).count() == 0) {
                    throw new ApiException("105","笔录系统不存在【"+userEntity.getRealname()+"】-【"+departEntity.getDepartName()+"】的关系");
                }
            }else{
                throw new ApiException("106","笔录系统不存在部门编号【"+paramVo.getData().getOrgCode()+"】");
            }
        }else{
            departEntity = this.sysDepartService.getById(userEntity.getDepartId());
        }
        // 更新用户
        userEntity.setLoginDepartId(departEntity.getId());
        List<UserDepartRoleDetailVo> departRoleDetailVos = userDepartRolesMapper.selectByUserId(userEntity.getId());
        if(!CollectionUtils.isEmpty(departRoleDetailVos)) {
            String roleId = "";
            for(UserDepartRoleDetailVo udr : departRoleDetailVos) {
                if(departEntity.getId().equals(udr.getDepId())) {
                    roleId = udr.getRoleId();
                    if(departEntity.getId().equals(userEntity.getLoginDepartId()) && udr.getRoleId().equals(userEntity.getLoginRoleId())) {
                        roleId = udr.getRoleId();
                    }
                }
            }
            userEntity.setLoginRoleId(roleId);
        }
        userEntity.setLoginIp(paramVo.getRemoteIp());
        userEntity.setPcSid(paramVo.getSid());
        sysUserService.updateById(userEntity);
        // 构造返回结果
        LoginResult resultJo = this.setResultData(userEntity, departEntity);
        // 创建会话
        this.startSession(paramVo, userEntity, departEntity, SysConstants.LOGIN_TYPE_SSO);
        return resultJo;
    }
}
