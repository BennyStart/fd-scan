package com.fardo.modules.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.util.StringUtil;
import com.fardo.common.util.oConvertUtils;
import com.fardo.modules.system.role.entity.SysRoleDepartEntity;
import com.fardo.modules.system.role.mapper.SysRoleDepartMapper;
import com.fardo.modules.system.role.service.ISysRoleDepartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @(#)SysRoleDepartServiceImpl <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/31 13:42
 * 描　述：
 */
@Service
public class SysRoleDepartServiceImpl extends ServiceImpl<SysRoleDepartMapper, SysRoleDepartEntity> implements ISysRoleDepartService {

    @Override
    @Transactional
    public void saveRoleDepart(String roleId, String departIds) {
        LambdaQueryWrapper<SysRoleDepartEntity> query = new QueryWrapper<SysRoleDepartEntity>().lambda().eq(SysRoleDepartEntity::getRoleId, roleId);
        this.remove(query);
        if(StringUtil.isNotEmpty(departIds)) {
            List<SysRoleDepartEntity> list = new ArrayList<>();
            String[] arr = departIds.split(",");
            for (String p : arr) {
                if(oConvertUtils.isNotEmpty(p)) {
                    // TODO 是否包含下级待调整
                    SysRoleDepartEntity rolepms = new SysRoleDepartEntity(roleId, p, false);
                    list.add(rolepms);
                }
            }
            this.saveBatch(list);
        }
    }
}
