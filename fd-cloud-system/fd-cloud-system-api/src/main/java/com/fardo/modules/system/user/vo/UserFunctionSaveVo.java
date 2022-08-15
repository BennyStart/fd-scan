package com.fardo.modules.system.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)UserFunctionSaveVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/7/23 10:22
 * 描　述：
 */
@Data
public class UserFunctionSaveVo {
    @ApiModelProperty("常用功能id，用英文逗号隔开")
    private String commonIds;
    @ApiModelProperty("业务功能id，用英文逗号隔开")
    private String bizIds;
}
