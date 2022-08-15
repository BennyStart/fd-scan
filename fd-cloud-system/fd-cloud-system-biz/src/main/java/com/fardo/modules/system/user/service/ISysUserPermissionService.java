package com.fardo.modules.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.user.entity.SysUserPermissionEntity;
import com.fardo.modules.system.user.model.UserFunctionModel;
import com.fardo.modules.system.user.vo.UserFunctionSaveVo;

/**
 * <p>
 * 用户功能表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysUserPermissionService extends IService<SysUserPermissionEntity> {

    UserFunctionModel getUserFunction();

    void saveUserFunction(UserFunctionSaveVo vo);
}
