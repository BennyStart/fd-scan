package com.fardo.modules.system.depart.enums;

import com.fardo.common.api.vo.IResultCode;

public enum DepartResultCodeEnum implements IResultCode {

    ORGCODE_EXIST("101", "机构代码已存在"),
    PARENT_NO_EXIST("102", "上级单位不存在"),
    PARENT_NO_BE_SELF("103", "上级单位不能是自身"),
    PARENT_NO_BE_CHIRLDREN("104", "上级单位不能是自己的下级"),
    DELETE_FAIL_USER_EXIST("105", "该组织机构挂有用户，无法删除"),
    DELETE_FAIL_CHIRLDREN_EXIST("106", "该组织机构存在下级机构，无法删除"),
    ;

    private String resultCode;
    private String resultMsg;

    DepartResultCodeEnum(String resultCode, String resultMsg){
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    /**
     * @return the resultCode
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * @param resultCode the resultCode to set
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * @return the resultMsg
     */
    public String getResultMsg() {
        return resultMsg;
    }

    /**
     * @param resultMsg the resultMsg to set
     */
    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

}
