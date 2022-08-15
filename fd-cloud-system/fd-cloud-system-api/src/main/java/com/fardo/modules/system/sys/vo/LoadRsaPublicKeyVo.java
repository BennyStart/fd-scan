package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="获取服务端公钥请求对象", description="获取服务端公钥请求对象")
public class LoadRsaPublicKeyVo {

    @ApiModelProperty(value="默认为netFormat")
    private String type;

    @ApiModelProperty(value="新版本标志位，1-新版本")
    private String newVersionFlag;

}
