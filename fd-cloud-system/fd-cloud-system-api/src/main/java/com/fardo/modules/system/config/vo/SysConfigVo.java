package com.fardo.modules.system.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "系统参数配置接收参数", description = "系统参数配置接收参数")
public class SysConfigVo {
    @ApiModelProperty(value = "主键")
    private String id;

    @NotBlank(message = "参数名称不能为空")
    @ApiModelProperty(value = "参数名称",required = true)
    private String name;

    @NotBlank(message = "参数代码不能为空")
    @ApiModelProperty(value = "参数代码",required = true)
    private String code;

    @ApiModelProperty(value = "参数类型")
    private String type;

    @ApiModelProperty(value = "参数值")
    private String value;

    @ApiModelProperty(value = "参数描述")
    private String remark;


}
