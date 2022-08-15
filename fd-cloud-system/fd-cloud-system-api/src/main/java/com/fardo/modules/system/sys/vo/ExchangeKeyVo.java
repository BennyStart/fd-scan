package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="协商对等密钥请求对象", description="协商对等密钥请求对象")
public class ExchangeKeyVo {

    @ApiModelProperty(value="客户端随机生成的8个字节的随机串，使用RSA的公钥进行加密")
    private String randomKey;

    @ApiModelProperty(value="为了方便测试人员使用时，可不需加密，直接给8个字节的随机串，此时需传递0；1为RSA的公钥加密，0为不加密码，默认为1")
    private String randomKeyType;

}
