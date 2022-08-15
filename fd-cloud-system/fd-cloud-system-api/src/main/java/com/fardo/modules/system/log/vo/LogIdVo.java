package com.fardo.modules.system.log.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @(#)LogQueryVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/18 13:59
 * 描　述：
 */
@Data
public class LogIdVo {

    @NotBlank(message = "主键id不能为空")
    @ApiModelProperty("主键id")
    private String id;

}
