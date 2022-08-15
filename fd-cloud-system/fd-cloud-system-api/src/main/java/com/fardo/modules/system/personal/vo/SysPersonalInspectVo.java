package com.fardo.modules.system.personal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 人员核查
 *
 * @author guohh
 * @date 2021/8/31-14:33
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Data
@ApiModel(value = "人员核查", description = "人员核查")
public class SysPersonalInspectVo {

    @ApiModelProperty(value = "证件类型")
    private String zjlx;
    @ApiModelProperty(value = "身份证号")
    private String sfzh;
    @ApiModelProperty(value = "姓名")
    private String xm;
    @ApiModelProperty(value = "性别")
    private String xb;
    @ApiModelProperty(value = "出生日期")
    private String csrq;
    @ApiModelProperty(value = "民族")
    private String mz;
    @ApiModelProperty(value = "住址")
    private String zz;
}
