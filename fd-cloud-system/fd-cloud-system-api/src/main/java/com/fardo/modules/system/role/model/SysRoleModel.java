package com.fardo.modules.system.role.model;

import com.fardo.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @(#)SysRoleModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/22 15:51
 * 描　述：
 */
@ApiModel("角色权限详情")
@Data
public class SysRoleModel {

    @ApiModelProperty(value = "id")
    private String id;

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(value = "角色名称",required = true)
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @ApiModelProperty(value = "角色编码",required = true)
    private String roleCode;

    @Dict(dicCode = "DATA_SCOPE")
    @ApiModelProperty(value = "数据权限范围",required = true)
    private String dataAuthority;

    @ApiModelProperty(value = "菜单功能id集合，英文逗号隔开")
    private String permissionIds;

    @ApiModelProperty(value = "部门id集合，英文逗号隔开")
    private String departIds;

    @ApiModelProperty(value = "部门名称集合，英文逗号隔开")
    private String departNames;
}
