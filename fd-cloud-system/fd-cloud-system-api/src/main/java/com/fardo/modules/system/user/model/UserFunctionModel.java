package com.fardo.modules.system.user.model;

import com.fardo.modules.system.permission.model.SysPermissionModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @(#)UserFunctionModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/7/23 9:57
 * 描　述：
 */
@Data
public class UserFunctionModel {
    @ApiModelProperty("常用功能")
    private List<SysPermissionModel> commonList;
    @ApiModelProperty("业务功能")
    private List<SysPermissionModel> bizList;
    @ApiModelProperty("全部功能")
    private List<SysPermissionModel> list;
}
