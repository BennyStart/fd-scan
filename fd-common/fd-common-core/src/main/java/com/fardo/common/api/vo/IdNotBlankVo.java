/**
 * IdNotBlankVo;
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：luyf
 * 时　间：2021/12/28
 * 描　述：创建
 */
package com.fardo.common.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 作者:luyf
 * 文件名:IdNotBlankVo
 * 版本号:1.0
 * 创建日期:2021/12/28
 * 描述:IdVo 且限制不能为空
 */
@Data
public class IdNotBlankVo {

    @NotBlank(message = "主键id不能为空")
    @ApiModelProperty("主键id")
    private String id;
}
