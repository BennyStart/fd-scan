package com.fardo.modules.system.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("文件上传参数")
@Data
public class UploadFilesVo {
    @NotNull
    @ApiModelProperty(value = "文件存储key")
    private String fileUrl;
   /* @ApiModelProperty(value = "文件名称")
    private String fileName;*/
}
