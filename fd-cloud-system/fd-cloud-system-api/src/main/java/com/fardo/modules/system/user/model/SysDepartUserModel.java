package com.fardo.modules.system.user.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)SysDepartUserModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/13 11:11
 * 描　述：
 */
@Data
public class SysDepartUserModel {

    public static final String TYPE_RY = "ry";
    public static final String TYPE_BM = "bm";


    @ApiModelProperty("部门id或者人员id")
    private String id;
    @ApiModelProperty("部门名称或者人员名称")
    private String name;
    @ApiModelProperty("类型（ry-人员，bm-部门）")
    private String type;

    public SysDepartUserModel(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
