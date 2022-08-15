package com.fardo.modules.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fardo.modules.system.user.entity.SysUserDepartEntity;
import org.apache.ibatis.annotations.Param;

public interface SysUserDepartMapper extends BaseMapper<SysUserDepartEntity>{

	/**
	 * 删除用户部门关系
	 * @param userId 用户id
	 */
	void deleteByUserId(@Param("userId") String userId);

}
