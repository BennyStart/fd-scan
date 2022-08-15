package com.fardo.modules.system.personal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户中心常用地址设置", description = "用户中心常用地址设置")
public class SysPersonalSettingVo {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色ID")
    private String userRole;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门ID")
    private String deptId;

    @ApiModelProperty(value = "地址list")
    private java.util.List<SysPersonalSettingListVo> list;
}
