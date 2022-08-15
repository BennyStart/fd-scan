package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)LoginSSoInfoVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/8/4 13:31
 * 描　述：
 */
@Data
public class LoginSsoInfoVo {

    @ApiModelProperty("登录会话标识")
    private String sid;
    @ApiModelProperty("加密类型")
    private String encryptType;
    @ApiModelProperty("公钥")
    private String rsaPublicKey;
    @ApiModelProperty("随机数")
    private String randomKey;
    @ApiModelProperty("加密算法类型，0-RSA，1-SM2")
    private String randomKeyType;
    @ApiModelProperty("登录用户信息")
    private LoginResult loginInfo;
    @ApiModelProperty("携带的信息")
    private LoginSsoTagAjVo tag;

}
