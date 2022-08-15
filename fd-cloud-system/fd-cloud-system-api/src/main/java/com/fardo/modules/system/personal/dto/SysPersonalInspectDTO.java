package com.fardo.modules.system.personal.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

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
public class SysPersonalInspectDTO {

    @NotBlank(message = "公民身份证号码不能为空")
    @ApiModelProperty(value = "公民身份证号码")
    private String sfzh;

    // @NotBlank(message = "常住人口姓名不能为空")
    @ApiModelProperty(value = "常住人口姓名")
    private String xm;

}
