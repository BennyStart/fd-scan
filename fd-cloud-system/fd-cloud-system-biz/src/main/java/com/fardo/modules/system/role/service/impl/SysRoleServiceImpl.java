package com.fardo.modules.system.role.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.api.vo.Result;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.exception.ApiException;
import com.fardo.common.system.base.entity.BaseEntity;
import com.fardo.common.util.ImportExcelUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.depart.vo.SysDepartVo;
import com.fardo.modules.system.role.entity.SysRoleDepartEntity;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.modules.system.role.entity.SysRolePermissionEntity;
import com.fardo.modules.system.role.enums.DataScopeEnum;
import com.fardo.modules.system.role.enums.RoleResultCodeEnum;
import com.fardo.modules.system.role.mapper.SysRoleDepartMapper;
import com.fardo.modules.system.role.mapper.SysRoleMapper;
import com.fardo.modules.system.role.mapper.SysRolePermissionMapper;
import com.fardo.modules.system.role.model.SysCustomDepartModel;
import com.fardo.modules.system.role.model.SysDataPermissionModel;
import com.fardo.modules.system.role.model.SysRoleModel;
import com.fardo.modules.system.role.service.ISysRoleDepartService;
import com.fardo.modules.system.role.service.ISysRolePermissionService;
import com.fardo.modules.system.role.service.ISysRoleService;
import com.fardo.modules.system.role.vo.SysRoleParamVo;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import com.fardo.modules.system.user.mapper.SysUserMapper;
import com.fardo.modules.system.user.mapper.SysUserRoleMapper;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRoleEntity> implements ISysRoleService {
    @Resource
    SysRoleMapper sysRoleMapper;
    @Resource
    SysUserMapper sysUserMapper;
    @Resource
    SysRolePermissionMapper sysRolePermissionMapper;
    @Resource
    SysUserRoleMapper sysUserRoleMapper;
    @Resource
    SysRoleDepartMapper sysRoleDepartMapper;
    @Autowired
    ISysRolePermissionService sysRolePermissionService;
    @Autowired
    ISysRoleDepartService sysRoleDepartService;

    @Override
    public Result importExcelCheckRoleCode(MultipartFile file, ImportParams params) throws Exception {
        List<Object> listSysRoles = ExcelImportUtil.importExcel(file.getInputStream(), SysRoleEntity.class, params);
        int totalCount = listSysRoles.size();
        List<String> errorStrs = new ArrayList<>();

        // 去除 listSysRoles 中重复的数据
        for (int i = 0; i < listSysRoles.size(); i++) {
            String roleCodeI =((SysRoleEntity)listSysRoles.get(i)).getRoleCode();
            for (int j = i + 1; j < listSysRoles.size(); j++) {
                String roleCodeJ =((SysRoleEntity)listSysRoles.get(j)).getRoleCode();
                // 发现重复数据
                if (roleCodeI.equals(roleCodeJ)) {
                    errorStrs.add("第 " + (j + 1) + " 行的 roleCode 值：" + roleCodeI + " 已存在，忽略导入");
                    listSysRoles.remove(j);
                    break;
                }
            }
        }
        // 去掉 sql 中的重复数据
        Integer errorLines=0;
        Integer successLines=0;
        List<String> list = ImportExcelUtil.importDateSave(listSysRoles, ISysRoleService.class, errorStrs, CommonConstant.SQL_INDEX_UNIQ_SYS_ROLE_CODE);
         errorLines+=list.size();
         successLines+=(listSysRoles.size()-errorLines);
        return ImportExcelUtil.imporReturnRes(errorLines,successLines,list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(String roleid) {
        if(sysUserRoleMapper.countByRoleId(roleid) > 0) {
            throw new ApiException("101","删除失败，该角色下存在用户，请将用户转移之后再进行删除操作");
        }
        //2.删除角色和权限关系
        sysRoleMapper.deleteRolePermissionRelation(roleid);
        //3.删除角色
        this.removeById(roleid);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchRole(String[] roleIds) {
        //1.删除角色和用户关系
        sysUserMapper.deleteBathRoleUserRelation(roleIds);
        //2.删除角色和权限关系
        sysUserMapper.deleteBathRolePermissionRelation(roleIds);
        //3.删除角色
        this.removeByIds(Arrays.asList(roleIds));
        return true;
    }

    @Override
    public SysRoleModel getRoleDetail(String roleId) {
        SysRoleModel sysRoleModel = new SysRoleModel();
        if(!StringUtils.isEmpty(roleId)) {
            SysRoleEntity sysRole = this.getById(roleId);
            if(sysRole != null) {
                BeanUtils.copyProperties(sysRole,sysRoleModel);
                LambdaQueryWrapper<SysRolePermissionEntity> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SysRolePermissionEntity::getRoleId, roleId);
                List<SysRolePermissionEntity> rpList = sysRolePermissionMapper.selectList(queryWrapper);
                if(!CollectionUtils.isEmpty(rpList)) {
                    sysRoleModel.setPermissionIds(rpList.stream().map(SysRolePermissionEntity::getPermissionId).collect(Collectors.joining(",")));
                }
                List<SysDepartVo> rdList = sysRoleDepartMapper.getDepartInfoByRoleId(roleId);
                if(!CollectionUtils.isEmpty(rdList)) {
                    sysRoleModel.setDepartIds(rdList.stream().map(SysDepartVo::getId).collect(Collectors.joining(",")));
                    sysRoleModel.setDepartNames(rdList.stream().map(SysDepartVo::getDepartName).collect(Collectors.joining(",")));
                }
            }
        }
        return sysRoleModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleAuth(SysRoleModel sysRoleModel) {
        if(!validRoleCode(sysRoleModel.getRoleCode(),sysRoleModel.getId())) {
            throw new ApiException(RoleResultCodeEnum.ROLE_CODE_EXIST);
        }
        if(!validRoleName(sysRoleModel.getRoleName(),sysRoleModel.getId())) {
            throw new ApiException(RoleResultCodeEnum.ROLE_NAME_EXIST);
        }
        SysRoleEntity sysRole = this.getById(sysRoleModel.getId());
        if(sysRole != null) {
            if(DataScopeEnum.CUSTOM.getCode().equals(sysRoleModel.getDataAuthority())) {
                sysRoleDepartService.saveRoleDepart(sysRoleModel.getId(), sysRoleModel.getDepartIds());
            }else{
                if(DataScopeEnum.CUSTOM.getCode().equals(sysRole.getDataAuthority())) {
                    LambdaQueryWrapper<SysRoleDepartEntity> query = new QueryWrapper<SysRoleDepartEntity>().lambda().eq(SysRoleDepartEntity::getRoleId, sysRole.getId());
                    sysRoleDepartService.remove(query);
                }
            }
            sysRolePermissionService.saveRolePermission(sysRoleModel.getId(),sysRoleModel.getPermissionIds());
            sysRole.setRoleCode(sysRoleModel.getRoleCode());
            sysRole.setRoleName(sysRoleModel.getRoleName());
            sysRole.setDataAuthority(sysRoleModel.getDataAuthority());
            this.updateById(sysRole);
            return true;
        }
        return false;
    }

    @Override
    public IPage<SysRoleEntity> queryList(SysRoleParamVo roleVo) {
        LambdaQueryWrapper<SysRoleEntity> queryWrapper = new QueryWrapper<SysRoleEntity>().lambda();
        queryWrapper.eq(SysRoleEntity::getShowFlag, CommonConstant.DEL_FLAG_1);
        if(!StringUtils.isEmpty(roleVo.getDataAuthority())) {
            queryWrapper.eq(SysRoleEntity::getDataAuthority, roleVo.getDataAuthority());
        }
        if(!StringUtils.isEmpty(roleVo.getRoleCode())) {
           queryWrapper.like(SysRoleEntity::getRoleCode, roleVo.getRoleCode());
        }
        if(!StringUtils.isEmpty(roleVo.getRoleName())) {
            queryWrapper.like(SysRoleEntity::getRoleName, roleVo.getRoleName());
        }
        Page<SysRoleEntity> page = new Page<SysRoleEntity>(roleVo.getPageNo(), roleVo.getPageSize());
        this.page(page, queryWrapper);
        return page;
    }

    @Override
    @Transactional
    public SysRoleModel saveRoleAuth(SysRoleModel sysRoleModel) {
        SysRoleEntity role = new SysRoleEntity();
        if(!validRoleCode(sysRoleModel.getRoleCode(),sysRoleModel.getId())) {
            throw new ApiException(RoleResultCodeEnum.ROLE_CODE_EXIST);
        }
        if(!validRoleName(sysRoleModel.getRoleName(),sysRoleModel.getId())) {
            throw new ApiException(RoleResultCodeEnum.ROLE_NAME_EXIST);
        }
        role.setRoleName(sysRoleModel.getRoleName());
        role.setRoleCode(sysRoleModel.getRoleCode());
        role.setDataAuthority(sysRoleModel.getDataAuthority());
        this.save(role);
        sysRoleModel.setId(role.getId());
        if(DataScopeEnum.CUSTOM.getCode().equals(sysRoleModel.getDataAuthority())) {
            sysRoleDepartService.saveRoleDepart(sysRoleModel.getId(), sysRoleModel.getDepartIds());
        }
        sysRolePermissionService.saveRolePermission(sysRoleModel.getId(),sysRoleModel.getPermissionIds());
        return sysRoleModel;
    }

    @Override
    public SysDataPermissionModel getUserDataScope() {
        LoginUserVo loginUserVo = LoginUtil.getLoginUser();
        return getUserDataScope(loginUserVo);
    }

    @Override
    public SysDataPermissionModel getUserDataScope(LoginUserVo loginUserVo) {
        //获取当前登录用户
        if(loginUserVo != null && StringUtil.isNotEmpty(loginUserVo.getCurRoleId())) {
            //获取用户当前登录角色
            SysRoleEntity role = this.sysRoleMapper.selectById(loginUserVo.getCurRoleId());
            if(role == null || StringUtil.isEmpty(role.getDataAuthority())) {
                return null;
            }
            SysDataPermissionModel model = new SysDataPermissionModel();
            //自定义数据范围
            if(DataScopeEnum.CUSTOM.getCode().equals(role.getDataAuthority())) {
                List<SysRoleDepartEntity> rdList = sysRoleDepartMapper.getListByRoleId(role.getId());
                if(!CollectionUtils.isEmpty(rdList)) {
                    List<SysCustomDepartModel> customDepartModels = new ArrayList<>(rdList.size());
                    for (SysRoleDepartEntity rd : rdList) {
                        if(rd.getContainSubFlag()) {
                            customDepartModels.add(new SysCustomDepartModel(DataScopeEnum.DEPART_WITH_SUB.getCode(),rd.getDepartId()));
                        }else{
                            customDepartModels.add(new SysCustomDepartModel(DataScopeEnum.DEPART.getCode(),rd.getDepartId()));
                        }
                    }
                    model.setCustomDepartModels(customDepartModels);
                }
            }
            model.setDataScope(role.getDataAuthority());
            return model;
        }
        return null;
    }

    /**
     * 角色编码是否重复
     * @param code
     * @param id
     * @return
     */
    private boolean validRoleCode(String code, String id) {
        LambdaQueryWrapper<SysRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleEntity::getRoleCode, code);
        if(StringUtil.isNotEmpty(id)) {
            queryWrapper.notIn(BaseEntity::getId,id);
        }
        List<SysRoleEntity> list = this.sysRoleMapper.selectList(queryWrapper);
        if(CollectionUtil.isNotEmpty(list)) {
            return false;
        }
        return true;
    }

    /**
     * 角色名称是否重复
     * @param name
     * @param id
     * @return
     */
    private boolean validRoleName(String name, String id) {
        LambdaQueryWrapper<SysRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleEntity::getRoleName, name);
        if(StringUtil.isNotEmpty(id)) {
            queryWrapper.notIn(BaseEntity::getId,id);
        }
        List<SysRoleEntity> list = this.sysRoleMapper.selectList(queryWrapper);
        if(CollectionUtil.isNotEmpty(list)) {
            return false;
        }
        return true;
    }
}
