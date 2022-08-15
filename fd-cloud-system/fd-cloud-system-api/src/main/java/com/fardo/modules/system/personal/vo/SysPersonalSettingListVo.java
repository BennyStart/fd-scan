package com.fardo.modules.system.personal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户中心常用地址设置list",description = "用户中心常用地址设置list")
public class SysPersonalSettingListVo {

    private static final long serialVersionUID = 1L;

    /**
     * id新增为空,修改不能为空
     */
    @ApiModelProperty(value = "id新增为空,修改不能为空")
    private String id;

    /**
     * 常用地点设置
     */
    @ApiModelProperty(value = "常用地点设置")
    private String address;

    /**
     * 默认类型：0为默认,1非默认
     */
    @ApiModelProperty(value = "默认类型：0为默认,1非默认")
    private String typeSort;

    @ApiModelProperty(value = "排序")
    private String sort;

}
