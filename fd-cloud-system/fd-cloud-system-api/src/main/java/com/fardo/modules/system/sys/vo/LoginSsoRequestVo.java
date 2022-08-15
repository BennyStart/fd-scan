package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @(#)LoginSsoRequestVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/8/4 13:44
 * 描　述：
 */
@Data
public class LoginSsoRequestVo {

    @NotBlank(message = "requestId不能为空")
    @ApiModelProperty("获取登录信息id")
    private String requestId;

}
