package com.fardo.modules.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fardo.modules.system.user.entity.SysUserPermissionEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户功能表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface SysUserPermissionMapper extends BaseMapper<SysUserPermissionEntity> {

    void deleteByUserId(@Param("userId") String userId);
}