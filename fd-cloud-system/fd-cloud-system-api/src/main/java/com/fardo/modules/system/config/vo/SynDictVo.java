package com.fardo.modules.system.config.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SynDictVo {

    @NotBlank(message = "字典编码不能为空")
    @ApiModelProperty(value="字典编码")
    private String dictCode;

}
