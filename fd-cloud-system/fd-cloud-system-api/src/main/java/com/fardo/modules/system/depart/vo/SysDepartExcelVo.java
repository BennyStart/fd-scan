package com.fardo.modules.system.depart.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @(#)SysDepartExcelVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/10/8 14:25
 * 描　述：
 */
@Data
public class SysDepartExcelVo {
    /**机构编码*/
    @Excel(name = "*组织机构编码", width = 15)
    private String orgCode;
    /**机构/部门名称*/
    @Excel(name = "*组织机构名称", width = 15)
    private String departName;
    /**部门简称*/
    @Excel(name = "*组织机构简称", width = 15)
    private String departNameAbbr;
    /**机构别名*/
    @Excel(name = "*组织机构别名", width = 15)
    private String departNameAlias;
    /**行政区划编码*/
    @Excel(name = "行政区划编码", width = 15)
    private String areaCode;
    @Excel(name = "上级机构编码", width = 15)
    private String parentId;
    /**机构类型*/
    @Excel(name = "机构类型", width = 15)
    private String orgType;
}
