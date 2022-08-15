package com.fardo.modules.system.config.model;

import com.fardo.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("软件升级策略信息")
public class SysSoftwareUpgradeStrategyModel implements Serializable {

    private static final long serialVersionUID = 6943070787985246507L;
    @ApiModelProperty(value="主键")
    private String id;

    @ApiModelProperty(value="软件名称")
    @Dict(dicCode = "soft_name")
    private String softwareName;

    @ApiModelProperty(value="软件类型")
    @Dict(dicCode = "soft_type")
    private String softwareType;

    @ApiModelProperty(value="软件起始版本")
    private String softwareVersionStart;

    @ApiModelProperty(value="软件结束版本")
    private String softwareVersionEnd;

    @ApiModelProperty(value="服务器软件起始版本")
    private String serverSoftwareVersionStart;

    @ApiModelProperty(value="服务器软件结束版本")
    private String serverSoftwareVersionEnd;

    @ApiModelProperty(value="行政区划id")
    @Dict(dictTable ="t_sys_area",dicText = "name",dicCode = "id")
    private String area;

    @ApiModelProperty(value="操作系统版本")
    private String systemVersion;

    @ApiModelProperty(value="操作系统位数")
    @Dict(dicCode = "operation_system_bit")
    private String systemBit;

    @ApiModelProperty(value="强制升级标志")
    private String forceFlag;

    @ApiModelProperty(value="升级软件版本")
    private String upgradeSoftwareVersion;

    @ApiModelProperty(value="升级软件包url")
    private String upgradeSoftwareUrl;

    @ApiModelProperty(value="备注")
    private String remark;

}
