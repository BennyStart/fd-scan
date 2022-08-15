package com.fardo.modules.system.user.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @(#)SysUserPasswordChangeVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/18 16:08
 * 描　述：
 */
@Data
@ApiModel("修改密码参数")
public class SysUserPasswordChangeVo {

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value="用户id",name="id", required = true)
    private String id;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value="密码",name="password", required = true)
    private String password;

    @NotBlank(message = "新密码不能为空")
    @ApiModelProperty(value="新密码",name="newPassword", required = true)
    private String newPassword;
}
