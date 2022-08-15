package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileServerVo {

    @ApiModelProperty(value="文件唯一标识")
    private String fileId;

}
