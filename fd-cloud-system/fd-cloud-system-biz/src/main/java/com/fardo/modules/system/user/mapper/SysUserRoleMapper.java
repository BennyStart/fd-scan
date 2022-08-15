package com.fardo.modules.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fardo.modules.system.role.vo.SysRoleVo;
import com.fardo.modules.system.user.entity.SysUserRoleEntity;
import com.fardo.modules.system.user.vo.SysUserRoleVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRoleEntity> {

	@Select("select role_code from sys_role where id in (select role_id from sys_user_role where user_id = (select id from sys_user where username=#{username}))")
	List<String> getRoleByUserName(@Param("username") String username);

	@Select("select id from sys_role where id in (select role_id from sys_user_role where user_id = (select id from sys_user where username=#{username}))")
	List<String> getRoleIdByUserName(@Param("username") String username);

	List<SysUserRoleVo> getRoleInfoByUserIds(@Param("userIds") List<String> userIds);

    @Select("select id,role_code,role_name,data_authority from t_sys_role where id in (select role_id from t_sys_user_role where user_id = #{userId})")
    List<SysRoleVo> getRoleEntityByUserId(@Param("userId") String userId);

    @Select("select count(*) from t_sys_user_role where role_id = #{roleId}")
    int countByRoleId(@Param("roleId") String roleId);

    @Delete("delete from t_sys_user_role where user_id = #{userId}")
    void deleteByUserId(@Param("userId") String userId);
}