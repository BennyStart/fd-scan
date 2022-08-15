package com.fardo.modules.system.config.model;

import com.fardo.common.aspect.annotation.Dict;
import com.fardo.modules.system.config.entity.SysSoftwareDownloadEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("软件下载配置信息")
public class SysSoftwareInfoModel implements Serializable {
    private static final long serialVersionUID = -2248897992518993296L;
    @ApiModelProperty(value="主键")
    private String id;
    @Dict(dicCode = "soft_name")
    @ApiModelProperty(value="软件名称")
    private String  softwareName;
    @Dict(dicCode = "soft_type")
    @ApiModelProperty(value="软件类型")
    private String  softwareType;
    @ApiModelProperty(value="软件版本")
    private String  softwareVersion;
    @ApiModelProperty(value="软件版本变化说明")
    private String  versionChangeExplain;
    @ApiModelProperty(value="发布时间 yyyymmdd")
    private String  releaseDate;

    private List<SysSoftwareDownloadEntity> softDownList;

}
