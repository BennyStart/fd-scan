package com.fardo.modules.system.personal.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/10/19-15:28
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Data
@ApiModel(value = "文书校验",description = "文书校验")
public class BlQualitysetUserInfoDTO {

    @NotBlank(message = "userId不能为空")
    @ApiModelProperty(value = "userId")
    private String userId;
}
