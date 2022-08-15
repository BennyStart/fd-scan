package com.fardo.modules.system.personal.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "用户中心常用地址设置",description = "用户中心常用地址设置")
public class SysPersonalSettingDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 角色id
     */
    @NotBlank(message = "角色ID不能为空")
    @ApiModelProperty(value = "角色ID")
    private String userRole;

    /**
     * 部门id
     */
    @NotBlank(message = "部门ID不能为空")
    @ApiModelProperty(value = "部门ID")
    private String deptId;


    @Valid
    @ApiModelProperty(value = "地址list")
    private java.util.List<SysPersonalSettingListDTO> list;
}
