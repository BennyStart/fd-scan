package com.fardo.modules.system.sys.vo;

import com.fardo.modules.system.personal.vo.SysPersonalInfoVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)LoginResult <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/11 13:30
 * 描　述：
 */
@Data
public class LoginResult {
    @ApiModelProperty(value="用户信息")
    private UserVo user;
    @ApiModelProperty(value="用户部门信息")
    private DepartVo org;
    @ApiModelProperty(value = "是否需要确认角色")
    private Boolean checkRole;
    @ApiModelProperty(value = "用户角色默认常用地址")
    private SysPersonalInfoVo address;
}
