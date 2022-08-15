package com.fardo.modules.system.config.vo;

import com.fardo.common.system.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(value="升级策略配置条件查询参数", description="软件下载下载配置条件查询参数")
@Data
public class SysSoftwareUpgradeStrategyVo extends PageVo {


    @ApiModelProperty(value="主键")
    private String id;

    @NotBlank(message = "软件名称不能为空")
    @ApiModelProperty(value="软件名称")
    private String softwareName;
    @NotBlank(message = "软件类型不能为空")
    @ApiModelProperty(value="软件类型")
    private String softwareType;

    @ApiModelProperty(value="软件起始版本")
    private String softwareVersionStart;

    @ApiModelProperty(value="软件结束版本")
    private String softwareVersionEnd;

    @ApiModelProperty(value="服务器软件起始版本")
    private String serverSoftwareVersionStart;

    @ApiModelProperty(value="服务器软件结束版本")
    private String serverSoftwareVersionEnd;

    @ApiModelProperty(value="行政区划")
    private String area;

    @ApiModelProperty(value="操作系统版本")
    private String systemVersion;

    @ApiModelProperty(value="操作系统位数")
    private String systemBit;

    @ApiModelProperty(value="强制升级标志")
    private String forceFlag;

    @NotBlank(message = "升级软件版本不能为空")
    @ApiModelProperty(value="升级软件版本")
    private String upgradeSoftwareVersion;

    @ApiModelProperty(value="升级软件包url")
    private String upgradeSoftwareUrl;

    @ApiModelProperty(value="备注")
    private String remark;



}
