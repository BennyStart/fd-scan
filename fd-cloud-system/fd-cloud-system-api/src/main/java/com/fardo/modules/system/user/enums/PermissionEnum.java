package com.fardo.modules.system.user.enums;

/**
 * @(#)PermissionEnum <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/4/13 19:28
 * 描　述：
 */
public enum PermissionEnum {

    talkRule("talk/talkRule","谈话规则设置"),
    userList("user/userList","用户列表"),
    orgManager("user/orgManager","组织机构"),
    roleManager("user/roleManager","角色管理"),
    areaManage("user/areaManage","监区室管理"),
    detaineesManager("user/detaineesManager","在押人员"),
    softDownload("sysSetting/download","软件下载配置"),
    softUpgrade("sysSetting/upgrade","升级策略配置"),
    sysSetting("sysSetting/system","系统参数配置"),
    ;
    private String code;

    private String text;

    PermissionEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
