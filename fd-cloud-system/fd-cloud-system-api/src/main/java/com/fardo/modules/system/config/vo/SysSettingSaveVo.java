package com.fardo.modules.system.config.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("客户端配置保存实体")
public class SysSettingSaveVo {


    @ApiModelProperty("主键id（新增不传，修改时必填）")
    private String id;
    @NotBlank(message = "配置项名称不能为空")
    @ApiModelProperty("配置项名称")
    private String proName;
    @NotBlank(message = "配置代码不能为空")
    @ApiModelProperty("配置")
    private String proKey;
    @NotBlank(message = "配置值不能为空")
    @ApiModelProperty("配置值")
    private String proValue;
    @ApiModelProperty("配置描述")
    private String remark;

}
