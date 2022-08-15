package com.fardo.modules.system.personal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.personal.dto.SysPersonalDTO;
import com.fardo.modules.system.personal.dto.SysPersonalSettingDTO;
import com.fardo.modules.system.personal.entity.SysPersonalSettingEntity;
import com.fardo.modules.system.personal.vo.SysPersonalInfoVo;
import com.fardo.modules.system.personal.vo.SysPersonalSettingVo;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/8/31-10:43
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
public interface ISysPersonalSettingService extends IService<SysPersonalSettingEntity> {

    boolean saveOrUpdate(SysPersonalSettingDTO data, String id);

    SysPersonalSettingVo getList(SysPersonalDTO data);

    SysPersonalInfoVo getSysPersonalInfo(String userId, String userRole, String deptId);
}
