package com.fardo.modules.system.config.vo;

import com.fardo.common.system.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/7/15-8:59
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Data
@ApiModel(value = "系统参数配置接收参数", description = "系统参数配置接收参数")
public class SysConfigListVo extends PageVo {

    @ApiModelProperty(value = "参数名称")
    private String name;

    @ApiModelProperty(value = "参数配置")
    private String code;

    @ApiModelProperty(value = "参数描述")
    private String remark;
}
