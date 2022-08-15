package com.fardo.common.api.vo;

import com.fardo.common.enums.OperTypeEnum;
import lombok.Data;

/**
 * @(#)AspectLogVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/9/16 8:44
 * 描　述：接口切面，保存日志所需数据
 */
@Data
public class AspectLogVo {

    public final static String SEPARATOR = "，";

    /**
     * 操作描述
     */
    private String operDesc;
    /**
     * 操作结果数据
     */
    private String resultData;
    /**
     * 操作类型
     */
    private OperTypeEnum operTypeEnum;

    public AspectLogVo() {
    }

    public AspectLogVo(String operDesc) {
        this.operDesc = operDesc;
    }

    public AspectLogVo(String operDesc, String resultData) {
        this.operDesc = operDesc;
        this.resultData = resultData;
    }

    public AspectLogVo(String operDesc, String resultData, OperTypeEnum operTypeEnum) {
        this.operDesc = operDesc;
        this.resultData = resultData;
        this.operTypeEnum = operTypeEnum;
    }

    public String getOperDesc() {
        return operDesc;
    }

    public void setOperDesc(String operDesc) {
        this.operDesc = operDesc;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public OperTypeEnum getOperTypeEnum() {
        return operTypeEnum;
    }

    public void setOperTypeEnum(OperTypeEnum operTypeEnum) {
        this.operTypeEnum = operTypeEnum;
    }
}
