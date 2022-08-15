package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EncryptResult {

    @ApiModelProperty(value="加密后数据")
    private String data;

}
