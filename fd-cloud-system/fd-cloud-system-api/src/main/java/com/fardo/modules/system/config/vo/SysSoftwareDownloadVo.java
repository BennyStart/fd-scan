package com.fardo.modules.system.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="软件下载下载配置参数", description="软件下载下载配置条件查询参数")
public class SysSoftwareDownloadVo {


    @ApiModelProperty(value="下载名称")
    private String  downloadName;
    @ApiModelProperty(value="下载地址")
    private String  downloadUrl;
    @ApiModelProperty(value="是否升级包")
    private String  ifUpgrade;
}
