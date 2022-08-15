package com.fardo.modules.system.config.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpgradeQueryResult {

    @ApiModelProperty(value="强制升级标志")
    private String forceFlag;
    @ApiModelProperty(value="升级软件版本")
    private String upgradeVer;
    @ApiModelProperty(value="软件版本变化说明")
    private String softVerChange;
    private String upgradeFlag;
    @ApiModelProperty(value="升级软件包url")
    private String upgradeUrl;

}
