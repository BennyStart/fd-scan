package com.fardo.modules.system.security.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @(#)ApiSecretIdsVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/8/5 15:33
 * 描　述：
 */
@Data
public class ApiSecretIdsVo {

    @NotBlank(message = "主键id不能为空")
    @ApiModelProperty("主键id，多个逗号隔开")
    private String ids;

}
