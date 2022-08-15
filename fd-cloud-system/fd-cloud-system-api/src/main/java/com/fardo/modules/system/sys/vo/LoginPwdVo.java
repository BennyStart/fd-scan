package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value="登录请求对象", description="登录请求对象")
public class LoginPwdVo {

    @NotBlank(message="登录账号不能为空")
    @ApiModelProperty(value="登录账号")
    private String loginId;

    @NotBlank(message="密码不能为空")
    @ApiModelProperty(value="密码")
    private String password;

    @ApiModelProperty(value="登录来源")
    private String loginFrom;

}
