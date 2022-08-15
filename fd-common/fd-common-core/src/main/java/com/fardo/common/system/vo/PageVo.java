package com.fardo.common.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)PageVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/16 9:45
 * 描　述：
 */
@Data
public class PageVo {
    @ApiModelProperty(value="页码",example = "1")
    private Integer pageNo = 1;
    @ApiModelProperty(value="每页记录数", example = "10")
    private Integer pageSize = 10;
}
