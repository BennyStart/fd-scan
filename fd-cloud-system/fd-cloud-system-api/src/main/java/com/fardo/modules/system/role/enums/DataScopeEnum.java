package com.fardo.modules.system.role.enums;

/**
 * @(#)DataScopEnum <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/23 9:36
 * 描　述：数据权限枚举类
 */
public enum DataScopeEnum {
    /** 本单位数据*/
    DEPART("2"),
    /** 自定义单位数据*/
    CUSTOM("3"),
    /** 本单位及以下单位数据*/
    DEPART_WITH_SUB("5"),
    /** 默认数据（本人）*/
    DEFAULT("6");

    private String code;

    DataScopeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
