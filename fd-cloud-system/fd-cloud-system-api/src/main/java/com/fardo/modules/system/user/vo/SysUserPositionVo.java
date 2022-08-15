package com.fardo.modules.system.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @(#)SysUserPositionVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/13 14:04
 * 描　述：
 */
@Data
public class SysUserPositionVo {

    @NotBlank(message = "机构id不能为空")
    @ApiModelProperty(value="机构id")
    private String departId;

    @ApiModelProperty(value="机构名称")
    private String departName;

    @ApiModelProperty(value="角色id集合，用英文逗号隔开")
    private String roleIds;

    @ApiModelProperty(value="角色名称集合，用英文逗号隔开")
    private String roleNames;
}
