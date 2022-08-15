package com.fardo.modules.system.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserLikeVo {

    @ApiModelProperty(value="警号、姓名或单位的首字母、文字")
    private String condition;


}
