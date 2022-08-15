package com.fardo.modules.system.role.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

/**
 * @(#)SysRoleVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/23 10:20
 * 描　述：
 */
@ApiModel("角色基本信息")
@Data
public class SysRoleVo {

    @ApiModelProperty(value = "id，新增不填")
    private String id;

    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(value = "角色名称",required = true)
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @ApiModelProperty(value = "角色编码",required = true)
    private String roleCode;

    @ApiModelProperty(value = "数据权限范围")
    private String dataAuthority;
}
