package com.fardo.modules.system.role.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)SysUserRolesModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/6/3 14:48
 * 描　述：
 */
@Data
@ApiModel("用户角色信息")
public class SysUserRolesModel {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("用户名")
    private String name;
    @ApiModelProperty("所在部门id")
    private String departId;
    @ApiModelProperty("所在部门名称")
    private String departName;
    @ApiModelProperty("角色id")
    private String roleId;
    @ApiModelProperty("角色名")
    private String roleName;
    @ApiModelProperty("是否选中")
    private Boolean checked;
}
