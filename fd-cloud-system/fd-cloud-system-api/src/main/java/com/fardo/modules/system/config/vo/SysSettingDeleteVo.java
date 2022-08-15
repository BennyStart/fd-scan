package com.fardo.modules.system.config.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("客户端配置删除实体")
public class SysSettingDeleteVo {


    @NotBlank(message = "主键id不能为空")
    @ApiModelProperty("主键id，多个逗号隔开")
    private String ids;

}
