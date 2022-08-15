package com.fardo.modules.system.config.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @(#)UpdateConfigVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/22 9:55
 * 描　述：
 */
@Data
public class UpdateConfigByCodeVo {

    @NotBlank(message = "配置项代码不能为空")
    @ApiModelProperty("配置项代码")
    private String code;
    @NotBlank(message = "配置值不能为空")
    @ApiModelProperty("配置值")
    private String value;

}
