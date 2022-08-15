package com.fardo.modules.system.role.enums;

import com.fardo.common.api.vo.IResultCode;

public enum RoleResultCodeEnum implements IResultCode {

    ROLE_CODE_EXIST("101", "角色编码已存在"),
    ROLE_NAME_EXIST("102", "角色名称已存在");

    private String resultCode;
    private String resultMsg;

    RoleResultCodeEnum(String resultCode, String resultMsg){
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
