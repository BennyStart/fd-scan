package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @(#)LoginSSOVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/8/3 15:59
 * 描　述：
 */
@Data
public class LoginSsoVo {

    @NotBlank(message = "账号类型不能为空")
    @ApiModelProperty("账号类型，值字典：idcard-身份证号，policeNo-警号")
    private String idType;
    @NotBlank(message = "账号值不能为空")
    @ApiModelProperty("账号值，根据账号类型赋值")
    private String idValue;
    @ApiModelProperty("机构代码")
    private String orgCode;
    @ApiModelProperty("单点登录携带信息")
    private LoginSsoTagAjVo tag;


}
