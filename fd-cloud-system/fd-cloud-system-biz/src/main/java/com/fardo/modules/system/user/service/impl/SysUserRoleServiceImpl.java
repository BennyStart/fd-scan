package com.fardo.modules.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.system.base.entity.BaseEntity;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.mapper.SysDepartMapper;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.modules.system.role.enums.DataScopeEnum;
import com.fardo.modules.system.role.mapper.SysRoleMapper;
import com.fardo.modules.system.role.model.SysUserRolesModel;
import com.fardo.modules.system.role.vo.SysRoleVo;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import com.fardo.modules.system.user.entity.SysUserDepartRolesEntity;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.entity.SysUserRoleEntity;
import com.fardo.modules.system.user.enums.PermissionEnum;
import com.fardo.modules.system.user.mapper.SysUserDepartRolesMapper;
import com.fardo.modules.system.user.mapper.SysUserMapper;
import com.fardo.modules.system.user.mapper.SysUserRoleMapper;
import com.fardo.modules.system.user.service.ISysUserRoleService;
import com.fardo.modules.system.user.vo.DataScopeVo;
import com.fardo.modules.system.user.vo.UserDepartRoleDetailVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRoleEntity> implements ISysUserRoleService {

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysDepartMapper departMapper;
    @Resource
    private SysUserDepartRolesMapper userDepartRolesMapper;
    @Resource
    private SysUserMapper userMapper;

    @Override
    public List<SysRoleVo> getRoleEntityByUserId(String userId) {
        return sysUserRoleMapper.getRoleEntityByUserId(userId);
    }

    @Override
    public void saveRoleUsers(String roleId, String userIds) {
        //角色id或者用户id不能为空
        if(StringUtil.isEmpty(roleId) || StringUtil.isEmpty(userIds)) {
            return;
        }
        //查找已经包含该角色的用户
        List<String> list = Arrays.asList(userIds.split(","));
        LambdaQueryWrapper<SysUserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRoleEntity::getRoleId,roleId);
        queryWrapper.in(SysUserRoleEntity::getUserId, list);
        queryWrapper.select(SysUserRoleEntity::getUserId);
        List<SysUserRoleEntity> existUrs = sysUserRoleMapper.selectList(queryWrapper);
        //排查已经拥有该角色的用户
        if(!CollectionUtils.isEmpty(existUrs)) {
            List<String> existUserIds = existUrs.stream().map(SysUserRoleEntity::getUserId).collect(Collectors.toList());
            list =list.stream().filter(userId -> !existUserIds.contains(userId)).collect(Collectors.toList());
        }
        //保存用户和角色的关系
        for(String userId : list) {
            SysUserRoleEntity userRole = new SysUserRoleEntity(userId, roleId);
            sysUserRoleMapper.insert(userRole);
        }
        //保存用户部门角色的关系
        LambdaQueryWrapper<SysUserEntity> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(BaseEntity::getId, list);
        userLambdaQueryWrapper.select(BaseEntity::getId,SysUserEntity::getDepartId);
        List<SysUserEntity> userList = userMapper.selectList(userLambdaQueryWrapper);
        for(SysUserEntity user : userList){
            SysUserDepartRolesEntity userDepartRoles = new SysUserDepartRolesEntity(user.getId(),user.getDepartId(),roleId);
            userDepartRolesMapper.insert(userDepartRoles);
        }
    }

    @Override
    public void deleteRoleUsers(String roleId, String userIds) {
        //角色id或者用户id不能为空
        if(StringUtil.isEmpty(roleId) || StringUtil.isEmpty(userIds)) {
            return;
        }
        //删除用户角色关系
        List<String> list = Arrays.asList(userIds.split(","));
        LambdaQueryWrapper<SysUserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRoleEntity::getRoleId,roleId);
        queryWrapper.in(SysUserRoleEntity::getUserId, list);
        this.remove(queryWrapper);
        //删除用户部门角色关系
        LambdaQueryWrapper<SysUserEntity> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(BaseEntity::getId, list);
        userLambdaQueryWrapper.select(BaseEntity::getId,SysUserEntity::getDepartId);
        List<SysUserEntity> userList = userMapper.selectList(userLambdaQueryWrapper);
        for(SysUserEntity user : userList){
            userDepartRolesMapper.delete(user.getId(),user.getDepartId(),roleId);
        }
    }

    @Override
    public DataScopeVo getDataScope(String userId, PermissionEnum permissionEnum) {
        List<SysRoleEntity> roles = roleMapper.getRoles(userId, permissionEnum.getCode());
        SysDepartEntity depart = departMapper.getUserMasterDepart(userId);
        return countDataScope(roles,depart);
    }

    @Override
    public DataScopeVo getDataScope(String userId) {
        List<SysRoleEntity> roles = roleMapper.getRoles(userId);
        SysDepartEntity depart = departMapper.getUserMasterDepart(userId);
        return countDataScope(roles,depart);
    }

    private DataScopeVo countDataScope(List<SysRoleEntity> roles, SysDepartEntity depart) {
        DataScopeVo dataScopeVo = new DataScopeVo();
        Set<String> specifyPaths = new HashSet<>();
        List<String> roleIds = new ArrayList<>();
        //判断是否有全部数据
        for(SysRoleEntity r : roles) {
            if(DataScopeEnum.DEPART_WITH_SUB.getCode().equals(r.getDataAuthority())) {
                if(depart != null) {
                    dataScopeVo.setLikePath(depart.getPath());
                }
            }else if(DataScopeEnum.DEPART.getCode().equals(r.getDataAuthority())) {
                if(depart != null) {
                    specifyPaths.add(depart.getPath());
                }
            }else if(DataScopeEnum.DEFAULT.getCode().equals(r.getDataAuthority())) {
                dataScopeVo.setChargeArea(true);
            }else if(DataScopeEnum.CUSTOM.getCode().equals(r.getDataAuthority())) {
                roleIds.add(r.getId());
            }
        }
        if(roleIds.size() > 0) {
            List<String> paths = departMapper.getPahtByRoleIds(roleIds);
            specifyPaths.addAll(paths);
        }
        if(StringUtil.isNotEmpty(dataScopeVo.getLikePath())) {
            dataScopeVo.setSpecifyPaths(specifyPaths.stream().filter(s -> !s.startsWith(dataScopeVo.getLikePath())).collect(Collectors.toList()));
            dataScopeVo.setLikePath(dataScopeVo.getLikePath() + "%");
        }else{
            List<String> list = new ArrayList<>(specifyPaths);
            dataScopeVo.setSpecifyPaths(list);
        }
        return dataScopeVo;
    }

    @Override
    public List<SysUserRolesModel> getRolesForLoginUser() {
        LoginUserVo loginUserVo = LoginUtil.getLoginUser();
        List<UserDepartRoleDetailVo> udrList = userDepartRolesMapper.selectByUserId(loginUserVo.getId());
        if(!CollectionUtils.isEmpty(udrList)) {
            List<SysUserRolesModel> list = new ArrayList<>(udrList.size());
            for(UserDepartRoleDetailVo vo : udrList) {
                SysUserRolesModel model = new SysUserRolesModel();
                model.setId(vo.getId());
                model.setDepartId(vo.getDepId());
                model.setDepartName(vo.getDepartName());
                model.setRoleId(vo.getRoleId());
                model.setRoleName(vo.getRoleName());
                model.setName(loginUserVo.getRealname());
                model.setChecked(false);
                //登录用户的登录部门id和角色id都匹配，说明用户之前默认该部门角色登录
                if(vo.getRoleId().equals(loginUserVo.getCurRoleId()) && vo.getDepId().equals(loginUserVo.getCurDepartId())) {
                    model.setChecked(true);
                }
                list.add(model);
            }
            return  list;
        }
        return null;
    }

    @Override
    public void deleteByUserId(String userId) {
        baseMapper.deleteByUserId(userId);
    }

    @Override
    public boolean checkRoleDepartExit(String userId, String departId, String roleId) {
        LambdaQueryWrapper<SysUserDepartRolesEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUserDepartRolesEntity::getDepId, departId);
        lambdaQueryWrapper.eq(SysUserDepartRolesEntity::getRoleId, roleId);
        lambdaQueryWrapper.eq(SysUserDepartRolesEntity::getUserId, userId);
        List<SysUserDepartRolesEntity> list = userDepartRolesMapper.selectList(lambdaQueryWrapper);
        if(CollectionUtils.isEmpty(list)){
            return false;
        }
        return true;
    }
}
