package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoadRSAPublicKeyResult {

    @ApiModelProperty(value="公钥")
    private String rsaPublicKey;
    @ApiModelProperty(value="加密类型")
    private String encryptType;

}
