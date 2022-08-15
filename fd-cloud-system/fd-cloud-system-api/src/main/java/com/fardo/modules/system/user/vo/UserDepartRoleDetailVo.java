package com.fardo.modules.system.user.vo;

import lombok.Data;

/**
 * @(#)SysUserDepartRoleDetailVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/14 10:43
 * 描　述：用户部门角色关系详情
 */
@Data
public class UserDepartRoleDetailVo {
    private String id;
    /**用户id*/
    private String userId;
    /**部门id*/
    private String depId;
    /**角色id*/
    private String roleId;
    /**部门名称*/
    private String departName;
    /**角色名称*/
    private String roleName;
}
