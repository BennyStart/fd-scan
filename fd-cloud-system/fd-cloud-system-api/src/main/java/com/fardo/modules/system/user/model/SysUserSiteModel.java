package com.fardo.modules.system.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @(#)SysUserSiteModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/17 17:19
 * 描　述：
 */
@Data
@ApiModel("站点用户信息")
public class SysUserSiteModel implements Serializable {
    @ApiModelProperty(value="id",name="id")
    private String id;
    @ApiModelProperty(value="用户名",name="username")
    private String username;
    @ApiModelProperty(value="姓名",name="realname")
    private String realname;
    @ApiModelProperty(value="姓名拼音首字母",name="realnamePy")
    private String realnamePy;
    @ApiModelProperty(value="警号",name="policeNo")
    private String policeNo;
    @ApiModelProperty(value="身份证号",name="idcard")
    private String idcard;
    @ApiModelProperty(value="手机号",name="mobilephone")
    private String mobilephone;
    @ApiModelProperty(value="密码",name="password")
    private String password;
    @ApiModelProperty(value="部门id",name="departId")
    private String departId;
    @ApiModelProperty(value="部门名称",name="departName")
    private String departName;
    @ApiModelProperty(value="部门代码",name="orgCode")
    private String orgCode;
    @ApiModelProperty(value="部门行政区划代码",name="areaCode")
    private String areaCode;
    @ApiModelProperty(value="部门名称拼音首字母",name="departNamePinyinAbbr")
    private String departNamePinyinAbbr;
    @ApiModelProperty(value="系统编码",name="sysCode")
    private String sysCode;
    @ApiModelProperty(value="部门路径",name="path")
    private String path;
    @ApiModelProperty(value="机构类型",name="orgType")
    private String orgType;
}
