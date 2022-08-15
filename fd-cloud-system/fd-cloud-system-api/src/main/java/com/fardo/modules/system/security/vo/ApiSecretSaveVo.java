package com.fardo.modules.system.security.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)ApiSecretSaveVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/8/5 15:03
 * 描　述：
 */
@Data
public class ApiSecretSaveVo {

    @ApiModelProperty("主键id，新增不传，修改必传")
    private String id;
    @ApiModelProperty("公司名称")
    private String gsmc;
    @ApiModelProperty("地址")
    private String dz;
    @ApiModelProperty("接入方应用类型")
    private String yylx;
    @ApiModelProperty("接入方应用名称")
    private String yymc;
    @ApiModelProperty("接入数上限")
    private Integer jrsx;
    @ApiModelProperty("并发数上限")
    private Integer bfsx;
    @ApiModelProperty("联系人")
    private String lxr;
    @ApiModelProperty("电话")
    private String dh;
    @ApiModelProperty("手机号")
    private String sj;
    @ApiModelProperty("email")
    private String email;

}
