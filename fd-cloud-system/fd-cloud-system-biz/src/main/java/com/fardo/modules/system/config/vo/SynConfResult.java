package com.fardo.modules.system.config.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SynConfResult {

    @ApiModelProperty(value="配置项id")
    private String configId;
    @ApiModelProperty(value="配置项对应配置内容的md5")
    private String configMd5;
    @ApiModelProperty(value="配置项内容")
    private String content;
    @ApiModelProperty(value="配置项版本号")
    private String configVersion;


}
