package com.fardo.common.enums;

/**
 * @(#)OperTypeEnum <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/14 15:14
 * 描　述：
 */
public enum OperTypeEnum {
    NULL(),
    YHDL("1001","用户登录","登录登出","0","1","系统-用户表"),
    YHDC("1003","用户登出","登录登出","5","1","系统-用户表"),
    XZBL("2001","新增笔录","笔录管理","2","3","笔录-笔录表"),
    XGBL("2002","修改笔录","笔录管理","3","3","笔录-笔录表"),
    CKBL("2003","查看笔录","笔录管理","1","3","笔录-笔录表"),
    SCBL("2004","删除笔录","笔录管理","4","3","笔录-笔录表"),
    CXBL("2005","查询笔录","笔录管理","1","3","笔录-笔录表"),
    DCBL("2006","导出笔录","笔录管理","1","3","笔录-笔录表"),
    DYBL("2007","打印笔录","笔录管理","1","3","笔录-笔录表"),
    BLYJ("2008","笔录移交","笔录管理","3","3","笔录-笔录表"),
    BLYJSP("2009","笔录移交审批","笔录管理","3","3","笔录-笔录表"),
    BLSQ("2010","笔录授权","笔录管理","3","3","笔录-笔录表"),
    BLQZNY("2011","笔录签字捺印","笔录管理","3","3","笔录-笔录表"),
    // BRWSQZNY("2012","辨认文书签字捺印","笔录管理","3","3","笔录-笔录表"),
    // XZBRZP("3001","新增辨认照片","辨认照片管理","2","4","笔录-辨认照片表"),
    // XGBRZP("3002","修改辨认照片","辨认照片管理","3","4","笔录-辨认照片表"),
    // SCBRZP("3003","删除辨认照片","辨认照片管理","4","4","笔录-辨认照片表"),
    XZGGMB("3010","新增公共模板","模板管理","2","3","笔录-模板表"),
    XGGGMB("3011","修改公共模板","模板管理","3","3","笔录-模板表"),
    SCGGMB("3012","删除公共模板","模板管理","4","3","笔录-模板表"),
    XZGG("3020","新增公告","公告管理","2","3","笔录-公告表"),
    XGGG("3021","修改公告","公告管理","2","3","笔录-公告表"),
    SCGG("3022","删除公告","公告管理","2","3","笔录-公告表"),
    XZYH("1010","新增用户","用户管理","2","1","系统-用户表"),
    XGYH("1011","修改用户","用户管理","3","1","系统-用户表"),
    SCYH("1012","删除用户","用户管理","4","1","系统-用户表"),
    XZZZJG("1020","新增组织机构","机构管理","2","6","系统-机构表"),
    XGZZJG("1021","修改组织机构","机构管理","3","6","系统-机构表"),
    SCZZJG("1022","删除组织机构","机构管理","4","6","系统-机构表"),
    XZAJ("4001","新增案件","案件管理","2","3","笔录-案件表"),
    XGAJ("4002","修改案件","案件管理","3","3","笔录-案件表"),
    SCAJ("4003","删除案件","案件管理","4","3","笔录-案件表"),
    ;

    // 操作类型
    private String operType;
    // 操作名称
    private String operName;
    // 功能模块
    private String operModule;
    // 操作种类，用户具体操作类型代表，0：登录；1：查询；2：新增；3：修改；4：删除
    private String operateType;
    //日志对象类型,1:人,2:车,3:案件,4:物品,5:线索,6:组织,7:全文检索,99:其它
    private String resourceType;
    //日志对象名称,比如“暂住人口表”
    private String resourceName;

    OperTypeEnum() {
    }

    OperTypeEnum(String operType, String operName, String operModule, String operateType, String resourceType, String resourceName) {
        this.operType = operType;
        this.operName = operName;
        this.operModule = operModule;
        this.operateType = operateType;
        this.resourceType = resourceType;
        this.resourceName = resourceName;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getOperModule() {
        return operModule;
    }

    public void setOperModule(String operModule) {
        this.operModule = operModule;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
