package com.fardo.modules.system.personal.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class BlQualitysetUserDTO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "检验方式:0民警确认、1每份笔录仅校验一次、2每次自动校验")
    private String jyfs;

    @ApiModelProperty(value = "默认类型：是否开启：0不开启,1开启")
    private String type;
}
