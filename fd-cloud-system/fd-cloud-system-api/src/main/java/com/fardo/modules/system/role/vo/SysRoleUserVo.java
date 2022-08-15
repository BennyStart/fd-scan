package com.fardo.modules.system.role.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @(#)SysRoleUserVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/30 14:37
 * 描　述：
 */
@ApiModel("用户角色参数vo")
@Data
public class SysRoleUserVo {

    @NotBlank(message = "角色id不能为空")
    @ApiModelProperty(value = "角色id",required = true)
    private String roleId;
    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id，多个用英文逗号隔开", required = true)
    private String userIds;
}
