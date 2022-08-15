package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EchoResult {

    @ApiModelProperty(value="当前服务器时间,yyyy-MM-dd HH:mm:ss")
    private String time;

}
