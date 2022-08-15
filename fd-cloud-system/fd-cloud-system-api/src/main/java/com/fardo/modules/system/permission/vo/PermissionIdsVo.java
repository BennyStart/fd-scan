package com.fardo.modules.system.permission.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @(#)PermissionIdsVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/7/23 8:45
 * 描　述：
 */
@Data
public class PermissionIdsVo {
    @ApiModelProperty(value = "菜单功能id集合")
    private List<String> permissionIds;
}
