package com.fardo.modules.system.personal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/8/31-14:33
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Data
@ApiModel(value = "用户默认地址",description = "用户默认地址")
public class SysPersonalInfoVo {

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "角色id")
    private String userRole;

    @ApiModelProperty(value = "默认地点")
    private String address;

    @ApiModelProperty(value = "默认类型：0为默认，1为非默认")
    private String type;

    @ApiModelProperty(value = "部门id")
    private String deptId;
}
