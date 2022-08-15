package com.fardo.modules.system.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.role.entity.SysRoleDepartEntity;

/**
 * @(#)ISysRoleDepartService <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/31 13:40
 * 描　述：
 */
public interface ISysRoleDepartService extends IService<SysRoleDepartEntity> {

    /**
     * 保存角色数据权限范围/先删后增
     * @param roleId
     * @param departIds
     */
    public void saveRoleDepart(String roleId,String departIds);


}
