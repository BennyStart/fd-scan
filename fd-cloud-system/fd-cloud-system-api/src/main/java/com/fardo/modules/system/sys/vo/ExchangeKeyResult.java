package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)ExchangeKeyResult <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/12 10:27
 * 描　述：
 */
@Data
public class ExchangeKeyResult {
    @ApiModelProperty(value="会话id")
    private String sid;
}
