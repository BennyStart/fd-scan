package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)LoginSSOTagVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/8/3 16:02
 * 描　述：
 */
@Data
public class LoginSsoTagZfryVo {

    @ApiModelProperty("执法人员2姓名")
    private String zfry2xm;
    @ApiModelProperty("执法人员2执法证号")
    private String zfry2zfzh;
    @ApiModelProperty("执法人员2身份证号")
    private String zfry2sfzh;
    @ApiModelProperty("执法人员2ID")
    private String zfry2id;
    @ApiModelProperty("执法人员2单位")
    private String zfry2dw;

}
