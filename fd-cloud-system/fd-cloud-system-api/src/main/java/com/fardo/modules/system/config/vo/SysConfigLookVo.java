package com.fardo.modules.system.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "系统参数配置接收参数", description = "系统参数配置接收参数")
public class SysConfigLookVo {

    @NotBlank(message = "主键不能为空")
    @ApiModelProperty(value = "主键")
    private String id;

}
