package com.fardo.modules.system.log.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.util.StringUtil;
import lombok.Data;

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
@TableName("t_sys_user_log")
public class SysUserLogEntity {

    /**
     * 请求来源appkey
     */
    private String appKey;
    /**
     * 花费时间
     */
    private Long costTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 备用字段1
     */
    private String data1;
    /**
     * 备用字段2
     */
    private String data2;
    /**
     * 备用字段3
     */
    private String data3;
    /**
     * 部门id
     */
    private String departId;
    /**
     * 错误代码
     */
    private String errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * 主键id
     */
    private String id;
    /**
     * 操作描述
     */
    private String operDesc;
    /**
     * 操作名称
     */
    private String operName;
    /**
     * 操作结果，0：成功，1：失败
     */
    private String operResult;
    /**
     * 操作类型
     */
    private String operType;
    /**
     * 请求ip
     */
    private String requestIp;
    /**
     * 请求接口参数
     */
    private String requestParam;
    /**
     * 请求接口地址
     */
    private String requestUrl;
    /**
     * 用户信息
     */
    private String userInfo;
    /**
     * 版本号
     */
    private String version;
    /**
     * 操作模块
     */
    private String operModule;

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        if(StringUtil.isNotBlank(data1) && data1.length() > 2000) {
            this.data1 = data1.substring(0, 2000);
        } else {
            this.data1 = data1;
        }
    }

    public String getOperDesc() {
        return operDesc;
    }

    public void setOperDesc(String operDesc) {
        if(StringUtil.isNotBlank(operDesc) && operDesc.length() > 2000) {
            this.operDesc = operDesc.substring(0, 2000);
        } else {
            this.operDesc = operDesc;
        }
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        if(StringUtil.isNotBlank(requestParam) && requestParam.length() > 2000) {
            this.requestParam = requestParam.substring(0, 2000);
        } else {
            this.requestParam = requestParam;
        }
    }

}
