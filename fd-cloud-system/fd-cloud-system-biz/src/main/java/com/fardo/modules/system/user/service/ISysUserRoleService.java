package com.fardo.modules.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.role.model.SysUserRolesModel;
import com.fardo.modules.system.user.enums.PermissionEnum;
import com.fardo.modules.system.role.vo.SysRoleVo;
import com.fardo.modules.system.user.entity.SysUserRoleEntity;
import com.fardo.modules.system.user.vo.DataScopeVo;

import java.util.List;

/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysUserRoleService extends IService<SysUserRoleEntity> {

    List<SysRoleVo> getRoleEntityByUserId(String userId);

    void saveRoleUsers(String roleId, String userIds);

    void deleteRoleUsers(String roleId, String userIds);

    DataScopeVo getDataScope(String userId, PermissionEnum permissionEnum);

    DataScopeVo getDataScope(String userId);

    /**
     * 获取用户部门角色信息
     * @return
     */
    List<SysUserRolesModel> getRolesForLoginUser();

    void deleteByUserId(String userId);

    boolean checkRoleDepartExit(String userId, String departId, String roleId);
}
