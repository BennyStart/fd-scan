package com.fardo.modules.system.security.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @(#)ApiSecretPageModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/8/5 14:45
 * 描　述：
 */
@Data
@ApiModel("接入应用列表")
public class ApiSecretPageModel implements Serializable {

    @ApiModelProperty("主键id")
    private String id;
    @ApiModelProperty("接口key")
    private String apiKey;
    @ApiModelProperty("签名秘钥")
    private String apiSecret;
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
