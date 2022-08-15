package com.fardo.modules.system.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @(#)LockUserVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/18 16:27
 * 描　述：
 */
@Data
@ApiModel("锁定用户参数")
public class LockUserVo {

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value="用户id",name="userId")
    private String userId;

    @NotBlank(message = "状态不能为空")
    @ApiModelProperty(value="状态(1正常, 2锁定)",name="status")
    private String status;
}
