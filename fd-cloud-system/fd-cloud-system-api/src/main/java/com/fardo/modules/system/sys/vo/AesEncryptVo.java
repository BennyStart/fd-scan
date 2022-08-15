package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value="aes加密请求对象", description="aes加密请求对象")
public class AesEncryptVo {

    @NotBlank(message="待加密数据不能为空")
    @ApiModelProperty(value="待加密数据")
    private String data;

    @ApiModelProperty(value="密钥")
    private String apiKey;

}
