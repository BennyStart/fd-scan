package com.fardo.modules.system.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @(#)SysRoleDepartEntity <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/31 13:38
 * 描　述：
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_role_depart")
@NoArgsConstructor
public class SysRoleDepartEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 部门id
     */
    private String departId;

    /**
     * 是否包含下级机构
     */
    private Boolean containSubFlag;

    public SysRoleDepartEntity(String roleId, String departId, Boolean containSubFlag) {
        this.roleId = roleId;
        this.departId = departId;
        this.containSubFlag = containSubFlag;
    }
}
