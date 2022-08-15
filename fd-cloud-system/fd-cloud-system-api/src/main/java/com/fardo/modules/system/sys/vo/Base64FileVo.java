package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Base64FileVo {
    @NotBlank(message = "文件内容不能为空")
    @ApiModelProperty(value="文件base64字符串")
    private String fileContent;
    @NotBlank(message = "文件名不能为空")
    @ApiModelProperty(value="文件名")
    private String fileName;

}
