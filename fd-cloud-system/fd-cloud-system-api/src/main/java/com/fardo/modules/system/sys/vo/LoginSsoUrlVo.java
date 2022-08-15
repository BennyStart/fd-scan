package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)LoginSSOVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/8/4 15:59
 * 描　述：
 */
@Data
public class LoginSsoUrlVo {

    @ApiModelProperty("单点登录前端地址")
    private String ssoUrl;


}
