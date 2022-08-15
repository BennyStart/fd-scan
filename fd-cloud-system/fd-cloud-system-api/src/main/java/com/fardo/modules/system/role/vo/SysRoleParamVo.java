package com.fardo.modules.system.role.vo;

import com.fardo.common.system.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @(#)SysRoleParamVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/18 19:59
 * 描　述：
 */
@ApiModel("角色列表查询条件")
@Data
public class SysRoleParamVo extends PageVo {

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "数据权限范围")
    private String dataAuthority;
}
