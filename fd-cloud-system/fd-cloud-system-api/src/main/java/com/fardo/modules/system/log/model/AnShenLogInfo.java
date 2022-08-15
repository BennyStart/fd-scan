package com.fardo.modules.system.log.model;

import lombok.Data;

/**
 * @(#)AnShenLogInfo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/15 10:03
 * 描　述：
 */
@Data
public class AnShenLogInfo {

    // 操作种类，用户具体操作类型代表，0：登录；1：查询；2：新增；3：修改；4：删除
    private String operateType;
    //日志对象类型,1:人,2:车,3:案件,4:物品,5:线索,6:组织,7:全文检索,99:其它
    private String resourceType;
    //日志对象名称,比如“暂住人口表”
    private String resourceName;

    public AnShenLogInfo() {
    }

    public AnShenLogInfo(String operateType, String resourceType, String resourceName) {
        this.operateType = operateType;
        this.resourceType = resourceType;
        this.resourceName = resourceName;
    }
}
