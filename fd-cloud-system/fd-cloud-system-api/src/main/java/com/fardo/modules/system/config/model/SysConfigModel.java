package com.fardo.modules.system.config.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("系统参数配置信息")
public class SysConfigModel implements Serializable {

    private static final long serialVersionUID = 7206050848218015407L;
    @ApiModelProperty(value="主键")
    private String id;

    @ApiModelProperty(value="参数名称")
    private String name;

    @ApiModelProperty(value="参数主键")
    private String code;

    @ApiModelProperty(value="参数类型")
    private String type;


    @ApiModelProperty(value="参数值")
    private String value;

    @ApiModelProperty(value="参数描述")
    private String remark;
}
