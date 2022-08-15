package com.fardo.modules.system.user.model;

import com.fardo.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @(#)SysUser <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/17 17:19
 * 描　述：
 */
@Data
@ApiModel("用户信息")
public class SysUserModel implements Serializable {
    @ApiModelProperty(value="序列号",name="序列号")
    private String sn;
    @ApiModelProperty(value="用户id",name="用户id")
    private String id;
    @ApiModelProperty(value="用户名",name="username")
    private String username;
    @ApiModelProperty(value="姓名",name="realname")
    private String realname;
    @ApiModelProperty(value="部门id",name="departId")
    private String departId;
    @ApiModelProperty(value="部门名称",name="departName")
    private String departName;
    @ApiModelProperty(value="警号",name="policeNo")
    private String policeNo;
    @ApiModelProperty(value="身份证号",name="idcard")
    private String idcard;
    @ApiModelProperty(value="性别（1-男，2-女）",name="sex")
    private String sex;
    @ApiModelProperty(value="手机号码")
    private String mobilephone;

}
