package com.fardo.modules.system.user.service.impl;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.api.vo.Result;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RedisLock;
import com.fardo.common.constant.CacheConstant;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.enums.IndexUniqEnum;
import com.fardo.common.exception.ApiException;
import com.fardo.common.exception.FdException;
import com.fardo.common.system.api.ISysBaseAPI;
import com.fardo.common.system.base.entity.BaseEntity;
import com.fardo.common.system.vo.LoginUser;
import com.fardo.common.system.vo.SysUserCacheInfo;
import com.fardo.common.util.*;
import com.fardo.common.util.security.Md5;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.mapper.SysDepartMapper;
import com.fardo.modules.system.depart.model.SysDepartModel;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import com.fardo.modules.system.permission.mapper.SysPermissionMapper;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.modules.system.role.mapper.SysRoleMapper;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import com.fardo.modules.system.user.entity.SysUserDepartEntity;
import com.fardo.modules.system.user.entity.SysUserDepartRolesEntity;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.entity.SysUserRoleEntity;
import com.fardo.modules.system.user.enums.PermissionEnum;
import com.fardo.modules.system.user.enums.UserResultCodeEnum;
import com.fardo.modules.system.user.mapper.SysUserDepartMapper;
import com.fardo.modules.system.user.mapper.SysUserDepartRolesMapper;
import com.fardo.modules.system.user.mapper.SysUserMapper;
import com.fardo.modules.system.user.mapper.SysUserRoleMapper;
import com.fardo.modules.system.user.model.SysDepartUserModel;
import com.fardo.modules.system.user.model.SysUserModel;
import com.fardo.modules.system.user.model.SysUserSiteModel;
import com.fardo.modules.system.user.model.SysUserSysDepartModel;
import com.fardo.modules.system.user.service.ISysUserDepartService;
import com.fardo.modules.system.user.service.ISysUserRoleService;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.system.user.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @Author: scott
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserEntity> implements ISysUserService {

    private static String[] XXZM = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private static AtomicBoolean taskStatus = new AtomicBoolean(false);

    private static TaskResultBaseVo taskVo = new TaskResultBaseVo();

    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysPermissionMapper sysPermissionMapper;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private SysUserDepartMapper sysUserDepartMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private SysDepartMapper sysDepartMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private ISysUserRoleService sysUserRoleService;
    @Resource
    private SysUserDepartRolesMapper sysUserDepartRolesMapper;
    @Autowired
    private ISysUserDepartService sysUserDepartService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysDepartService departService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${user.init.password}")
    private String initPassword;

    @Override
    public List<SysDepartUserModel> getDepartUserList(String userId, String departId) {
        List<SysDepartUserModel> resultList = new ArrayList<>();
        List<SysDepartModel> departModels = departService.querySubDeptList(departId);
        if (!CollectionUtils.isEmpty(departModels)) {
            for (SysDepartModel depart : departModels) {
                SysDepartUserModel model = new SysDepartUserModel(depart.getId(), depart.getDepartName(), SysDepartUserModel.TYPE_BM);
                resultList.add(model);
            }
        }
        if (StringUtil.isNotEmpty(departId)) {
            LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUserEntity::getDepartId, departId);
            queryWrapper.eq(SysUserEntity::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<SysUserEntity> userList = this.userMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(userList)) {
                for (SysUserEntity user : userList) {
                    SysDepartUserModel model = new SysDepartUserModel(user.getId(), user.getRealname(), SysDepartUserModel.TYPE_RY);
                    resultList.add(model);
                }
            }
        }
        return resultList;
    }

    @Override
    public SysUserVo getUserDetail(String userId) {
        //获取用户信息
        SysUserEntity sysUser = userMapper.selectById(userId);
        if (sysUser == null) {
            throw new ApiException(ResultCode.INVALIDPARAMETER);
        }
        SysUserVo vo = new SysUserVo();
        BeanUtils.copyProperties(sysUser, vo);
        vo.setOtherPositionFlag(false);
        //获取用户-部门-角色信息
        List<UserDepartRoleDetailVo> userDepartRoles = sysUserDepartRolesMapper.selectByUserId(userId);
        if (!CollectionUtils.isEmpty(userDepartRoles)) {
            List<String> departIds = userDepartRoles.stream().map(UserDepartRoleDetailVo::getDepId).distinct().collect(Collectors.toList());
            List<SysUserPositionVo> list = new ArrayList<>();
            for (String departId : departIds) {
                if(StringUtil.isEmpty(departId)) {
                    continue;
                }
                if (departId.equals(sysUser.getDepartId())) {
                    vo.setDepartName(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getDepartName).distinct().collect(Collectors.joining()));
                    vo.setRoleIds(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getRoleId).collect(Collectors.joining(",")));
                    vo.setRoleNames(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getRoleName).collect(Collectors.joining(",")));
                } else {
                    SysUserPositionVo up = new SysUserPositionVo();
                    up.setDepartId(departId);
                    up.setDepartName(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getDepartName).distinct().collect(Collectors.joining()));
                    up.setRoleIds(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getRoleId).collect(Collectors.joining(",")));
                    up.setRoleNames(userDepartRoles.stream().filter(dr -> dr.getDepId().equals(departId)).map(UserDepartRoleDetailVo::getRoleName).collect(Collectors.joining(",")));
                    list.add(up);
                }
            }
            if (!CollectionUtils.isEmpty(list)) {
                vo.setOtherPositionFlag(true);
                vo.setOtherPositions(list);
            }
        }
        return vo;
    }

    @Override
    @Transactional
    public ResultVo<SysUserIdParamVo> saveUser(SysUserVo sysUserVo) {
        ResultVo<SysUserIdParamVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysUserEntity sysUser = SysUserVo.getSysUser(null, sysUserVo);
        //判断手机号码是否重复
        String mobilephone = sysUser.getMobilephone();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(mobilephone)) {
            QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mobilephone", mobilephone);
            queryWrapper.eq("del_flag", 0);
            List<SysUserEntity> sysUserEntities = baseMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(sysUserEntities)) {
                throw new ApiException(UserResultCodeEnum.PHONE_EXIST);
            }
        }
        //用户名为空，自动生成用户名
        if (StringUtil.isEmpty(sysUser.getUsername())) {
            sysUser.setUsername(getUserName(sysUser.getRealname(), sysUser.getIdcard(),null));
        } else {
            //检验用户名是否已存在
            if (getUserByName(sysUser.getUsername()) != null) {
                throw new ApiException(UserResultCodeEnum.USERNAME_EXIST);
            }
        }
        //校验身份证号是否重复
        existsIdCard(sysUser);
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), sysUser.getPassword(), PasswordUtil.getStaticSalt());
        //用户密码加密
        sysUser.setPassword(passwordEncode);
        sysUser.setStatus(CommonConstant.USER_UNFREEZE);
        sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
        sysUser.setRealnamePy(PinyinUtil.getPinYinHeadChar(sysUser.getRealname()));
        //保存用户信息
        this.save(sysUser);
        sysUserVo.setId(sysUser.getId());
        //保存用户角色关系
        this.addUserWithRole(sysUser, sysUserVo.getRoleIds());
        //保存用户部门关系
        this.addUserWithDepart(sysUser, sysUser.getDepartId());
        if (!CollectionUtils.isEmpty(sysUserVo.getOtherPositions())) {
            for (SysUserPositionVo vo : sysUserVo.getOtherPositions()) {
                this.addUserWithDepart(sysUser, vo.getDepartId());
            }
        }
        //保存用户部门角色关系
        this.addUserWithDepartRoles(sysUser.getId(), sysUser.getDepartId(), sysUserVo.getRoleIds(), sysUserVo.getOtherPositions());
        SysUserIdParamVo sysUserIdParamVo = new SysUserIdParamVo();
        sysUserIdParamVo.setId(sysUserVo.getId());
        resultVo.setResults(sysUserIdParamVo);
        return resultVo;
    }

    @Override
    @Transactional
    public ResultVo<SysUserIdParamVo> updateUser(SysUserUpdateVo sysUserVo) {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        ResultVo<SysUserIdParamVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysUserEntity sysUser = this.getById(sysUserVo.getId());
        if (sysUser == null) {
            throw new ApiException(ResultCode.INVALIDPARAMETER);
        }

        //判断手机号码是否重复
        String mobilephone = sysUser.getMobilephone();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(mobilephone)) {
            QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mobilephone", mobilephone);
            queryWrapper.eq("del_flag", 0);
            queryWrapper.ne("ID",sysUserVo.getId());
            List<SysUserEntity> sysUserEntities = baseMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(sysUserEntities)) {
                throw new ApiException(UserResultCodeEnum.PHONE_EXIST);
            }
        }

        String operDesc = String.format("（用户名：%s）", sysUser.getUsername());
        request.setAttribute(SysConstants.LOG_OPER_DESC, operDesc);
        request.setAttribute(SysConstants.LOG_RESULT_DATA, operDesc);

        sysUser = SysUserUpdateVo.getSysUser(sysUser, sysUserVo);
        //校验身份证号是否重复
        existsIdCard(sysUser);
        sysUser.setRealnamePy(PinyinUtil.getPinYinHeadChar(sysUser.getRealname()));
        //更新用户信息
        this.userMapper.updateById(sysUser);
        //更新用户角色关系
        sysUserRoleMapper.deleteByUserId(sysUser.getId());
        this.addUserWithRole(sysUser, sysUserVo.getRoleIds());
        //更新用户部门关系
        sysUserDepartMapper.deleteByUserId(sysUser.getId());
        this.addUserWithDepart(sysUser, sysUser.getDepartId());
        if (!CollectionUtils.isEmpty(sysUserVo.getOtherPositions())) {
            for (SysUserPositionVo vo : sysUserVo.getOtherPositions()) {
                this.addUserWithDepart(sysUser, vo.getDepartId());
            }
        }
        //更新用户部门角色关系
        sysUserDepartRolesMapper.deleteByUserId(sysUser.getId());
        this.addUserWithDepartRoles(sysUser.getId(), sysUser.getDepartId(), sysUserVo.getRoleIds(), sysUserVo.getOtherPositions());
        SysUserIdParamVo sysUserIdParamVo = new SysUserIdParamVo();
        sysUserIdParamVo.setId(sysUserVo.getId());
        resultVo.setResults(sysUserIdParamVo);
        return resultVo;
    }


    @Override
    @Transactional
    public void resetPassword(SysUserPasswordResetVo userPasswordResetVo) {
        SysUserEntity sysUser = this.userMapper.selectById(userPasswordResetVo.getId());
        if (sysUser == null) {
            throw new ApiException(ResultCode.INVALIDPARAMETER);
        }
        String password = userPasswordResetVo.getPassword();
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, PasswordUtil.getStaticSalt());
        sysUser.setPassword(passwordEncode);
        this.userMapper.updateById(sysUser);
    }

    @Override
    public IPage<SysUserModel> getPageModelList(String userId, SysUserParamVo sysUserParamVo) {
        IPage<SysUserModel> modelIPage = new Page<>();
        DataScopeVo dataScopeVo = sysUserRoleService.getDataScope(userId, PermissionEnum.userList);
        if (StringUtil.isNotEmpty(sysUserParamVo.getIdcard())) {
            sysUserParamVo.setIdcard(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getIdcard()));
        }
        if (StringUtil.isNotEmpty(sysUserParamVo.getPoliceNo())) {
            sysUserParamVo.setPoliceNo(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getPoliceNo()));
        }
        if (StringUtil.isNotEmpty(sysUserParamVo.getRealname())) {
            sysUserParamVo.setRealname(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getRealname()));
        }
        if (StringUtil.isNotEmpty(sysUserParamVo.getUsername())) {
            sysUserParamVo.setUsername(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getUsername()));
        }
        if (StringUtil.isNotEmpty(sysUserParamVo.getDepartName())) {
            sysUserParamVo.setDepartName(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getDepartName()));
        }
        if (StringUtil.isNotEmpty(sysUserParamVo.getMobilephone())) {
            sysUserParamVo.setMobilephone(String.format(SysConstants.FORMAT_LIKE_ANYWHERE, sysUserParamVo.getMobilephone()));
        }
        // TODO 权限
        IPage<SysUserEntity> pageList = this.userMapper.getPageList(new Page(sysUserParamVo.getPageNo(), sysUserParamVo.getPageSize()), sysUserParamVo, null);
        BeanUtils.copyProperties(pageList, modelIPage, "records");
        if (!CollectionUtils.isEmpty(pageList.getRecords())) {
            List<SysUserModel> list = new ArrayList<>(pageList.getRecords().size());
            HashSet<String> departIds = new HashSet<>();
            pageList.getRecords().forEach(m -> {
                SysUserModel model = new SysUserModel();
                BeanUtils.copyProperties(m, model);
                departIds.add(m.getDepartId());
                list.add(model);
            });
            //查找相关部门
            List<SysDepartEntity> departs = sysDepartMapper.selectBatchIds(departIds);
            list.forEach(m -> {
                m.setDepartName(departs.stream().filter(d -> d.getId().equals(m.getDepartId())).map(d -> d.getDepartName()).collect(Collectors.joining(",")));
            });
            modelIPage.setRecords(list);
        }
        return modelIPage;
    }

    @Override
    @Transactional
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> addUser(SysUserEntity sysUser, String roles) {
        checkData(sysUser);
        SysDepartEntity departEntity = sysDepartMapper.selectById(sysUser.getDepartId());
        if (departEntity == null) {
            return Result.error("部门信息有误");
        }
        sysUser.setUsername(getUserName(sysUser.getRealname(), sysUser.getIdcard(),null));
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), sysUser.getPassword(), PasswordUtil.getStaticSalt());
        sysUser.setPassword(passwordEncode);
        sysUser.setStatus(CommonConstant.USER_UNFREEZE);
        sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
        this.save(sysUser);
        this.addUserWithRole(sysUser, roles);
        this.addUserWithDepart(sysUser, sysUser.getDepartId());
        return Result.ok();
    }

    @Override
    @Transactional
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> updateUser(SysUserEntity sysUser, String roles) {
        checkData(sysUser);
        SysUserEntity sysUserEntity = this.getById(sysUser.getId());
        if (sysUserEntity == null) {
            return Result.error("未找到对应实体");
        }
        if (!sysUserEntity.getDepartId().equals(sysUser.getDepartId())) {
            SysDepartEntity departEntity = sysDepartMapper.selectById(sysUser.getDepartId());
            if (departEntity == null) {
                return Result.error("部门信息有误");
            }
        }
        this.userMapper.updateById(sysUser);
        this.editUserWithRole(sysUser, roles);
        this.editUserWithDepart(sysUser, sysUser.getDepartId());
        return Result.ok();
    }

    private void checkData(SysUserEntity sysUser) {
        if (!IdcardUtil.isValidCard(sysUser.getIdcard())) {
            throw new FdException("身份证号格式有误");
        }
        if (!ReUtil.isMatch(SysConstants.RE_POLICE_NO, sysUser.getPoliceNo())) {
            throw new FdException("警号号格式有误");
        }
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword) {
        SysUserEntity user = userMapper.getUserByName(username);
        if (!StringUtils.isEmpty(oldpassword)) {
            String passwordEncode = PasswordUtil.encrypt(username, oldpassword, PasswordUtil.getStaticSalt());
            if (!user.getPassword().equals(passwordEncode)) {
                return Result.error("旧密码输入错误!");
            }
        }
        if (oConvertUtils.isEmpty(newpassword)) {
            return Result.error("新密码不允许为空!");
        }
        if (!newpassword.equals(confirmpassword)) {
            return Result.error("两次输入密码不一致!");
        }
        String password = PasswordUtil.encrypt(username, newpassword, PasswordUtil.getStaticSalt());
        this.userMapper.update(new SysUserEntity().setPassword(password), new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getId, user.getId()));
        return Result.ok("密码重置成功!");
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public Result<?> changePassword(SysUserEntity sysUser) {
        String password = sysUser.getPassword();
        String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, PasswordUtil.getStaticSalt());
        sysUser.setPassword(passwordEncode);
        this.userMapper.updateById(sysUser);
        return Result.ok("密码修改成功!");
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public ResultVo logicDeleteUser(String userId) {
        ResultVo resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        //逻辑删除用户
        SysUserEntity user = this.getById(userId);
        user.setId(userId);
        user.setDelFlag(CommonConstant.DEL_FLAG_1);
        this.userMapper.updateById(user);
        //删除用户角色关系
        this.sysUserRoleMapper.deleteByUserId(userId);
        //删除用户部门关系
        this.sysUserDepartMapper.deleteByUserId(userId);
        //删除用户部门角色关系
        this.sysUserDepartRolesMapper.deleteByUserId(userId);
        String operDesc = String.format("（用户名：%s）", user.getUsername());
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        request.setAttribute(SysConstants.LOG_OPER_DESC, operDesc);
        request.setAttribute(SysConstants.LOG_RESULT_DATA, operDesc);
        return resultVo;
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchUsers(String userIds) {
        //1.删除用户
        this.removeByIds(Arrays.asList(userIds.split(",")));
        return false;
    }

    @Override
    public SysUserEntity getUserByName(String username) {
        return userMapper.getUserByName(username);
    }

    @Override
    public SysUserEntity getUserByPoliceNo(String policeNo) {
        return userMapper.selectOne(new QueryWrapper<SysUserEntity>().lambda().eq(SysUserEntity::getPoliceNo, policeNo).eq(SysUserEntity::getDelFlag, CommonConstant.DEL_FLAG_0));
    }

    @Override
    public SysUserEntity getUserByIdcard(String idcard) {
        return userMapper.selectOne(new QueryWrapper<SysUserEntity>().lambda().eq(SysUserEntity::getIdcard, idcard).eq(SysUserEntity::getDelFlag, CommonConstant.DEL_FLAG_0));
    }

    private void existsIdCard(SysUserEntity sysUserEntity) {
        int count = userMapper.selectCount(new QueryWrapper<SysUserEntity>().lambda().eq(SysUserEntity::getIdcard, sysUserEntity.getIdcard()).eq(SysUserEntity::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(count == 1) {
            SysUserEntity po = getUserByIdcard(sysUserEntity.getIdcard());
            if(!po.getId().equals(sysUserEntity.getId())) {
                throw new ApiException(UserResultCodeEnum.IDCARD_EXIST);
            }
        }
        if(count > 1) {
            throw new ApiException(UserResultCodeEnum.IDCARD_EXIST);
        }
    }

    @Override
    @Transactional
    public void addUserWithRole(SysUserEntity user, String roles) {
        if (oConvertUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                SysUserRoleEntity userRole = new SysUserRoleEntity(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    @Transactional
    public void editUserWithRole(SysUserEntity user, String roles) {
        sysUserRoleMapper.delete(new QueryWrapper<SysUserRoleEntity>().lambda().eq(SysUserRoleEntity::getUserId, user.getId()));
        addUserWithRole(user, roles);
    }


    @Override
    public List<String> getRole(String username) {
        return sysUserRoleMapper.getRoleByUserName(username);
    }

    /**
     * 通过用户名获取用户角色集合
     *
     * @param username 用户名
     * @return 角色集合
     */
    @Override
    public Set<String> getUserRolesSet(String username) {
        // 查询用户拥有的角色集合
        List<String> roles = sysUserRoleMapper.getRoleByUserName(username);
        log.info("-------通过数据库读取用户拥有的角色Rules------username： " + username + ",Roles size: " + (roles == null ? 0 : roles.size()));
        return new HashSet<>(roles);
    }

    /**
     * 通过用户名获取用户权限集合
     *
     * @param username 用户名
     * @return 权限集合
     */
    @Override
    public Set<String> getUserPermissionsSet(String username) {
        Set<String> permissionSet = new HashSet<>();
        List<SysPermissionEntity> permissionList = sysPermissionMapper.queryByUser(username);
        for (SysPermissionEntity po : permissionList) {
//			// TODO URL规则有问题？
//			if (oConvertUtils.isNotEmpty(po.getUrl())) {
//				permissionSet.add(po.getUrl());
//			}
            if (oConvertUtils.isNotEmpty(po.getPerms())) {
                permissionSet.add(po.getPerms());
            }
        }
        log.info("-------通过数据库读取用户拥有的权限Perms------username： " + username + ",Perms size: " + (permissionSet == null ? 0 : permissionSet.size()));
        return permissionSet;
    }

    @Override
    public SysUserCacheInfo getCacheUser(String username) {
        SysUserCacheInfo info = new SysUserCacheInfo();
        info.setOneDepart(true);
//		SysUser user = userMapper.getUserByName(username);
//		info.setSysUserCode(user.getUsername());
//		info.setSysUserName(user.getRealname());


        LoginUser user = sysBaseAPI.getUserByName(username);
        if (user != null) {
            info.setSysUserCode(user.getUsername());
            info.setSysUserName(user.getRealname());
            info.setSysOrgCode(user.getOrgCode());
        }

        //多部门支持in查询
        List<SysDepartEntity> list = sysDepartMapper.queryUserDeparts(user.getId());
        List<String> sysMultiOrgCode = new ArrayList<String>();
        if (list == null || list.size() == 0) {
            //当前用户无部门
            //sysMultiOrgCode.add("0");
        } else if (list.size() == 1) {
            sysMultiOrgCode.add(list.get(0).getOrgCode());
        } else {
            info.setOneDepart(false);
            for (SysDepartEntity dpt : list) {
                sysMultiOrgCode.add(dpt.getOrgCode());
            }
        }
        info.setSysMultiOrgCode(sysMultiOrgCode);

        return info;
    }

    // 根据部门Id查询
    @Override
    public IPage<SysUserEntity> getUserByDepId(Page<SysUserEntity> page, String departId, String username) {
        return userMapper.getUserByDepId(page, departId, username);
    }

    @Override
    public IPage<SysUserEntity> getUserByDepIds(Page<SysUserEntity> page, List<String> departIds, String username) {
        return userMapper.getUserByDepIds(page, departIds, username);
    }

    @Override
    public Map<String, String> getDepNamesByUserIds(List<String> userIds) {
        List<SysUserDepVo> list = this.baseMapper.getDepNamesByUserIds(userIds);

        Map<String, String> res = new HashMap<String, String>();
        list.forEach(item -> {
                    if (res.get(item.getUserId()) == null) {
                        res.put(item.getUserId(), item.getDepartName());
                    } else {
                        res.put(item.getUserId(), res.get(item.getUserId()) + "," + item.getDepartName());
                    }
                }
        );
        return res;
    }

    @Override
    public IPage<SysUserEntity> getUserByDepartIdAndQueryWrapper(Page<SysUserEntity> page, String departId, QueryWrapper<SysUserEntity> queryWrapper) {
        LambdaQueryWrapper<SysUserEntity> lambdaQueryWrapper = queryWrapper.lambda();

        lambdaQueryWrapper.eq(SysUserEntity::getDelFlag, "0");
        lambdaQueryWrapper.inSql(SysUserEntity::getId, "SELECT user_id FROM sys_user_depart WHERE dep_id = '" + departId + "'");

        return userMapper.selectPage(page, lambdaQueryWrapper);
    }

    @Override
    public IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, SysUserEntity userParams, IPage page) {
        List<SysUserSysDepartModel> list = baseMapper.getUserByOrgCode(page, orgCode, userParams);
        Integer total = baseMapper.getUserByOrgCodeTotal(orgCode, userParams);

        IPage<SysUserSysDepartModel> result = new Page<>(page.getCurrent(), page.getSize(), total);
        result.setRecords(list);

        return result;
    }

    // 根据角色Id查询
    @Override
    public IPage<SysUserEntity> getUserByRoleId(Page<SysUserEntity> page, String roleId, String username) {
        return userMapper.getUserByRoleId(page, roleId, username);
    }


    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, key = "#username")
    public void updateUserDepart(String username, String departId) {
        baseMapper.updateUserDepart(username, departId);
    }


    @Override
    public SysUserEntity getUserByPhone(String phone) {
        return userMapper.getUserByPhone(phone);
    }


    @Override
    public SysUserEntity getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    @Transactional
    public void addUserWithDepart(SysUserEntity user, String selectedParts) {
        if (oConvertUtils.isNotEmpty(selectedParts)) {
            String[] arr = selectedParts.split(",");
            for (String deaprtId : arr) {
                SysUserDepartEntity userDeaprt = new SysUserDepartEntity(user.getId(), deaprtId);
                sysUserDepartMapper.insert(userDeaprt);
            }
        }
    }

    private void addUserWithDepartRoles(String userId, String departId, String roles, List<SysUserPositionVo> userPositionVos) {
        if(oConvertUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                if(sysUserRoleService.checkRoleDepartExit(userId,departId,roleId)){
                    continue;
                }
                SysUserDepartRolesEntity userDepartRoles = new SysUserDepartRolesEntity(userId,departId,roleId);
                sysUserDepartRolesMapper.insert(userDepartRoles);
            }
        }
        if(!CollectionUtils.isEmpty(userPositionVos)) {
            for(SysUserPositionVo vo : userPositionVos) {
                String[] arr = vo.getRoleIds().split(",");
                for (String roleId : arr) {
                    if(sysUserRoleService.checkRoleDepartExit(userId,vo.getDepartId(),roleId)){
                        continue;
                    }
                    SysUserDepartRolesEntity userDepartRoles = new SysUserDepartRolesEntity(userId,vo.getDepartId(),roleId);
                    sysUserDepartRolesMapper.insert(userDepartRoles);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, allEntries = true)
    public void editUserWithDepart(SysUserEntity user, String departs) {
        String[] arr = {};
        if (oConvertUtils.isNotEmpty(departs)) {
            arr = departs.split(",");
        }
        //先删后加
        sysUserDepartMapper.delete(new QueryWrapper<SysUserDepartEntity>().lambda().eq(SysUserDepartEntity::getUserId, user.getId()));
        if (oConvertUtils.isNotEmpty(departs)) {
            for (String departId : arr) {
                SysUserDepartEntity userDepart = new SysUserDepartEntity(user.getId(), departId);
                sysUserDepartMapper.insert(userDepart);
            }
        }
    }


    /**
     * 校验用户是否有效
     *
     * @param sysUser
     * @return
     */
    @Override
    public Result<?> checkUserIsEffective(SysUserEntity sysUser) {
        Result<?> result = new Result<Object>();
        //情况1：根据用户信息查询，该用户不存在
        if (sysUser == null) {
            result.error500("账号输入错误，请重新输入");
            sysBaseAPI.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
            return result;
        }
        //情况2：根据用户信息查询，该用户已注销
        if (CommonConstant.DEL_FLAG_1.toString().equals(sysUser.getDelFlag())) {
            sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已注销");
            return result;
        }
        //情况3：根据用户信息查询，该用户已冻结
        if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
            sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已冻结");
            return result;
        }
        return result;
    }

    @Override
    public List<SysUserEntity> queryLogicDeleted() {
        return this.queryLogicDeleted(null);
    }

    @Override
    public List<SysUserEntity> queryLogicDeleted(LambdaQueryWrapper<SysUserEntity> wrapper) {
        if (wrapper == null) {
            wrapper = new LambdaQueryWrapper<>();
        }
        wrapper.eq(SysUserEntity::getDelFlag, "1");
        return userMapper.selectLogicDeleted(wrapper);
    }

    @Override
    public boolean revertLogicDeleted(List<String> userIds, SysUserEntity updateEntity) {
        String ids = String.format("'%s'", String.join("','", userIds));
        return userMapper.revertLogicDeleted(ids, updateEntity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeLogicDeleted(List<String> userIds) {
        String ids = String.format("'%s'", String.join("','", userIds));
        // 1. 删除用户
        int line = userMapper.deleteLogicDeleted(ids);
        // 2. 删除用户部门关系
        line += sysUserDepartMapper.delete(new LambdaQueryWrapper<SysUserDepartEntity>().in(SysUserDepartEntity::getUserId, userIds));
        //3. 删除用户角色关系
        line += sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRoleEntity>().in(SysUserRoleEntity::getUserId, userIds));
        return line != 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNullPhoneEmail() {
        userMapper.updateNullByEmptyString("email");
        userMapper.updateNullByEmptyString("phone");
        return true;
    }

    @Override
    public void saveThirdUser(SysUserEntity sysUser) {
        //保存用户
        String userid = UUIDGenerator.generate();
        sysUser.setId(userid);
        baseMapper.insert(sysUser);
        //获取第三方角色
        SysRoleEntity sysRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRoleEntity>().eq(SysRoleEntity::getRoleCode, "third_role"));
        //保存用户角色
        SysUserRoleEntity userRole = new SysUserRoleEntity();
        userRole.setRoleId(sysRole.getId());
        userRole.setUserId(userid);
        sysUserRoleMapper.insert(userRole);
    }

    @Override
    public List<SysUserEntity> queryByDepIds(List<String> departIds, String username) {
        return userMapper.queryByDepIds(departIds, username);
    }

    @Override
    public List<SysUserModel> getUserList(List<String> idList) {
        return userMapper.getUserList(idList);
    }

    @Override
    public List<SysUserModel> getUserSelect() {
        return userMapper.getUserSelect();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedisLock
    public void importExcel(List<SysUserExcelVo> userExcelVos) {
        if (taskStatus.compareAndSet(false, true)) {
            long start = System.currentTimeMillis();
            long t = System.currentTimeMillis();
            redisUtil.set(CacheKeyConstants.USER_IMP_TASK_START, true);
            try {
                if (userExcelVos != null) {
                    taskVo.init();
                    taskVo.refresh(TaskResultBaseVo.DOING, "正在校验导入数据", 0L);
                    taskVo.setTotalCount(userExcelVos.size());
                    flushCacheTaskVo(taskVo);
                    SysUserExcelVo userExcelVo;
                    int line = 2;
                    int emptyCount = 0;
                    List<SysUserEntity> userPoList = list();
                    HashSet<String> userNameMap = new HashSet<>();
                    HashSet<String> idCardMap = new HashSet<>();
                    HashSet<String> phoneMap = new HashSet<>();
                    if (!CollectionUtils.isEmpty(userPoList)) {
                        userNameMap = new HashSet<>(userPoList.size() + userExcelVos.size());
                        idCardMap = new HashSet<>(userPoList.size() + userExcelVos.size());
                        phoneMap = new HashSet<>(userPoList.size() + userExcelVos.size());
                        for (SysUserEntity po : userPoList) {
                            if (CommonConstant.DEL_FLAG_0.equals(po.getDelFlag())) {
                                userNameMap.add(po.getUsername());
                                if(StringUtil.isNotEmpty(po.getIdcard())) {
                                    idCardMap.add(po.getIdcard());
                                }
                                phoneMap.add(po.getMobilephone());
                            }
                        }
                        userPoList.clear();
                    }
                    log.info("读取全部用户数据耗时：{}秒", (System.currentTimeMillis() - t) / 1000);
                    //基础校验
                    t = System.currentTimeMillis();
                    for (int i = 0; i < userExcelVos.size(); i++) {
                        userExcelVo = userExcelVos.get(i);
                        if (FieldUtils.allFieldIsNull(userExcelVo)) {
                            emptyCount++;
                            continue;
                        }
                        if (StringUtil.isEmpty(userExcelVo.getRealname())) {
                            throw new RuntimeException("第" + (line + i) + "行真实姓名不能为空");
                        }
                        // if (StringUtil.isEmpty(userExcelVo.getIdcard())) {
                        //     throw new RuntimeException("第" + (line + i) + "行身份证号不能为空");
                        // }
                        if (StringUtil.isEmpty(userExcelVo.getPhone())) {
                            throw new RuntimeException("第" + (line + i) + "行手机号不能为空");
                        }
                        if (StringUtil.isEmpty(userExcelVo.getDepartCode())) {
                            throw new RuntimeException("第" + (line + i) + "行单位编码不能为空");
                        }
                        if (StringUtil.isEmpty(userExcelVo.getRoleCode())) {
                            throw new RuntimeException("第" + (line + i) + "行角色编码不能为空");
                        }
                        if (StringUtil.isEmpty(userExcelVo.getPoliceNo())) {
                            throw new RuntimeException("第" + (line + i) + "行执法证号不能为空");
                        }
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(userExcelVo.getIdcard())){
                            if (!IdcardUtil.isValidCard(userExcelVo.getIdcard())) {
                                throw new RuntimeException("第" + (line + i) + "行身份证号校验不通过");
                            }
                            if (idCardMap.contains(userExcelVo.getIdcard())) {
                                throw new RuntimeException("第" + (line + i) + "行身份证号已存在");
                            }
                            idCardMap.add(userExcelVo.getIdcard());
                        }
//                        if (!ReUtil.isMatch(SysConstants.RE_POLICE_NO, userExcelVo.getPoliceNo())) {
//                            throw new RuntimeException("第" + (line + i) + "行执法证号校验不通过");
//                        }
                        if (phoneMap.contains(userExcelVo.getPhone())) {
                            throw new RuntimeException("第" + (line + i) + "行手机号已存在");
                        }
                        phoneMap.add(userExcelVo.getPhone());
                        if (StringUtil.isNotEmpty(userExcelVo.getUsername())) {
                            if (ReUtil.isMatch("[0-9]*", userExcelVo.getUsername())) {
                                throw new RuntimeException("第" + (line + i) + "行用户名不能是纯数字");
                            }
                            if (userNameMap.contains(userExcelVo.getUsername())) {
                                throw new RuntimeException("第" + (line + i) + "行用户名已存在");
                            }
                            userNameMap.add(userExcelVo.getUsername());
                        }

                    }
                    if(emptyCount == userExcelVos.size()) {
                        throw new RuntimeException("无有效用户信息，请检查");
                    }
                    List<SysRoleEntity> roles = sysRoleMapper.selectList(null);
                    Set<String> departCodes = new HashSet<>(userExcelVos.size());
                    for (SysUserExcelVo vo : userExcelVos) {
                        if (roles.stream().filter(role -> role.getRoleCode().equals(vo.getRoleCode())).count() == 0) {
                            throw new RuntimeException(vo.getRoleCode() + "角色编码不存在");
                        }
                        departCodes.addAll(Arrays.asList(vo.getDepartCode().split(",")));
                    }
                    List<SysDepartEntity> departs = sysDepartMapper.selectList(new LambdaQueryWrapper<SysDepartEntity>().eq(SysDepartEntity::getDelFlag, CommonConstant.DEL_FLAG_0));
                    for (String departCode : departCodes) {
                        if (StringUtil.isEmpty(departCode)) {
                            continue;
                        }
                        if (departs.stream().filter(d -> departCode.equals(d.getOrgCode())).count() == 0) {
                            throw new RuntimeException(departCode + "单位编码不存在");
                        }
                    }
                    log.info("参数校验耗时：{}秒", (System.currentTimeMillis() - t) / 1000);
                    t = System.currentTimeMillis();
                    taskVo.refresh(TaskResultBaseVo.DOING, "正在导入用户数据", 0L);
                    flushCacheTaskVo(taskVo);
                    final int batchSize = 10000;
                    final String userId = LoginUtil.getLoginUser().getId();
                    final String createTime = DateUtils.getCurrentTime();
                    final String loginModes = "[\"1\",\"2\"]";
                    final String INSERT_USER_SQL = "insert into t_sys_user(id,username,idcard,police_no,realname,realname_py,mobilephone,password,create_by,create_time,other_position_flag,status,del_flag,login_modes,depart_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    List<Object[]> INSERT_USER_ORGS = new ArrayList<>(batchSize);
                    final String INSERT_UR_SQL = "insert into t_sys_user_role(id,user_id,role_id) values(?,?,?)";
                    List<Object[]> INSERT_UR_ORGS = new ArrayList<>(batchSize);
                    final String INSERT_UD_SQL = "insert into t_sys_user_depart(id,user_id,dep_id) values(?,?,?)";
                    List<Object[]> INSERT_UD_ORGS = new ArrayList<>(batchSize);
                    final String INSERT_UDR_SQL = "insert into t_sys_user_depart_roles(id,user_id,dep_id,role_id) values(?,?,?,?)";
                    List<Object[]> INSERT_UDR_ORGS = new ArrayList<>(batchSize);
                    for (SysUserExcelVo vo : userExcelVos) {
                        //组装用户数据
                        Object[] uorg = new Object[15];
                        vo.setId(UUIDGenerator.generate());
                        if (StringUtil.isEmpty(vo.getUsername())) {
                            vo.setUsername(getUserName(vo.getRealname(), vo.getPoliceNo(),userNameMap));
                        }
                        uorg[0] = vo.getId();
                        uorg[1] = vo.getUsername();
                        uorg[2] = vo.getIdcard();
                        uorg[3] = vo.getPoliceNo();
                        uorg[4] = vo.getRealname();
                        uorg[5] = PinyinUtil.getPinYinHeadChar(vo.getRealname());
                        uorg[6] = vo.getPhone();
                        uorg[7] = PasswordUtil.encrypt(vo.getUsername(), initPassword, PasswordUtil.getStaticSalt());
                        uorg[8] = userId;
                        uorg[9] = createTime;
                        uorg[10] = 0;
                        uorg[11] = CommonConstant.USER_UNFREEZE;
                        uorg[12] = CommonConstant.DEL_FLAG_0;
                        uorg[13] = loginModes;
                        String departId = departs.stream().filter(d -> d.getOrgCode().equals(vo.getDepartCode())).map(BaseEntity::getId).collect(Collectors.joining());
                        uorg[14] = departId;
                        INSERT_USER_ORGS.add(uorg);
                        //组装用户角色数据
                        Object[] urorg = new Object[3];
                        urorg[0] = UUIDGenerator.generate();
                        String roleId = roles.stream().filter(r -> r.getRoleCode().equals(vo.getRoleCode())).map(BaseEntity::getId).collect(Collectors.joining());
                        urorg[1] = vo.getId();
                        urorg[2] = roleId;
                        INSERT_UR_ORGS.add(urorg);
                        //组装用户部门数据
                        Object[] udorg = new Object[3];
                        udorg[0] = UUIDGenerator.generate();
                        udorg[1] = vo.getId();
                        udorg[2] = departId;
                        INSERT_UD_ORGS.add(udorg);
                        //组装用户部门角色数据
                        Object[] udrorg = new Object[4];
                        udrorg[0] = UUIDGenerator.generate();
                        udrorg[1] = vo.getId();
                        udrorg[2] = departId;
                        udrorg[3] = roleId;
                        INSERT_UDR_ORGS.add(udrorg);
                        if (INSERT_USER_ORGS.size() % batchSize == 0) {
                            jdbcTemplate.batchUpdate(INSERT_USER_SQL, INSERT_USER_ORGS);
                            jdbcTemplate.batchUpdate(INSERT_UD_SQL, INSERT_UD_ORGS);
                            jdbcTemplate.batchUpdate(INSERT_UR_SQL, INSERT_UR_ORGS);
                            jdbcTemplate.batchUpdate(INSERT_UDR_SQL, INSERT_UDR_ORGS);
                            INSERT_USER_ORGS.clear();
                            INSERT_UD_ORGS.clear();
                            INSERT_UR_ORGS.clear();
                            INSERT_UDR_ORGS.clear();
                            taskVo.setDoneCount(taskVo.getDoneCount() + batchSize);
                            flushCacheTaskVo(taskVo);
                            log.info("保存数据耗时：{}秒", (System.currentTimeMillis() - t) / 1000);
                            t = System.currentTimeMillis();
                        }
                    }
                    if (INSERT_USER_ORGS.size() > 0) {
                        jdbcTemplate.batchUpdate(INSERT_USER_SQL, INSERT_USER_ORGS);
                        jdbcTemplate.batchUpdate(INSERT_UD_SQL, INSERT_UD_ORGS);
                        jdbcTemplate.batchUpdate(INSERT_UR_SQL, INSERT_UR_ORGS);
                        jdbcTemplate.batchUpdate(INSERT_UDR_SQL, INSERT_UDR_ORGS);
                        taskVo.setDoneCount(taskVo.getDoneCount() + INSERT_USER_ORGS.size());
                        log.info("数据耗时：{}秒", (System.currentTimeMillis() - t) / 1000);
                    }
                    if (userMapper.countByDistinctColumn("username") != userMapper.countByColumn("username")) {
                        throw new RuntimeException("数据库中用户名存在重复，请联系管理员");
                    }
                    if (userMapper.countByDistinctColumn("idcard") != userMapper.countByColumn("idcard")) {
                        throw new RuntimeException("数据库中身份证号存在重复，请联系管理员");
                    }
                    taskVo.refresh(TaskResultBaseVo.SUCCESS, "用户导入成功", System.currentTimeMillis() - start);
                }
            } catch (Exception e) {
                taskVo.setFailCount(taskVo.getFailCount() + 1);
                String msg = e.getMessage();
                for (IndexUniqEnum uniqEnum : IndexUniqEnum.values()) {
                    if (e.getMessage().contains(uniqEnum.getUniqKey())) {
                        String value = msg.substring(msg.lastIndexOf("Duplicate entry '") + "Duplicate entry '".length(), msg.lastIndexOf("' for key"));
                        msg = uniqEnum.getUniqText() + value + "已存在";
                    }
                }
                taskVo.refresh(TaskResultBaseVo.FAIL, msg, System.currentTimeMillis() - start);
                log.error(e.getMessage(), e);
                throw e;
            } finally {
                flushCacheTaskVo(taskVo);
                redisUtil.del(CacheKeyConstants.USER_IMP_TASK_START);
                taskStatus.set(false);
            }
        }
    }

    @Override
    public TaskResultBaseVo getImportExcelResult() {
        TaskResultBaseVo vo = new TaskResultBaseVo();
        if (redisUtil.hasKey(CacheKeyConstants.USER_IMP_TASK)) {
            vo = (TaskResultBaseVo) redisUtil.get(CacheKeyConstants.USER_IMP_TASK);
        }
        return vo;
    }

    @Override
    public SysUserModel getUserModelByIdcard(String idcard) {
        SysUserModel model = new SysUserModel();
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserEntity::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(SysUserEntity::getIdcard, idcard);
        SysUserEntity one = this.getOne(wrapper);
        if (one != null) {
            BeanUtils.copyProperties(one, model);
        }
        return model;
    }

    @Override
    public SysUserModel getUserModelById(String id) {
        SysUserEntity user = userMapper.selectById(id);
        if (ObjectUtils.isEmpty(user)) {
            return null;
        }
        SysDepartEntity departEntity = departService.getById(user.getDepartId());
        SysUserModel model = new SysUserModel();
        BeanUtils.copyProperties(user, model);
        model.setDepartName(departEntity.getDepartName());
        return model;
    }

    @Override
    public SysUserModel getUserModelByZfzh(String zfzh) {
        return userMapper.getSysUserInfoModel(zfzh);
    }

    private void flushCacheTaskVo(TaskResultBaseVo taskVo) {
        redisUtil.set(CacheKeyConstants.USER_IMP_TASK, taskVo);
    }

    /**
     * 描述:  生成用户名
     * 版本: 1.0
     * 日期: 2021/3/23 16:11
     * 作者: suzc
     *
     * @param realName
     * @param idcard
     * @return java.lang.String
     */
    private String getUserName(String realName, String idcard, HashSet<String> userNameSet) {
        //姓
        String lastName = PinyinUtil.getPingYin(realName.substring(0, 1));
        //名
        String firstName = PinyinUtil.getPinYinHeadChar(realName.substring(1));
        String username = lastName + firstName + idcard.substring(idcard.length() - 4);
        String temp = username;
        int i = 0;
        while (checkUserNameExist(temp, userNameSet)) {
            if (i == XXZM.length) {
                throw new RuntimeException("自动生成用户名失败，姓名【"+realName+"】身份证号【"+idcard+"】");
            }
            temp = username + XXZM[i];
            i++;
        }
        return temp;
    }

    private boolean checkUserNameExist(String userName,HashSet<String> userNameSet){
        if(!CollectionUtils.isEmpty(userNameSet)) {
            if(userNameSet.contains(userName)) {
                return true;
            }else{
                userNameSet.add(userName);
                return false;
            }
        }else{
            if(getUserByName(userName) != null ) {
                return true;
            }
        }
        return false;
    }


    /**
     * 根据部门id查询该部门下用户，过滤调当前用户
     *
     * @param vo
     * @return
     */
    @Override
    public List<SysUserModel> queryUserForDepartNoCurrentUser(SysUserLikeVo vo) {
        String departId = LoginUtil.getLoginUser().getDepartVo().getId();
        if (StringUtil.isNotEmpty(vo.getCondition())) {
            vo.setCondition("%" + vo.getCondition() + "%");
        }
        List<SysUserModel> list = this.userMapper.queryUserForDepart(departId, vo, LoginUtil.getLoginUser().getId());
        if(!CollectionUtils.isEmpty(list)) {
            for(SysUserModel model : list) {
                model.setSn(Md5.encryptMD5(model.getId()+model.getDepartId()));
            }
            if(list.size()>50) {
                list = list.subList(0,50);
            }
        }
        return list;
    }

    @Override
    public List<SysUserSiteModel> loadUserListForUserSite() {
        LoginUserVo loginUserVo = LoginUtil.getLoginUser();
        String path = loginUserVo.getDepartVo().getPath();
        return userMapper.getUserListForPath(path);
    }

    @RedisLock
    @Transactional
    @Override
    public void initRealnamePy() {
        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.isNull(SysUserEntity::getRealnamePy).select(SysUserEntity::getId, SysUserEntity::getRealname);
        List<SysUserEntity> list = this.list(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            for (SysUserEntity user : list) {
                user.setRealnamePy(PinyinUtil.getPinYinHeadChar(user.getRealname()));
                this.userMapper.updateById(user);
            }
        }
    }

}
