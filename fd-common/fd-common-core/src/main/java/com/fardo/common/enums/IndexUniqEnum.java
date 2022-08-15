package com.fardo.common.enums;

/**
 * @(#)IndexUniqEnum <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/4/14 15:47
 * 描　述：
 */
public enum IndexUniqEnum {

    un_sys_user_username("un_sys_user_username", "用户名"),
    un_sys_user_idcard("un_sys_user_idcard", "身份证号"),
    un_sys_user_police_no("un_sys_user_police_no", "警号"),
    un_sys_role_role_code("un_sys_role_role_code", "角色编码"),
    un_sys_role_role_name("un_sys_role_role_name", "角色名称"),
    ;

    IndexUniqEnum(String uniqKey, String uniqText) {
        this.uniqKey = uniqKey;
        this.uniqText = uniqText;
    }

    private String uniqKey;
    private String uniqText;

    public String getUniqKey() {
        return uniqKey;
    }

    public String getUniqText() {
        return uniqText;
    }
}
