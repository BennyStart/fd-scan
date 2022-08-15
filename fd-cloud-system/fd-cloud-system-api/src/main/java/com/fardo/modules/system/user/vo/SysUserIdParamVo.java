package com.fardo.modules.system.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)SysDepartVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/12 14:30
 * 描　述：
 */
@Data
@ApiModel(value="用户id信息")
public class SysUserIdParamVo {
    @ApiModelProperty(value="用户id")
    private String id;
}
