package com.fardo.modules.system.log.vo;

import com.fardo.common.system.vo.PageVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)LogQueryVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/18 13:59
 * 描　述：
 */
@Data
public class LogQueryVo extends PageVo {

    @ApiModelProperty("开始时间，格式yyyyMMddHHmmss")
    private String startTime;
    @ApiModelProperty("结束时间，格式yyyyMMddHHmmss")
    private String endTime;
    @ApiModelProperty("姓名")
    private String xm;
    @ApiModelProperty("身份证号")
    private String idCard;
    @ApiModelProperty("操作ip")
    private String requestIp;
    @ApiModelProperty("操作部门")
    private String departName;
    @ApiModelProperty("操作类型，下拉框获取请求/api/system/log/getComboBox")
    private String operType;
    @ApiModelProperty("所属模块，下拉框获取请求/api/system/log/getComboBox")
    private String operModule;
    @ApiModelProperty("手机号")
    private String phone;

}
