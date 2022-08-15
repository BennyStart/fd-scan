package com.fardo.modules.system.permission.enums;

/**
 * @(#)PermissionEnum <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/18 14:20
 * 描　述：
 */
public enum PermissionTypeEnum {
    LEVEL_1("0","一级菜单"),
    LEVEL_2("1","子菜单"),
    BUTTON("2","功能按钮"),
    ;
    private String code;

    private String name;

    PermissionTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
