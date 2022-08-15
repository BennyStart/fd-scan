package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)IndexInfoVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/24 14:38
 * 描　述：
 */
@Data
@ApiModel("首页信息")
public class IndexInfoVo {
    @ApiModelProperty("平台名称")
    private String systemName;
    @ApiModelProperty("联系电话")
    private String phone;
    @ApiModelProperty("版本号")
    private String version;
}
