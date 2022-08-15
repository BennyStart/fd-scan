package com.fardo.modules.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fardo.modules.system.user.entity.SysUserDepartRolesEntity;
import com.fardo.modules.system.user.vo.UserDepartRoleDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserDepartRolesMapper extends BaseMapper<SysUserDepartRolesEntity>{

    /**
     * 删除用户部门角色关系
     * @param userId 用户id
     */
    void deleteByUserId(@Param("userId") String userId);

    void delete(@Param("userId") String userId,@Param("departId") String departId,@Param("roleId") String roleId);

    /**
     * 查询用户角色角色关系
     * @param userId 用户id
     * @return
     */
    List<UserDepartRoleDetailVo> selectByUserId(@Param("userId") String userId);
}
