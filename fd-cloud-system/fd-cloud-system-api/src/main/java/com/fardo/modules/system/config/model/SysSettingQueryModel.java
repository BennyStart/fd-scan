package com.fardo.modules.system.config.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("客户端配置分页查询结果")
public class SysSettingQueryModel {


    @ApiModelProperty("主键id")
    private String id;
    @ApiModelProperty("配置项名称")
    private String proName;
    @ApiModelProperty("配置")
    private String proKey;
    @ApiModelProperty("配置值")
    private String proValue;
    @ApiModelProperty("配置描述")
    private String remark;

}
