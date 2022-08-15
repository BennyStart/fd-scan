package com.fardo.modules.system.role.mapper;

import com.fardo.modules.system.role.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface SysRoleMapper extends BaseMapper<SysRoleEntity> {

    /**
     * @Author scott
     * @Date 2019/12/13 16:12
     * @Description: 删除角色与用户关系
     */
    @Delete("delete from t_sys_user_role where role_id = #{roleId}")
    void deleteRoleUserRelation(@Param("roleId") String roleId);


    /**
     * @Author scott
     * @Date 2019/12/13 16:12
     * @Description: 删除角色与权限关系
     */
    @Delete("delete from t_sys_role_permission where role_id = #{roleId}")
    void deleteRolePermissionRelation(@Param("roleId") String roleId);

    @Select("select * from t_sys_role where id in(select ur.role_id from t_sys_user_role ur where ur.user_id = #{userId} and ur.role_id in (select rp.role_id from t_sys_role_permission rp where rp.permission_id = (select p.id from t_sys_permission p where p.url = #{url})))")
    List<SysRoleEntity> getRoles(@Param("userId") String userId, @Param("url") String url);

    @Select("select * from t_sys_role where id in(select ur.role_id from t_sys_user_role ur where ur.user_id = #{userId})")
    List<SysRoleEntity> getRoles(@Param("userId") String userId);

    @Select("select * from t_sys_role where role_code = #{roleCode}")
    SysRoleEntity getRoleByCode(@Param("roleCode") String roleCode);

}
