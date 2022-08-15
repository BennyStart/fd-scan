package com.fardo.modules.system.log.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @(#)LogComboModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/18 14:20
 * 描　述：
 */
@Data
public class LogComboModel {

    @ApiModelProperty("操作类型")
    private List<ComboBoxModel> operTypes;
    @ApiModelProperty("所属模板")
    private List<ComboBoxModel> operModule;

}
