package com.fardo.modules.system.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @(#)SysUserPasswordResetVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/24 13:52
 * 描　述：
 */
@Data
@ApiModel("重置密码参数")
public class SysUserPasswordResetVo {

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value="用户id",required = true)
    private String id;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "请输入6-18位的密码")
    @ApiModelProperty(value="密码", required = true)
    private String password;
}
