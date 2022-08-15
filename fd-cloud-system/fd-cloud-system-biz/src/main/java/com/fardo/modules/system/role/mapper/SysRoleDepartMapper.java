package com.fardo.modules.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fardo.modules.system.depart.vo.SysDepartVo;
import com.fardo.modules.system.role.entity.SysRoleDepartEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @(#)SysRoleDepartMapper <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/31 13:41
 * 描　述：
 */
public interface SysRoleDepartMapper extends BaseMapper<SysRoleDepartEntity> {

    @Select("select * from t_sys_role_depart where role_id = #{roleId}")
    List<SysRoleDepartEntity> getListByRoleId(@Param("roleId") String roleId);

    List<SysDepartVo> getDepartInfoByRoleId(@Param("roleId") String roleId);
}
