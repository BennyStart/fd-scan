package com.fardo.modules.system.log.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @(#)SysUserLogEntity <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/7/22 9:17
 * 描　述：
 */
@Data
public class UserLogExportVo {

    @Excel(name = "用户登录名")
    private String username;
    @Excel(name = "操作结果")
    private String operResult;
    @Excel(name = "操作类型")
    private String operType;
    @Excel(name = "操作描述")
    private String operDesc;
    @Excel(name = "结果数据")
    private String data1;
    @Excel(name = "操作时间", format = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
    @Excel(name = "操作IP地址")
    private String requestIp;
    @Excel(name = "部门名称")
    private String departName;
    @Excel(name = "错误码")
    private String errorCode;
    @Excel(name = "错误信息")
    private String errorMsg;
    @Excel(name = "操作名称")
    private String operName;
    @Excel(name = "操作者名称")
    private String xm;
    @Excel(name = "执法证号 ")
    private String polieNo;
    @Excel(name = "操作者身份证号码")
    private String idCard;
    @Excel(name = "部门编码")
    private String departCode;
    @Excel(name = "操作条件")
    private String requestParam;
    @Excel(name = "操作总用时(ms)")
    private Long costTime;
    @Excel(name = "版本号")
    private String version;
    @Excel(name = "操作类型_安审")
    private String operateType;
    @Excel(name = "资源类型")
    private String resourceType;
    @Excel(name = "资源名称")
    private String resourceName;



}
