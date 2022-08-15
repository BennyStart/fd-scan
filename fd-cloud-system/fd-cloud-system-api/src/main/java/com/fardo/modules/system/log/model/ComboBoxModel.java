package com.fardo.modules.system.log.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)ComboBoxModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/18 14:23
 * 描　述：
 */
@Data
public class ComboBoxModel {

    /**
     * 下拉框值
     */
    @ApiModelProperty("下拉框值")
    private String value;
    /**
     * 下拉框名称
     */
    @ApiModelProperty("下拉框名称")
    private String name;

    public ComboBoxModel() {
    }

    public ComboBoxModel(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
