package com.fardo.modules.system.user.vo;

import com.fardo.common.system.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)SysUserParamVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/17 15:25
 * 描　述：
 */
@Data
public class SysUserParamVo extends PageVo {

    @ApiModelProperty(value="用户id")
    private String id;

    @ApiModelProperty(value="用户账号")
    private String username;

    @ApiModelProperty("用户名称")
    private String realname;

    @ApiModelProperty("警号")
    private String policeNo;

    @ApiModelProperty("身份证号码")
    private String idcard;

    @ApiModelProperty("机构id")
    private String departId;

    @ApiModelProperty("机构名称")
    private String departName;

    @ApiModelProperty("角色id")
    private String roleId;

    @ApiModelProperty("排除角色id，过滤掉有该角色的用户")
    private String excludeRoleId;
    @ApiModelProperty(value="手机号码")
    private String mobilephone;
}
