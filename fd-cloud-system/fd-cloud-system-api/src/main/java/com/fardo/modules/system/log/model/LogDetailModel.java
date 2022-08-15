package com.fardo.modules.system.log.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)LogDetailModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/18 15:10
 * 描　述：
 */
@Data
public class LogDetailModel {

    @ApiModelProperty("用户登录名")
    private String username;
    @ApiModelProperty("操作部门")
    private String departName;
    @ApiModelProperty("操作类型编码")
    private String operType;
    @ApiModelProperty("操作类型名称")
    private String operName;
    @ApiModelProperty("操作结果")
    private String operResult;
    @ApiModelProperty("操作时间")
    private String createTime;
    @ApiModelProperty("操作ip地址")
    private String requestIp;
    @ApiModelProperty("操作描述")
    private String operDesc;
    @ApiModelProperty("操作结果数据")
    private String resultData;

}
