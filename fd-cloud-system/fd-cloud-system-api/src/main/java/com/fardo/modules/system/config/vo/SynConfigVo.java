package com.fardo.modules.system.config.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SynConfigVo {

    @NotBlank(message = "配置项id不能为空")
    @ApiModelProperty(value="配置项id")
    private String configId;

    @ApiModelProperty(value="配置项对应配置内容的md5")
    private String configMd5;

}
