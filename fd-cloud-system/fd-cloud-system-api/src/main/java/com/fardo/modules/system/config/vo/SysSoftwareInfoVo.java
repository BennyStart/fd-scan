package com.fardo.modules.system.config.vo;

import com.fardo.common.system.vo.PageVo;
import com.fardo.modules.system.config.entity.SysSoftwareDownloadEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel(value="软件下载下载配置参数", description="软件下载下载配置条件查询参数")
@Data
public class SysSoftwareInfoVo extends PageVo {

    @ApiModelProperty(value="主键")
    private String id;
    @NotBlank(message = "软件名称不能为空")
    @ApiModelProperty(value="软件名称")
    private String  softwareName ;
    @NotBlank(message = "软件类型不能为空")
    @ApiModelProperty(value="软件类型")
    private String  softwareType;
    @NotBlank(message = "软件版本不能为空")
    @ApiModelProperty(value="软件版本")
    private String  softwareVersion;
    @NotBlank(message = "软件版本不能为空")
    @ApiModelProperty(value="软件版本变化说明")
    private String  versionChangeExplain;
    @ApiModelProperty(value="发布时间 yyyymmdd")
    private String  releaseDate;

    @ApiModelProperty(value = "软件下载地址列表")
    private List<SysSoftwareDownloadEntity> softDownList;

}
