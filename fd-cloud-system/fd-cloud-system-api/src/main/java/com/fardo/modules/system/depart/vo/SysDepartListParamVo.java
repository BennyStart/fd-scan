package com.fardo.modules.system.depart.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)SysDepartVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/12 14:30
 * 描　述：
 */
@Data
@ApiModel(value="机构树查询条件")
public class SysDepartListParamVo {
    @ApiModelProperty(value="机构id")
    private String id;
    @ApiModelProperty(value="等级")
    private Integer level;
    @ApiModelProperty("台州部门原始区域代码")
    private String originalAreaCode;
}
