package com.fardo.modules.system.role.model;

import lombok.Data;

/**
 * @(#)SysCustomDepartPermissionModel <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/22 15:51
 * 描　述：
 */
@Data
public class SysCustomDepartModel {
    /**
     * 数据权限范围编码
     */
    private String dataScope;
    /**
     * 部门id
     */
    private String departId;


    public SysCustomDepartModel(String dataScope, String departId) {
        this.dataScope = dataScope;
        this.departId = departId;
    }
}
