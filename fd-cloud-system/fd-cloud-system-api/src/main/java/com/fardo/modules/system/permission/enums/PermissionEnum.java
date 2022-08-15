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
public enum PermissionEnum {
    ;
    private String code;

    private String name;

    PermissionEnum(String code, String name) {
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
